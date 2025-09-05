package co.com.crediya.model.user.gateways;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<Boolean> existsByEmailOrIdentityDocument(String email, String identityDocument);
    Mono<Boolean> existsByIdentityDocument(String identityDocument);
}
