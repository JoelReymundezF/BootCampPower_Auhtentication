package co.com.crediya.r2dbc;

import co.com.crediya.model.user.User;
import co.com.crediya.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @InjectMocks
    UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    TransactionalOperator txOperator;

    @Mock
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        repositoryAdapter = new UserReactiveRepositoryAdapter(repository, mapper, txOperator);
    }

    @Test
    void validateExisteEmail() {
        when(repository.existsByEmailOrIdentityDocument("pedro.dama@mail.com", "123"))
                .thenReturn(Mono.just(true));
        Mono<Boolean> result = repositoryAdapter.existsByEmailOrIdentityDocument("pedro.dama@mail.com", "123");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsByEmailOrIdentityDocument("pedro.dama@mail.com", "123");
    }

    @Test
    void testSaveUser() {
        User user = new User();
        UserEntity entity = new UserEntity();

        when(mapper.map(any(User.class), eq(UserEntity.class))).thenReturn(entity);
        when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(user);
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(entity));
        when(txOperator.execute(any()))
                .thenAnswer(invocation -> {
                    org.springframework.transaction.reactive.TransactionCallback<?> callback =
                            (org.springframework.transaction.reactive.TransactionCallback<?>) invocation.getArgument(0);
                    return Flux.from(callback.doInTransaction(null));
                });

        StepVerifier.create(repositoryAdapter.save(user))
                .expectNext(user)
                .verifyComplete();

        verify(repository).save(any(UserEntity.class));
    }


    @Test
    void testFindAll() {
        UserEntity entity1 = new UserEntity();
        UserEntity entity2 = new UserEntity();
        User user1 = new User();
        User user2 = new User();

        when(repository.findAll()).thenReturn(Flux.just(entity1, entity2));
        when(mapper.map(entity1, User.class)).thenReturn(user1);
        when(mapper.map(entity2, User.class)).thenReturn(user2);

        StepVerifier.create(repositoryAdapter.findAll())
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();

        verify(repository).findAll();
    }


}
