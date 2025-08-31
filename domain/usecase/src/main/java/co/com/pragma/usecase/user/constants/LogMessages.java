package co.com.pragma.usecase.user.constants;

public final class LogMessages {

    private LogMessages() {
        throw new IllegalStateException("Utility class");
    }

    public static final String EMAIL_ALREADY_EXISTS = "Intento de registro con correo existente: {0}";
    public static final String USER_SAVED_SUCCESS = "Usuario registrado exitosamente con email: {0}";
    public static final String FETCHING_ALL_USERS = "Consultando todos los usuarios";
    public static final String FETCHING_USER_BY_ID = "Buscando usuario por ID: {0}";
    public static final String FETCHING_USER_BY_DOC = "Buscando usuario por documento: {0}";

    public static final String EMAIL_ALREADY_EXISTS_ERROR = "El correo ya está registrado";
    public static final String ROLE_CLIENT_DEFAULT = "ROLE_CLIENT";
}
