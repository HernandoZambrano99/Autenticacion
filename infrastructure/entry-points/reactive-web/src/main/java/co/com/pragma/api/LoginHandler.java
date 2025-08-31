package co.com.pragma.api;

import co.com.pragma.api.dto.request.LoginRequestDto;
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
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginHandler {

    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoder;
    private final JwtUtil jwtUtil;

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginRequestDto.class)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Body vacío o inválido")))
                .flatMap(dto -> userRepository.findByEmail(dto.getEmail())
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas")))
                        .flatMap(user -> Mono.fromCallable(() -> passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(matches -> {
                                    if (!matches) {
                                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
                                    }

                                    String roleName = (user.getRole() != null) ? user.getRole().getName() : "ROLE_CLIENT";
                                    String token = jwtUtil.generateToken(user.getId(), user.getEmail(), roleName);

                                    return ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(Map.of(
                                                    "token_type", "Bearer",
                                                    "expires_in", jwtUtil.getExpirationMs() / 1000,
                                                    "access_token", token
                                            ));
                                })
                        )
                )
                .onErrorResume(ResponseStatusException.class,
                        ex -> ServerResponse.status(ex.getStatusCode())
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(Map.of("error", ex.getReason())));
    }

}
