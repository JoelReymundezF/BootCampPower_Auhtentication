package co.com.crediya.r2dbc;

import co.com.crediya.model.role.Role;
import co.com.crediya.r2dbc.entity.RoleEntity;
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
class RoleReactiveRepositoryAdapterTest {

    @InjectMocks
    RoleReactiveRepositoryAdapter adapter;

    @Mock
    RoleReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Mock
    TransactionalOperator txOperator;

    @BeforeEach
    void setUp() {
        adapter = new RoleReactiveRepositoryAdapter(repository, mapper, txOperator);
    }

    @Test
    void testExistsById() {
        Long roleId = 1L;

        when(repository.existsById(roleId)).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsById(roleId))
                .expectNext(true)
                .verifyComplete();

        verify(repository).existsById(roleId);
    }

    @Test
    void testSaveRole() {
        Role role = new Role();
        RoleEntity entity = new RoleEntity();

        // Mock ObjectMapper para ambos sentidos
        when(mapper.map(any(Role.class), eq(RoleEntity.class))).thenReturn(entity);
        when(mapper.map(any(RoleEntity.class), eq(Role.class))).thenReturn(role);

        // Mock repository save
        when(repository.save(any(RoleEntity.class))).thenReturn(Mono.just(entity));

        // Mock TransactionalOperator
        when(txOperator.execute(any()))
                .thenAnswer(invocation -> {
                    var callback = (org.springframework.transaction.reactive.TransactionCallback<Object>) invocation.getArgument(0);
                    return Flux.from(callback.doInTransaction(null));
                });

        StepVerifier.create(adapter.save(role))
                .expectNext(role)
                .verifyComplete();

        verify(repository).save(any(RoleEntity.class));
    }
}
