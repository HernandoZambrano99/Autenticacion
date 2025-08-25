package co.com.pragma.api.exceptionHandler;

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
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", "Validation Failed");
            body.put("path", request.path());
            body.put("details", vex.getDetails());
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        }

        if (error instanceof IllegalArgumentException iae) {
            var body = Map.of(
                    "status", HttpStatus.CONFLICT.value(),
                    "error", "Conflict",
                    "message", iae.getMessage(),
                    "path", request.path()
            );
            return ServerResponse.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);
        }

        if (error instanceof org.springframework.web.server.ServerWebInputException sie) {
            var body = Map.of(
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "error", "Bad Request",
                    "message", sie.getReason(),
                    "path", request.path()
            );
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
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