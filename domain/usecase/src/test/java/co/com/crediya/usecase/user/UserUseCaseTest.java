package co.com.crediya.usecase.user;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.token.gateways.TokenProviderGateway;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.BootcampRoleNotFoundException;
import co.com.crediya.model.user.exceptions.BootcampUserAlreadyExistsException;
import co.com.crediya.model.user.gateways.PasswordEncoderGateway;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserUseCase userUseCase;
    @Mock
    private PasswordEncoderGateway passwordEncoderGateway;
    @Mock
    private TokenProviderGateway tokenProvider;

    private final User user = User.builder()
            .firstName("Joel")
            .lastName("Flores")
            .email("joel@test.com")
            .identityDocument("12345678")
            .password("123456")
            .baseSalary(new BigDecimal("3500.00"))
            .birthDate(LocalDate.of(1995, 5, 21))
            .roleId(1L)
            .build();

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoderGateway = mock(PasswordEncoderGateway.class);
        tokenProvider = mock(TokenProviderGateway.class);

        userUseCase = new UserUseCase(userRepository, roleRepository, passwordEncoderGateway, tokenProvider);
    }

    @Test
    void saveUserSuccess() {
        when(roleRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmailOrIdentityDocument(user.getEmail(), user.getIdentityDocument())).thenReturn(Mono.just(false));
        when(passwordEncoderGateway.encode("123456")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectNextMatches(saved -> saved.getPassword().equals("encodedPass"))
                .verifyComplete();

        verify(userRepository).save(any(User.class));
    }

    @Test
    void saveUserRoleNotFound() {
        when(roleRepository.existsById(1L)).thenReturn(Mono.just(false));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(ex -> ex instanceof BootcampRoleNotFoundException)
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void saveUserAlreadyExists() {
        when(roleRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmailOrIdentityDocument(user.getEmail(), user.getIdentityDocument())).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.saveUser(user))
                .expectErrorMatches(ex -> ex instanceof BootcampUserAlreadyExistsException)
                .verify();

        verify(userRepository, never()).save(any());
    }

    @Test
    void existsByDocumentTest() {
        when(userRepository.existsByIdentityDocument("12345678")).thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.existsByDocument("12345678"))
                .expectNext(true)
                .verifyComplete();

        verify(userRepository).existsByIdentityDocument("12345678");
    }

    @Test
    void loginSuccess() {
        User storedUser = user.toBuilder().password("encodedPass").build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(storedUser));
        when(passwordEncoderGateway.matches("123456", "encodedPass")).thenReturn(true);
        when(tokenProvider.generateToken(storedUser)).thenReturn(Mono.just("jwt-token"));

        StepVerifier.create(userUseCase.login(user.getEmail(), "123456"))
                .expectNext("jwt-token")
                .verifyComplete();
    }

    @Test
    void loginInvalidPassword() {
        User storedUser = user.toBuilder().password("encodedPass").build();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Mono.just(storedUser));
        when(passwordEncoderGateway.matches("wrongPass", "encodedPass")).thenReturn(false);

        StepVerifier.create(userUseCase.login(user.getEmail(), "wrongPass"))
                .expectErrorMatches(ex -> ex.getMessage().equals("Invalid credentials"))
                .verify();
    }
}
