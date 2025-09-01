package co.com.pragma.api;

import co.com.pragma.api.dto.request.UserRequestDto;
import co.com.pragma.api.dto.response.UserResponseDto;
import co.com.pragma.api.exceptionHandler.RequestValidationException;
import co.com.pragma.api.mapper.UserRequestMapper;
import co.com.pragma.api.mapper.UserResponseMapper;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Validated
public class Handler {

    private final UserUseCase userUseCase;
    private final Validator validator;
    private final UserRequestMapper userRequestMapper;
    private final UserResponseMapper userResponseMapper;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDto.class)
                .flatMap(dto -> {
                    var violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        var details = violations.stream()
                                .map(v -> new RequestValidationException.FieldErrorDetail(
                                        v.getPropertyPath().toString(),
                                        v.getMessage()))
                                .toList();
                        return Mono.error(new RequestValidationException(details));
                    }
                    return Mono.just(dto);
                })
                .map(userRequestMapper::toModel)
                .flatMap(userUseCase::saveUser)
                .map(userResponseMapper::toDto)
                .flatMap(savedUser -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser));
    }

    public Mono<ServerResponse> listenFindAll(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue(userUseCase.findAllUsers());
    }

    public Mono<ServerResponse> listenFindByDocument(ServerRequest serverRequest){
        String authHeader = serverRequest.headers().firstHeader("Authorization");
        String jwt = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        String identityDocument = serverRequest.pathVariable("identityDocument");
        return userUseCase.findUserByIdentityDocument(identityDocument)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user));
    }

    public Mono<ServerResponse> listenFindById(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.pathVariable("id"));
        return userUseCase.findUserById(id)
                .flatMap(user -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(user))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> listenValidateMatch(ServerRequest serverRequest) {
        String authHeader = serverRequest.headers().firstHeader("Authorization");
        String jwt = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        String identityDocument = serverRequest.pathVariable("identityDocument");

        return userUseCase.matchDocumentIdWithJwt(identityDocument, jwt)
                .flatMap(match -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of("match", match))
                );
    }

}
