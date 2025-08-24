package co.com.pragma.r2dbc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    private Long id;
    private String name;
    private String lastName;
    private LocalDateTime birthday;
    private String adddress;
    private Integer phone;
    private String email;
    private Integer salary;
}
