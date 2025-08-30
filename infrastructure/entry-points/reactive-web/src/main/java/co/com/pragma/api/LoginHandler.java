package co.com.pragma.api;

import co.com.pragma.api.dto.LoginRequestDto;
import co.com.pragma.api.security.JwtUtil;
import co.com.pragma.model.user.gateways.PasswordEncoderGateway;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginHandler {

    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoder;
    private final JwtUtil jwtUtil;

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequestDto.class)
                .flatMap(dto -> userRepository.findByEmail(dto.getEmail())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")))
                        .flatMap(user -> Mono.fromCallable(() -> passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                                .subscribeOn(reactor.core.scheduler.Schedulers.boundedElastic())
                                .flatMap(matches -> {
                                    if (!matches) {
                                        // sin límite de intentos: simplemente rechazar
                                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
                                    }
                                    String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getName());
                                    Map<String, Object> body = Map.of(
                                            "token", token,
                                            "userId", user.getId(),
                                            "email", user.getEmail(),
                                            "role", user.getRole().getName()
                                    );
                                    return ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(body);
                                })
                        )
                );
    }
}
