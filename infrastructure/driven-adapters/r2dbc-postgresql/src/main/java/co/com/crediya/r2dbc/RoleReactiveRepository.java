package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.RoleEntity;
import co.com.crediya.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface RoleReactiveRepository extends ReactiveCrudRepository<RoleEntity, Long>, ReactiveQueryByExampleExecutor<RoleEntity> {

    Mono<Boolean> existsById(Long id);
}
