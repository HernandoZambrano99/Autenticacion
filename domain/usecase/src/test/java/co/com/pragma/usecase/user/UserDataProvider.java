package co.com.pragma.usecase.user;

import co.com.pragma.model.rol.Role;
import co.com.pragma.model.user.User;

import java.util.List;

public class UserDataProvider {

    public static Role defaultClientRole() {
        return Role.builder()
                .id(1L)
                .name("ROLE_CLIENT")
                .description("Default client role")
                .build();
    }

    public static Role adminRole() {
        return Role.builder()
                .id(2L)
                .name("ROLE_ADMIN")
                .description("Administrator role")
                .build();
    }

    public static User basicUser() {
        return User.builder()
                .id(1L)
                .email("test@test.com")
                .name("John Doe")
                .password("password")
                .build();
    }

    public static User basicUserWithRole() {
        return basicUser().toBuilder()
                .role(defaultClientRole())
                .build();
    }

    public static User hashedUser() {
        return basicUser().toBuilder()
                .password("hashedPassword")
                .role(defaultClientRole())
                .build();
    }

    public static User userAlice() {
        return User.builder()
                .id(2L)
                .email("alice@test.com")
                .name("Alice")
                .identityDocument("123")
                .build();
    }

    public static User userBob() {
        return User.builder()
                .id(3L)
                .email("bob@test.com")
                .name("Bob")
                .identityDocument("456")
                .build();
    }

    public static List<User> allUsers() {
        return List.of(userAlice(), userBob());
    }
}