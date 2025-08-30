package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserDTO;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.api.helper.validation.ValidationUtil;
import co.com.crediya.api.mapper.UserMapperDTO;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = {UserRouterRest.class, UserHandler.class})
@WebFluxTest
@Import({UserRouterRest.class, UserHandler.class, GlobalExceptionHandler.class})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserMapperDTO userMapper;

    @MockitoBean
    private ValidationUtil validationUtil;

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

    CreateUserDTO createUserDTO = new CreateUserDTO(
            "Joel",
            "Flores",
            LocalDate.of(1995, 5, 21),
            "Av. Siempre Viva 123",
            "987654321",
            "joel@test.com",
            "12345678",
            new BigDecimal("3500.00"),
            1L
    );

    UserDTO responseDTO = new UserDTO(
            "Joel",
            "Flores",
            LocalDate.of(1995, 5, 21),
            "11112143",
            "Av. Siempre Viva 123",
            "987654321",
            "joel@test.com",
            new BigDecimal("3500.00"),
            1L
    );

    @Test
    void testListenSaveUserSuccess() {

        var userModel = new co.com.crediya.model.user.User();
        userModel.setFirstName("Joel");
        userModel.setLastName("Flores");
        userModel.setEmail("joel@test.com");
        userModel.setIdentityDocument("12345678");

        when(validationUtil.validate(any(CreateUserDTO.class))).thenReturn(Mono.just(createUserDTO));

        when(userMapper.toModel(any(CreateUserDTO.class))).thenReturn(userModel);
        when(userUseCase.saveUser(any())).thenReturn(Mono.just(userModel));
        when(userMapper.toResponse(any())).thenReturn(responseDTO);

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.data.firstName").isEqualTo("Joel")
                .jsonPath("$.data.lastName").isEqualTo("Flores");
    }

    @Test
    void testListenSaveUserValidationError() {
        ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
        Mockito.when(violation.getMessage()).thenReturn("El campo email es obligatorio");

        ConstraintViolationException exception =
                new ConstraintViolationException(Set.of(violation));

        Mockito.when(validationUtil.validate(Mockito.any(CreateUserDTO.class)))
                .thenReturn(Mono.error(exception));

        CreateUserDTO dto = new CreateUserDTO();

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo(GlobalExceptionHandler.ERROR_VALIDATION)
                .jsonPath("$.errors[0]").isEqualTo("El campo email es obligatorio");
    }
}
