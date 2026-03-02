package ku_rum.backend.global.security;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.EXPIRED_TOKEN;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.INVALID_TOKEN;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.JWT_ERROR;
import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.MALFORMED_TOKEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import ku_rum.backend.global.config.AuthorizationList;
import ku_rum.backend.global.exception.global.GlobalException;
import ku_rum.backend.global.support.response.BaseErrorResponse;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import ku_rum.backend.global.utill.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
@Component
public class JwtTokenAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            StringBuffer path = ((HttpServletRequest) request).getRequestURL();

            if (AuthorizationList.isPermitted(String.valueOf(path))) {
                chain.doFilter(request, response);
                return;
            }

            String token = resolveToken((HttpServletRequest) request);

            if (token != null && jwtTokenProvider.validateToken(token) && isNotLogout(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setContentType("application/json;charset=UTF-8");

            BaseExceptionResponseStatus status = resolveTokenException(e);
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);

            BaseErrorResponse errorResponse = new BaseErrorResponse(status);
            httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
        }
    }

    private BaseExceptionResponseStatus resolveTokenException(Exception e) {
        if (e instanceof GlobalException) {
            return ((GlobalException) e).getStatus();
        }
        if (e instanceof ExpiredJwtException) {
            return EXPIRED_TOKEN;
        }
        if (e instanceof MalformedJwtException) {
            return MALFORMED_TOKEN;
        }
        if (e instanceof UnsupportedJwtException || e instanceof IllegalArgumentException) {
            return INVALID_TOKEN;
        }
        return JWT_ERROR;
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
