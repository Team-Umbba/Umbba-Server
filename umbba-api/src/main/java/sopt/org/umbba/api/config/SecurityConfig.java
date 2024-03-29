package sopt.org.umbba.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import sopt.org.umbba.api.config.auth.CustomJwtAuthenticationEntryPoint;
import sopt.org.umbba.api.config.auth.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {
            "/kakao/**", "/login", "/reissue",
            "/qna/**", "onboard/**", "/home", "/dummy", "/user/me",
//          "/log-out",
            "/test", "/profile", "/health", "/actuator/health",
            "/alarm/qna", "/alarm/drink",
            "/demo/**",
            "/album/image"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .formLogin().disable() // Form Login 사용 X
                .httpBasic().disable() // HTTP Basic 사용 X
                .csrf().disable() // 쿠키 기반이 아닌 JWT 기반이므로 사용 X
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Spring Security 세션 정책 : 세션을 생성 및 사용하지 않음
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customJwtAuthenticationEntryPoint) // 에러 핸들링
                .and()
                .authorizeHttpRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터 적용
                .build();
    }

}