package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.r2dbc.entity.UserEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface UserReactiveRepository extends ReactiveCrudRepository<UserEntity, BigInteger>, ReactiveQueryByExampleExecutor<UserEntity> {

    Mono<Boolean> existsByEmailOrIdentityDocument(String email, String identityDocument);
    Mono<Boolean> existsByIdentityDocument(String identityDocument);
    Mono<User> findByIdentityDocument(String identityDocument);
    Mono<User> findByEmail(String email);
}
