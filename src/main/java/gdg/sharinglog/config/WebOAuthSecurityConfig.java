package gdg.sharinglog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
public class WebOAuthSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**", "/img/**", "/js/**", "/favicon.ico").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout.logoutSuccessUrl("/login"))
                // 모바일 클라이언트가 호출하는 JSON API(/api/**)는 CSRF 토큰을 따로 주고받지 않으므로 예외 처리.
                // 이게 없으면 세션 기반 로그인 상태에서도 POST /api/groups 같은 요청이 403으로 막힌다.
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                // 인증 안 된 /api/** 요청은 (기존처럼) /login으로 302 리다이렉트하지 않고 401 JSON으로 응답한다.
                // 리다이렉트는 브라우저 화면 이동을 전제로 하는 방식이라 JSON을 기대하는 클라이언트에는 맞지 않는다.
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                PathPatternRequestMatcher.withDefaults().matcher("/api/**")
                        )
                )
                .build();
    }
}
