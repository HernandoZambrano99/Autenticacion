package co.com.pragma.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("last_name")
    private String lastName;

    @Column("birthday")
    private LocalDateTime birthday;

    @Column("address")
    private String address;

    @Column("phone")
    private Integer phone;

    @Column("email")
    private String email;

    @Column("salary")
    private BigDecimal salary;
}
