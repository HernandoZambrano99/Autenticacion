package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {
    // TODO: change four you own tests

    @InjectMocks
    UserRepositoryAdapter repositoryAdapter;

    @Mock
    ReactiveUserRepository repository;

    @Mock
    ObjectMapper mapper;

    @Test
    void mustFindValueById() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("Hernando")
                .lastName("Zambrano")
                .email("test@mail.com")
                .salary(5000)
                .build();

        User user = User.builder()
                .id(1L)
                .name("Hernando")
                .lastName("Zambrano")
                .email("test@mail.com")
                .salary(5000)
                .build();

        when(repository.findById(1L)).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        Mono<User> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("Hernando")
                .lastName("Zambrano")
                .email("test@mail.com")
                .salary(5000)
                .build();

        User user = User.builder()
                .id(1L)
                .name("Hernando")
                .lastName("Zambrano")
                .email("test@mail.com")
                .salary(5000)
                .build();

        when(repository.findAll()).thenReturn(Flux.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);

        Flux<User> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(1L))
                .verifyComplete();
    }

//    @Test
//    void mustFindByExample() {
//        when(repository.findAll(any(Example.class))).thenReturn(Flux.just("test"));
//        when(mapper.map("test", Object.class)).thenReturn("test");
//
//        Flux<Object> result = repositoryAdapter.findByExample("test");
//
//        StepVerifier.create(result)
//                .expectNextMatches(value -> value.equals("test"))
//                .verifyComplete();
//    }
//
    @Test
    void mustSaveValue() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("Hernando")
                .lastName("Zambrano")
                .email("test@mail.com")
                .salary(5000)
                .build();

        User user = User.builder()
                .id(1L)
                .name("Hernando")
                .lastName("Zambrano")
                .email("test@mail.com")
                .salary(5000)
                .build();

        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));
        when(mapper.map(userEntity, User.class)).thenReturn(user);
        when(mapper.map(user, UserEntity.class)).thenReturn(userEntity);

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNextMatches(saved -> saved.getId().equals(1L) && saved.getName().equals("Hernando"))
                .verifyComplete();
    }
}
