package co.com.pragma.r2dbc;

import co.com.pragma.r2dbc.entity.RoleEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ReactiveRoleRepository extends ReactiveCrudRepository<RoleEntity, Long>, ReactiveQueryByExampleExecutor<RoleEntity> {
    @Query("SELECT * FROM rol WHERE nombre = :name")
    Mono<RoleEntity> findByName(String name);
}