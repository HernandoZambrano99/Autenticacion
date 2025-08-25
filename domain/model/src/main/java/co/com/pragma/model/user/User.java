package co.com.pragma.model.user;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String name;
    private String lastName;
    private LocalDateTime birthday;
    private String address;
    private Integer phone;
    private String email;
    private Integer salary;

}
