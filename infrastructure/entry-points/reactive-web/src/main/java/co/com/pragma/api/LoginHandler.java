package co.com.pragma.api;

import co.com.pragma.api.constants.LoginConstants;
import co.com.pragma.api.dto.request.LoginRequestDto;
import co.com.pragma.api.exceptionHandler.InvalidCredentialsException;
import co.com.pragma.api.security.JwtUtil;
import co.com.pragma.model.user.gateways.PasswordEncoderGateway;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
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
                .switchIfEmpty(Mono.error(new IllegalArgumentException(LoginConstants.ERROR_BODY_INVALID)))
                .flatMap(dto -> userRepository.findByEmail(dto.getEmail())
                        .switchIfEmpty(Mono.error(new InvalidCredentialsException(LoginConstants.ERROR_INVALID_CREDENTIALS)))
                        .flatMap(user -> Mono.fromCallable(() -> passwordEncoder.matches(dto.getPassword(), user.getPassword()))
                                .subscribeOn(Schedulers.boundedElastic())
                                .flatMap(matches -> {
                                    if (!matches) {
                                        return Mono.error(new InvalidCredentialsException(LoginConstants.ERROR_INVALID_CREDENTIALS));
                                    }

                                    String roleName = (user.getRole() != null) ? user.getRole().getName() : LoginConstants.DEFAULT_ROLE;
                                    String token = jwtUtil.generateToken(user.getId(), user.getEmail(), roleName, user.getIdentityDocument());

                                    return ServerResponse.ok()
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(Map.of(
                                                    LoginConstants.TOKEN_TYPE, LoginConstants.TOKEN_TYPE_VALUE,
                                                    LoginConstants.EXPIRES_IN, jwtUtil.getExpirationMs() / 1000,
                                                    LoginConstants.ACCESS_TOKEN, token
                                            ));
                                })
                        )
                );
    }

}
