package co.com.pragma.usecase.user;

import co.com.pragma.model.rol.Role;
import co.com.pragma.model.rol.gateways.RoleRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.PasswordEncoderGateway;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.user.constants.LogMessages;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class UserUseCase {
    private static final Logger logger = Logger.getLogger(UserUseCase.class.getName());

    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoderGateway;
    private final RoleRepository roleRepository;

    public Mono<User> saveUser(User user) {
        return userRepository.existsByEmail(user.getEmail())
                .flatMap(exists -> {
                    if (exists) {
                        logger.warning(MessageFormat.format(LogMessages.EMAIL_ALREADY_EXISTS, user.getEmail()));
                        return Mono.error(new IllegalArgumentException(LogMessages.EMAIL_ALREADY_EXISTS_ERROR));
                    }
                    return Mono.fromCallable(() -> passwordEncoderGateway.encode(user.getPassword()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(hashed -> {
                                user.setPassword(hashed);
                                Mono<Role> roleResolvedMono;
                                roleResolvedMono = roleRepository.findByName(LogMessages.ROLE_CLIENT_DEFAULT);
                                return roleResolvedMono
                                        .map(role -> {
                                            user.setRole(role);
                                            return user;
                                        })
                                        .flatMap(userRepository::save)
                                        .doOnSuccess(savedUser -> logger.log(Level.INFO,
                                                MessageFormat.format(LogMessages.USER_SAVED_SUCCESS, savedUser.getEmail())));
                            });
                });
    }


    public Flux<User> findAllUsers() {
        logger.info(LogMessages.FETCHING_ALL_USERS);
        return userRepository.findAll();
    }

    public Mono<User> findUserById(Long id) {
        logger.info(MessageFormat.format(LogMessages.FETCHING_USER_BY_ID, id));
        return userRepository.findById(id);
    }

    public Mono<User> findUserByIdentityDocument(String identityDocument) {
        logger.info(MessageFormat.format(LogMessages.FETCHING_USER_BY_DOC, identityDocument));
        return userRepository.findByDocument(identityDocument);
    }

}
