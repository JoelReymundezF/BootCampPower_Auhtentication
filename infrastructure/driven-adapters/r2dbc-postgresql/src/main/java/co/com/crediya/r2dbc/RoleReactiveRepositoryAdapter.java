package co.com.crediya.r2dbc;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.r2dbc.entity.RoleEntity;
import co.com.crediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class RoleReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Role,
        RoleEntity,
        Long,
        RoleReactiveRepository
        > implements RoleRepository {

    public RoleReactiveRepositoryAdapter(RoleReactiveRepository repository,
                                         ObjectMapper mapper,
                                         TransactionalOperator txOperator) {
        super(repository, mapper, d -> mapper.map(d, Role.class), txOperator);
    }


    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(id);
    }

    @Override
    public Mono<Role> findById(Long id) {
        return super.findById(id);
    }
}