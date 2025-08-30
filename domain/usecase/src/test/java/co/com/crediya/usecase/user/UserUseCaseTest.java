package co.com.crediya.usecase.user;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.BootcampRoleNotFoundException;
import co.com.crediya.model.user.exceptions.BootcampUserAlreadyExistsException;
import co.com.crediya.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private final User userOne = User.builder()
            .firstName("Joel")
            .lastName("Flores")
            .birthDate(LocalDate.of(1995, 5, 21))
            .address("Av. Siempre Viva 123")
            .identityDocument("12345678")
            .phone("987654321")
            .email("joel@test.com")
            .baseSalary(new BigDecimal("3500.00"))
            .roleId(1L)
            .build();

    @Test
    void testSaveUserSuccess() {
        when(roleRepository.existsById(userOne.getRoleId())).thenReturn(Mono.just(true));
        when(userRepository.existsByEmailOrIdentityDocument(userOne.getEmail(), userOne.getIdentityDocument()))
                .thenReturn(Mono.just(false));
        when(userRepository.save(userOne)).thenReturn(Mono.just(userOne));
        StepVerifier.create(userUseCase.saveUser(userOne))
                .expectNextMatches(savedUser -> savedUser.getIdentityDocument().equals(userOne.getIdentityDocument()))
                .verifyComplete();
    }

    @Test
    void testSaveUserRoleNotFound() {
        when(roleRepository.existsById(userOne.getRoleId())).thenReturn(Mono.just(false));
        StepVerifier.create(userUseCase.saveUser(userOne))
                .expectError(BootcampRoleNotFoundException.class)
                .verify();
    }

    @Test
    void testSaveUserEmailOrDocumentExists() {
        when(roleRepository.existsById(userOne.getRoleId())).thenReturn(Mono.just(true));

        when(userRepository.existsByEmailOrIdentityDocument(userOne.getEmail(), userOne.getIdentityDocument()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(userUseCase.saveUser(userOne))
                .expectError(BootcampUserAlreadyExistsException.class)
                .verify();
    }
}
