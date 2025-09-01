package co.com.pragma.model.user.gateways;

import reactor.core.publisher.Mono;

public interface JwtProviderGateway {
    Mono<String> extractIdentityDocument(String jwt);
}
