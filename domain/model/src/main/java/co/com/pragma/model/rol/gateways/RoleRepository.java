package co.com.pragma.model.rol.gateways;

import co.com.pragma.model.rol.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Role> findById(Long id);
    Mono<Role> findByName(String name);
}