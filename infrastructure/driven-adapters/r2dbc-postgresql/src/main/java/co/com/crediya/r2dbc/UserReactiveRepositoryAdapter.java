package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import co.com.crediya.r2dbc.entity.UserEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User,
        UserEntity,
        BigInteger,
        UserReactiveRepository
> implements UserRepository {


    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator txOperator) {
        super(repository, mapper, d -> mapper.map(d, User.class/* change for domain model */), txOperator);
    }

    @Override
    public Mono<User> save(User user) {
        return super.save(user);
    }

    @Override
    public Mono<Boolean> existsByEmailOrIdentityDocument(String email, String identityDocument) {
        return this.repository.existsByEmailOrIdentityDocument(email,identityDocument);
    }
 }