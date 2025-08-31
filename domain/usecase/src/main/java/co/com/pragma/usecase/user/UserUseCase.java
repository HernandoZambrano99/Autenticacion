package co.com.pragma.usecase.user;

import co.com.pragma.model.rol.Role;
import co.com.pragma.model.rol.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.PasswordEncoderGateway;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoderGateway;
    private final RoleRepository roleRepository;

    public Mono<User> saveUser(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("El correo ya está registrado"));
                    }
                    return Mono.fromCallable(() -> passwordEncoderGateway.encode(user.getPassword()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(hashed -> {
                                user.setPassword(hashed);
                                Mono<Role> roleResolvedMono;
                                roleResolvedMono = roleRepository.findByName("ROLE_CLIENT");
                                return roleResolvedMono
                                        .map(role -> {
                                            user.setRole(role);
                                            return user;
                                        })
                                        .flatMap(userRepository::save);
                            });
                });
    }


    public Flux<User> findAllUsers() {
        return userRepository.findAll();
    }

    public Mono<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> findUserByIdentityDocument(String identityDocument) {
        return userRepository.findByDocument(identityDocument);
    }

}
