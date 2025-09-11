package co.com.crediya.model.token.gateways;

import co.com.crediya.model.user.User;
import reactor.core.publisher.Mono;

public interface TokenProviderGateway {

    Mono<String> generateToken(User user);

    Mono<Boolean> validateToken(String token);

    Mono<String> getUsernameFromToken(String token);

    Mono<User> getUserFromToken(String token);

}
