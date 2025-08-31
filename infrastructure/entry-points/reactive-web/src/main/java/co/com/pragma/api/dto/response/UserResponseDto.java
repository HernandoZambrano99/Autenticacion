package co.com.pragma.api.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserResponseDto {
    private Long id;
    private String name;
    private String lastName;
    private String identityDocument;
    private LocalDateTime birthday;
    private String address;
    private Integer phone;
    private String email;
}
