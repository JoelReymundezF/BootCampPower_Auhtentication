package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserDTO;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.api.helper.ApiResponse;
import co.com.crediya.api.mapper.UserMapperDTO;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import(GlobalExceptionHandler.class)
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserMapperDTO userMapperDTO;

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

    UserDTO userDTO = new UserDTO(
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
    void testListenPOSTUseCase() {
        when(userMapperDTO.toModel(any(CreateUserDTO.class))).thenReturn(userOne);
        when(userUseCase.saveUser(any(User.class))).thenReturn(Mono.just(userOne));
        when(userMapperDTO.toResponse(any(User.class))).thenReturn(userDTO);

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ApiResponse<UserDTO>>() {})
                .value(response -> Assertions.assertThat(response.getData().getEmail())
                        .isEqualTo(createUserDTO.getEmail()));
    }


    @Test
    void testWhenEmailIsInvalid() {
        CreateUserDTO invalidUserDTO = new CreateUserDTO(
                "Joel",
                "Flores",
                LocalDate.of(1995, 5, 21),
                "Av. Siempre Viva 123",
                "987654321",
                "correo-invalido",
                "12345678",
                new BigDecimal("3500.00"),
                1L
        );

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUserDTO)
                .exchange()
                .expectStatus().isBadRequest(); // Esperamos error de validación
    }
}
