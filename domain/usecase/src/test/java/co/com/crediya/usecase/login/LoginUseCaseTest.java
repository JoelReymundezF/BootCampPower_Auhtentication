package co.com.crediya.usecase.login;

import co.com.crediya.model.exeption.BootcampRuleCode;
import co.com.crediya.model.exeption.BootcampRuleException;
import co.com.crediya.model.token.gateways.TokenProviderGateway;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.PasswordEncoderGateway;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoderGateway passwordEncoder;
    @Mock
    private TokenProviderGateway tokenProvider;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .email("test@email.com")
                .password("encodedPass")
                .identityDocument("123456")
                .roleId(1L)
                .build();
    }

    @Test
    void login_success() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("plainPass", "encodedPass")).thenReturn(true);
        when(tokenProvider.generateToken(user)).thenReturn(Mono.just("jwtToken"));

        StepVerifier.create(loginUseCase.login(user.getEmail(), "plainPass"))
                .expectNext("jwtToken")
                .verifyComplete();

        verify(tokenProvider).generateToken(user);
    }

    @Test
    void login_userNotFound() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.empty());

        StepVerifier.create(loginUseCase.login(user.getEmail(), "plainPass"))
                .expectErrorMatches(err ->
                        err instanceof BootcampRuleException &&
                                ((BootcampRuleException) err).getCode().equals(BootcampRuleCode.USER_NOT_FOUND.getCode())
                )
                .verify();
    }

    @Test
    void login_incorrectPassword() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("wrongPass", "encodedPass")).thenReturn(false);

        StepVerifier.create(loginUseCase.login(user.getEmail(), "wrongPass"))
                .expectErrorMatches(err ->
                        err instanceof BootcampRuleException &&
                                ((BootcampRuleException) err).getCode().equals(BootcampRuleCode.INCORRECT_PASSWORD.getCode())
                )
                .verify();
    }
}
