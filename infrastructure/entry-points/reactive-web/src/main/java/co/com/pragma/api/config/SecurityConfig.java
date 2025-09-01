package co.com.pragma.api.config;

import co.com.pragma.api.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/usuarios").hasAnyAuthority("ROLE_ADMIN","ROLE_ASESOR")
                        .pathMatchers(HttpMethod.GET, "/api/v1/usuarios/validate/**").hasAuthority("ROLE_CLIENT")
                        .pathMatchers(HttpMethod.GET, "/api/v1/usuarios/**").hasAnyAuthority("ROLE_ADMIN","ROLE_ASESOR","ROLE_CLIENT")
                        .pathMatchers(HttpMethod.POST, "/api/v1/solicitudes").hasAuthority("ROLE_CLIENT")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }

    private AuthenticationWebFilter jwtAuthenticationFilter() {
        ReactiveAuthenticationManager manager = authentication -> Mono.just(authentication);

        AuthenticationWebFilter filter = new AuthenticationWebFilter(manager);
        filter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.anyExchange());

        filter.setServerAuthenticationConverter(exchange -> {
            List<String> authHeaders = exchange.getRequest().getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
            if (authHeaders.isEmpty()) return Mono.empty();
            String authHeader = authHeaders.get(0);
            if (!authHeader.startsWith("Bearer ")) return Mono.empty();

            String token = authHeader.substring(7);
            try {
                Jws<Claims> claimsJws = jwtUtil.validateToken(token);
                Claims claims = claimsJws.getBody();
                String email = claims.getSubject();
                String role = claims.get("role", String.class);

                List<SimpleGrantedAuthority> authorities = Arrays.stream(role.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                return Mono.just(new UsernamePasswordAuthenticationToken(email, null, authorities));
            } catch (Exception e) {
                return Mono.empty();
            }
        });

        return filter;
    }
}
