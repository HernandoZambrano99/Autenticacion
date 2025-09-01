package co.com.pragma.api.security;

import co.com.pragma.model.user.gateways.JwtProviderGateway;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProviderAdapter implements JwtProviderGateway {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<String> extractIdentityDocument(String jwt) {
        return Mono.fromCallable(() -> {
            Jws<Claims> claims = jwtUtil.validateToken(jwt);
            String identityDocument = claims.getBody().get("identityDocument", String.class);
            return identityDocument;
        }).onErrorResume(e -> {
            log.warn("JWT inválido: " + e.getMessage());
            return Mono.just("");
        });
    }
}