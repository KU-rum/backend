package ku_rum.backend.global.config;

import java.util.List;

public final class AuthorizationList {

    private static final List<String> AUTHORIZED_ENDPOINTS = List.of(
            "/",
            "/index.html",
            "/api/v1/auth/login",
            "/api/v1/users",
            "/api/v1/users/join",
            "/api/v1/users/weinlogin",
            "/api/v1/users/check-id",
            "/api/v1/users/check-nickname",
            "/api/v1/users/check-studentId",
            "/api/v1/users/validations",
            "/api/v1/users/validations/email",
            "/api/v1/mails/auth-codes",
            "/api/v1/mails/verification_codes",
            "/docs/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api/v1/users/loginId",
            "/api/v1/users/reset-account",
            "/actuator/**",
            "/api/v1/users/password-reset/initiate",
            "/api/v1/images/**",
            "/api/v1/notices/recent/5notices",
            "/api/v1/auth/**",
            "/api/v1/buildings/**",
            "/api/v1/departments/**",
            "/api/v1/colleges/**",
            "/api/v1/places",
            "/api/v1/places/**",
            "/api/v1/notices/**",
            "/api/v1/alarm/**",
            "/api/v1/user/**",
            "/api/v1/users/social"
    );

    private AuthorizationList() {
    }

    public static String[] getAuthorizedEndpoints() {
        return AUTHORIZED_ENDPOINTS.toArray(new String[0]);
    }

    public static boolean isPermitted(String path) {
        return AUTHORIZED_ENDPOINTS.stream().anyMatch(auth -> {
            if (auth.endsWith("/**")) {
                String prefix = auth.substring(0, auth.length() - 3);
                return path.startsWith(prefix);
            }
            return path.equals(pathNormalize(auth));
        });
    }

    private static String pathNormalize(String path) {
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }
}