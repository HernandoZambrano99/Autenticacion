package co.com.pragma.usecase.user;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("El correo ya está registrado"));
                    }
                    return userRepository.save(user);
                });
    }

    public Flux<User> findAllUsers(){
        return userRepository.findAll();
    }

    public Mono<User> findUserById(Long id){
        return userRepository.findById(id);
    }

    public Mono<User> findUserByIdentityDocument(String identityDocument){
        return userRepository.findByDocument(identityDocument);
    }
}
