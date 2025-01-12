package ku_rum.backend.global.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import ku_rum.backend.global.config.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

import static java.util.stream.Collectors.joining;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String AUTHORITIES_KEY = "roles";

    private final JwtProperties jwtProperties;
    private final RedisUtil redisUtil;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        var secret = Base64.getEncoder()
                .encodeToString(this.jwtProperties.getSecret().getBytes());
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public TokenResponse createToken(Authentication authentication) {
        var claims = getClaims(authentication);
        
        Date now = new Date();
        Date accessValidity = new Date(getAccessValidTime(now));
        Date refreshValidity = new Date(getRefreshValidTime(now));

        String accessToken = createToken(claims, now, accessValidity);
        String refreshToken = createToken(claims, now, refreshValidity);

        redisUtil.setRedisData(String.valueOf(claims.get("userPK")), refreshToken);

        return TokenResponse.of(
                accessToken,
                refreshToken,
                this.jwtProperties.getAccessTokenValiditySeconds(),
                this.jwtProperties.getRefreshTokenValiditySeconds());
    }

    private long getRefreshValidTime(Date now) {
        return now.getTime() + this.jwtProperties.getRefreshTokenValiditySeconds();
    }

    private long getAccessValidTime(Date now) {
        return now.getTime() + this.jwtProperties.getAccessTokenValiditySeconds();
    }

    private String createToken(Claims claims, Date now, Date accessValidity) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(accessValidity)
                .signWith(this.secretKey, Jwts.SIG.HS256)
                .compact();
    }

    private Claims getClaims(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Collection<? extends GrantedAuthority> authorities = userDetails.getRoles();
        var claimsBuilder = Jwts.claims().add("userPK", userDetails.getUserId());

        if (!authorities.isEmpty()) {
            claimsBuilder.add(AUTHORITIES_KEY, authorities.stream()
                    .map(GrantedAuthority::getAuthority).collect(joining(",")));
        }
        return claimsBuilder.build();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token).getPayload();

        Long userId = claims.get("userPK", Long.class);
        Collection<? extends GrantedAuthority> roles = getGrantedAuthorities(claims);

        CustomUserDetails principal = CustomUserDetails.of(userId, "", roles, "");

        return new UsernamePasswordAuthenticationToken(principal, token, roles);
    }

    private static Collection<? extends GrantedAuthority> getGrantedAuthorities(Claims claims) {
        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        return authoritiesClaim == null
                ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parser().verifyWith(this.secretKey).build()
                .parseSignedClaims(token);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            if (e instanceof SecurityException) {
                log.debug("[SecurityException] 잘못된 토큰");
                throw new JwtException("[SecurityException] 잘못된 토큰입니다.");
            } else if (e instanceof MalformedJwtException) {
                log.debug("[MalformedJwtException] 잘못된 토큰");
                throw new JwtException("[MalformedJwtException] 잘못된 토큰입니다.");
            } else if (e instanceof ExpiredJwtException) {
                log.debug("[ExpiredJwtException] 토큰 만료");
                throw new JwtException("[ExpiredJwtException] 토큰 만료");
            } else if (e instanceof UnsupportedJwtException) {
                log.debug("[UnsupportedJwtException] 잘못된 형식의 토큰");
                throw new JwtException("[UnsupportedJwtException] 잘못된 형식의 토큰");
            } else if (e instanceof IllegalArgumentException) {
                log.debug("[IllegalArgumentException]");
                throw new JwtException("[IllegalArgumentException]");
            } else {
                log.debug("[토큰검증 오류]" + e.getClass());
                throw new JwtException("[토큰검증 오류] 미처리 토큰 오류");
            }
        }
    }

    public long getExpiredTime(String token) {
        return parseClaims(token).
                getPayload().
                getExpiration().
                getTime();
    }

    public Long getUserId(String token) {
        return parseClaims(token).getPayload().get("userPK", Long.class);
    }

}
