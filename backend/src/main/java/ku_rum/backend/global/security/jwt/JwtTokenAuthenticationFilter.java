package ku_rum.backend.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ku_rum.backend.global.config.redis.RedisUtil;
import ku_rum.backend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtTokenAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            String token = resolveToken((HttpServletRequest) request);

            if (token != null && jwtTokenProvider.validateToken(token) && isNotLogout(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException e) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);
            httpServletResponse.setContentType("application/json;charset=UTF-8");

            Map<String, String> errors = new HashMap<>();
            errors.put("token", e.getMessage());

            BaseResponse<?> errorResponse = BaseResponse.of(HttpStatus.UNAUTHORIZED, errors);

            httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        }
    }

    private boolean isNotLogout(String accessToken) {
        String isLogout = redisUtil.getBlackList(accessToken);
        return isLogout.equals("false");
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
