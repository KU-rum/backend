package ku_rum.backend.global.security;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ku_rum.backend.global.config.AuthorizationList;
import ku_rum.backend.global.support.response.BaseErrorResponse;
import ku_rum.backend.global.support.status.BaseExceptionResponseStatus;
import ku_rum.backend.global.utill.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtTokenAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
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
        String message = e.getMessage();
        if (message == null) {
            return JWT_ERROR;
        }
        if (message.contains("ExpiredJwtException") || message.contains("만료")) {
            return EXPIRED_TOKEN;
        }
        if (message.contains("MalformedJwtException")) {
            return MALFORMED_TOKEN;
        }
        return INVALID_TOKEN;
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
