package co.com.pragma.api.exceptionHandler;

import co.com.pragma.api.constants.LoginConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                                  org.springframework.boot.autoconfigure.web.WebProperties.Resources resources,
                                  ApplicationContext applicationContext,
                                  ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(configurer.getWriters());
        this.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        Throwable error = getError(request);

        if (error instanceof RequestValidationException vex) {
            var body = new LinkedHashMap<String, Object>();
            body.put(LoginConstants.ERROR, LoginConstants.VALIDATION_FAILED);
            body.put(LoginConstants.PATH, request.path());
            body.put(LoginConstants.DETAILS, vex.getDetails());
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        }

        if (error instanceof IllegalArgumentException iae) {
            var body = Map.of(
                    LoginConstants.ERROR, LoginConstants.CONFLICT,
                    LoginConstants.MESSAGE, iae.getMessage(),
                    LoginConstants.PATH, request.path()
            );
            return ServerResponse.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        }

        if (error instanceof org.springframework.web.server.ServerWebInputException sie) {
            var body = Map.of(
                    LoginConstants.ERROR, LoginConstants.BAD_REQUEST,
                    LoginConstants.MESSAGE, sie.getReason(),
                    LoginConstants.PATH, request.path()
            );
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        }

        if (error instanceof InvalidCredentialsException ice) {
            var body = Map.of(
                    LoginConstants.ERROR, LoginConstants.UNAUTHORIZED,
                    LoginConstants.MESSAGE, ice.getMessage(),
                    LoginConstants.PATH, request.path()
            );
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        }

        var errorProps = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        log.error("Error no controlado: {}", error.getMessage(), error);
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(errorProps);
    }
}