package co.com.crediya.api.config;

import co.com.crediya.api.UserHandler;
import co.com.crediya.api.UserRouterRest;
import co.com.crediya.api.helper.validation.ValidationUtil;
import co.com.crediya.api.mapper.UserMapperDTO;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {UserRouterRest.class, UserHandler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserMapperDTO userMapper;

    @MockitoBean
    private ValidationUtil validationUtil;

    @Test
    void corsConfigurationShouldAllowOrigins() {

        Mockito.when(userUseCase.saveUser(Mockito.any()))
                .thenReturn(Mono.just(new User()));

        webTestClient.post()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}