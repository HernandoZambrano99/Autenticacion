package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    private UserRepository userRepository;
    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userUseCase = new UserUseCase(userRepository);
    }

    @Test
    void saveUser_ShouldSave_WhenEmailDoesNotExist() {
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .name("John Doe")
                .build();

        when(userRepository.existsByEmail(eq(user.getEmail()))).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectNext(user)
                .verifyComplete();

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void saveUser_ShouldError_WhenEmailAlreadyExists() {
        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .name("John Doe")
                .build();

        when(userRepository.existsByEmail(eq(user.getEmail()))).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(throwable ->
                        throwable instanceof IllegalArgumentException &&
                                throwable.getMessage().equals("El correo ya está registrado"))
                .verify();

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    void findAllUsers_ShouldReturnAllUsers() {
        User user1 = User.builder()
                .id(1L)
                .email("test@test.com")
                .name("Alice")
                .build();
        User user2 = User.builder()
                .id(2L)
                .email("b@test.com")
                .name("Bob")
                .build();

        when(userRepository.findAll()).thenReturn(Flux.just(user1, user2));

        StepVerifier.create(userUseCase.findAllUsers())
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();

        verify(userRepository).findAll();
    }
}
