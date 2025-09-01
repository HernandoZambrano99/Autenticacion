package co.com.pragma.model.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN(1L, "ROLE_ADMIN", "Administrador del sistema"),
    ASESOR(2L, "ROLE_ASESOR", "Cliente del sistema"),
    USER(3L, "ROLE_CLIENT", "Usuario estándar");

    private final Long id;
    private final String name;
    private final String description;

    RoleEnum(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static RoleEnum fromId(Long id) {
        for (RoleEnum role : values()) {
            if (role.getId().equals(id)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No existe un RoleEnum con id: " + id);
    }
}
