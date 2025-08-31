package co.com.pragma.r2dbc;

import co.com.pragma.model.rol.Role;
import co.com.pragma.model.rol.gateways.RoleRepository;
import co.com.pragma.r2dbc.entity.RoleEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class RoleRepositoryAdapter extends ReactiveAdapterOperations<
        Role,
        RoleEntity,
        Long,
        ReactiveRoleRepository
        > implements RoleRepository {

    public RoleRepositoryAdapter(ReactiveRoleRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
    }

    @Override
    public Mono<Role> findByName(String name) {
        return super.repository.findByName(name)
                .map(entity -> mapper.map(entity, Role.class));
    }
}