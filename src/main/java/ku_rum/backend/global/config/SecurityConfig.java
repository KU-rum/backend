package ku_rum.backend.global.config;

import ku_rum.backend.domain.oauth.application.CustomOAuth2UserService;
import ku_rum.backend.domain.oauth.handler.HttpCookieOAuth2AuthorizationRequestRepository;
import ku_rum.backend.domain.oauth.handler.OAuth2AuthenticationSuccessHandler;
import ku_rum.backend.domain.user.domain.repository.UserRepository;
import ku_rum.backend.global.security.CustomUserDetails;
import ku_rum.backend.global.security.JwtTokenAuthenticationFilter;
import ku_rum.backend.global.security.JwtTokenProvider;
import ku_rum.backend.global.utill.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static ku_rum.backend.global.support.status.BaseExceptionResponseStatus.NO_SUCH_USER;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS/CSRF/FormLogin/BasicAuth 설정
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 세션을 사용하지 않음
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 인증 실패 시 401만 던지도록 설정
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // 인가된 엔드포인트만 permit, 나머지는 인증 필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(AuthorizationList.getAuthorizedEndpoints()).permitAll()
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인 설정 (인증 성공/실패 핸들러)
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/oauth2/authorization")
                                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                        )
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        // OAuth 로그인 도중 에러 발생 시에도 401 반환
                        .failureHandler((request, response, exception) ->
                                response.sendError(HttpStatus.UNAUTHORIZED.value())
                        )
                )
                .oauth2Client(Customizer.withDefaults())

                // JWT 필터 등록
                .addFilterBefore(
                        new JwtTokenAuthenticationFilter(jwtTokenProvider, redisUtil),
                        UsernamePasswordAuthenticationFilter.class
                )
        ;

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return loginId -> userRepository.findUserByLoginId(loginId)
                .map(u -> CustomUserDetails.of(u.getId(),
                        u.getEmail(),
                        AuthorityUtils.createAuthorityList(u.getRoles().toArray(new String[0])),
                        u.getPassword(),
                        u.isFirstLogin()))
                .orElseThrow(() -> new UsernameNotFoundException(NO_SUCH_USER.getMessage()));
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));          // 또는 구체적 origin 목록
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
