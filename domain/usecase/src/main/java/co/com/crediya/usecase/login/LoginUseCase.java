package co.com.crediya.usecase.login;

import co.com.crediya.model.exeption.BootcampRuleCode;
import co.com.crediya.model.exeption.BootcampRuleException;
import co.com.crediya.model.token.gateways.TokenProviderGateway;
import co.com.crediya.model.user.gateways.PasswordEncoderGateway;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoderGateway passwordEncoder;
    private final TokenProviderGateway tokenProvider;

    public Mono<String> login(String email, String password) {
        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new BootcampRuleException(BootcampRuleCode.USER_NOT_FOUND)))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.error(new BootcampRuleException(BootcampRuleCode.INCORRECT_PASSWORD));
                    }
                    return tokenProvider.generateToken(user);
                });
    }
}
