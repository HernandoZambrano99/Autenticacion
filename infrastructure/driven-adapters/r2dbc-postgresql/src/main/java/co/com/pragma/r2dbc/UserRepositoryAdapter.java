package co.com.pragma.r2dbc;

import co.com.pragma.model.rol.Role;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryAdapter extends ReactiveAdapterOperations<
        User/* change for domain model */,
        UserEntity/* change for adapter model */,
        Long,
        ReactiveUserRepository
        > implements UserRepository {

    private final ReactiveRoleRepository roleRepository;

    public UserRepositoryAdapter(ReactiveUserRepository repository, ObjectMapper mapper, ReactiveRoleRepository roleRepository) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.roleRepository = roleRepository;
    }

    @Override
    public Mono<User> findById(Long id) {
        return super.findById(id);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return super.repository.deleteById(id);
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return super.repository.existsByEmail(email);
    }

    @Override
    public Mono<User> findByDocument(String identityDocument) {
        return super.repository.findByIdentityDocument(identityDocument)
                .map(userEntity -> super.mapper.map(userEntity, User.class));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return super.repository.findByEmail(email)
                .flatMap(entity -> mapWithRole(mapper.map(entity, User.class)));
    }

    private Mono<User> mapWithRole(User user) {
        return roleRepository.findById(user.getRole().getId())
                .map(roleEntity -> {
                    Role role = Role.builder()
                            .id(roleEntity.getId())
                            .name(roleEntity.getName())
                            .description(roleEntity.getDescription())
                            .build();
                    user.setRole(role);
                    return user;
                });
    }
}
