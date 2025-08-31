package co.com.pragma.usecase.user;

import co.com.pragma.model.rol.Role;
import co.com.pragma.model.rol.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.PasswordEncoderGateway;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

    private UserRepository userRepository;
    private UserUseCase userUseCase;
    private PasswordEncoderGateway passwordEncoderGateway;
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        roleRepository = Mockito.mock(RoleRepository.class);
        passwordEncoderGateway = Mockito.mock(PasswordEncoderGateway.class);
        userUseCase = new UserUseCase(userRepository, passwordEncoderGateway, roleRepository);
    }

    @Test
    void saveUser_ShouldSave_WhenEmailDoesNotExist() {
        User user = UserDataProvider.basicUser();
        Role clientRole = UserDataProvider.defaultClientRole();
        User expectedUser = UserDataProvider.hashedUser();

        when(userRepository.existsByEmail(eq(user.getEmail()))).thenReturn(Mono.just(false));
        when(passwordEncoderGateway.encode("password")).thenReturn("hashedPassword");
        when(roleRepository.findByName("ROLE_CLIENT")).thenReturn(Mono.just(clientRole));

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(expectedUser));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectNext(expectedUser)
                .verifyComplete();

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void saveUser_ShouldError_WhenEmailAlreadyExists() {

        User user = UserDataProvider.userAlice();

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
        List<User> users = UserDataProvider.allUsers();

        when(userRepository.findAll()).thenReturn(Flux.fromIterable(users));

        StepVerifier.create(userUseCase.findAllUsers())
                .expectNext(users.get(0))
                .expectNext(users.get(1))
                .verifyComplete();

        verify(userRepository).findAll();
    }

    @Test
    void findByIdReturnUser() {
        User user1 = UserDataProvider.userAlice();

        when(userRepository.findById(1L)).thenReturn(Mono.just(user1));

        StepVerifier.create(userUseCase.findUserById(1L))
                .expectNext(user1)
                .verifyComplete();
        verify(userRepository).findById(1L);
    }

    @Test
    void findByDocumentNumberReturnUser() {
        User user1 = UserDataProvider.userAlice();

        when(userRepository.findByDocument("123")).thenReturn(Mono.just(user1));

        StepVerifier.create(userUseCase.findUserByIdentityDocument("123"))
                .expectNext(user1)
                .verifyComplete();
        verify(userRepository).findByDocument("123");
    }
}
