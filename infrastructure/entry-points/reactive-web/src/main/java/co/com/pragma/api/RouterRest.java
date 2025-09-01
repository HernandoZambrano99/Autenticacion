package co.com.pragma.api;

import co.com.pragma.api.dto.request.LoginRequestDto;
import co.com.pragma.api.dto.request.UserRequestDto;
import co.com.pragma.api.dto.response.UserResponseDto;
import co.com.pragma.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class RouterRest {
    private final Handler userHandler;
    private final LoginHandler loginHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveUser",
                    operation = @Operation(
                            operationId = "GuardarUsuario",
                            summary = "Guardar un usuario",
                            description = "Crea un nuevo usuario en el sistema",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Datos del usuario a registrar",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UserRequestDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Error de validación en los datos de entrada"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/{id}",
                    method = org.springframework.web.bind.annotation.RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenFindById",
                    operation = @Operation(
                            operationId = "BuscarUsuarioPorId",
                            summary = "Buscar usuario por ID",
                            description = "Obtiene un usuario a partir de su identificador único",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = LoginHandler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "LoginUsuario",
                            summary = "Iniciar sesión",
                            description = "Autentica un usuario en el sistema mediante correo y contraseña, devolviendo un token JWT",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    description = "Credenciales del usuario",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = LoginRequestDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso",
                                            content = @Content(schema = @Schema(example = """
                                                {
                                                  "token_type": "Bearer",
                                                  "expires_in": 3600,
                                                  "access_token": "eyJhbGciOiJIUzI1NiIsInR5..."
                                                }
                                                """))
                                    ),
                                    @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/find/{identityDocument}",
                    method = org.springframework.web.bind.annotation.RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenFindByDocument",
                    operation = @Operation(
                            operationId = "BuscarUsuarioPorDocumento",
                            summary = "Buscar usuario por documento",
                            description = "Obtiene un usuario a partir de su documento de identidad",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/validate/{identityDocument}",
                    method = org.springframework.web.bind.annotation.RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "listenValidateMatch",
                    operation = @Operation(
                            operationId = "ValidarCoincidenciaUsuario",
                            summary = "Validar coincidencia de usuario",
                            description = "Valida si el documento del usuario coincide con el token JWT enviado en la cabecera",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Validación realizada",
                                            content = @Content(schema = @Schema(example = """
                                                { "match": true }
                                                """))
                                    ),
                                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                                    @ApiResponse(responseCode = "401", description = "Token inválido o sin coincidencia"),
                                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/usuarios"), userHandler::listenSaveUser)
                .andRoute(GET("/api/v1/usuarios/{id}"), userHandler::listenFindById)
                .andRoute(POST("/api/v1/login"), loginHandler::login)
                .andRoute(GET("/api/v1/usuarios/find/{identityDocument}"), userHandler::listenFindByDocument)
                .andRoute(GET("/api/v1/usuarios/validate/{identityDocument}"), userHandler::listenValidateMatch);
    }
}
