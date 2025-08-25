package co.com.pragma.api;

import co.com.pragma.api.dto.UserRequestDto;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Validated
public class Handler {

    private final UserUseCase userUseCase;

    public Mono<ServerResponse> listenSaveUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequestDto.class)
                .flatMap(userRequestDto -> {
                    User user = mapToDomain(userRequestDto);
                    return userUseCase.saveUser(user);
                })
                .flatMap(savedUser -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUser))
                .onErrorResume(WebExchangeBindException.class, ex -> {
                    var errors = ex.getFieldErrors().stream()
                            .map(err -> err.getField() + ": " + err.getDefaultMessage())
                            .toList();
                    return ServerResponse.badRequest().bodyValue(errors);
                })
                .onErrorResume(e -> ServerResponse.badRequest().bodyValue("Error: " + e.getMessage()));
    }

    public Mono<ServerResponse> listenFindAll(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue(userUseCase.findAllUsers());
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    private User mapToDomain(UserRequestDto dto) {
        return User.builder()
                .name(dto.getName())
                .lastName(dto.getLastName())
                .birthday(dto.getBirthday())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .salary(dto.getSalary())
                .build();
    }
}
