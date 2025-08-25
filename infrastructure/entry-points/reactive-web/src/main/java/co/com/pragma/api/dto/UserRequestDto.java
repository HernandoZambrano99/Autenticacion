package co.com.pragma.api.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;

    private LocalDateTime birthday;

    private String address;

    private Integer phone;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "Formato de correo no válido")
    private String email;

    @NotNull(message = "El salario es obligatorio")
    @Min(value = 0, message = "El salario no puede ser negativo")
    @Max(value = 15000000, message = "El salario no puede superar 15 millones")
    private Integer salary;
}
