package co.com.crediya.model.role.gateways;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface RoleRepository {
   Mono<Boolean> existsById(Long id);
}
