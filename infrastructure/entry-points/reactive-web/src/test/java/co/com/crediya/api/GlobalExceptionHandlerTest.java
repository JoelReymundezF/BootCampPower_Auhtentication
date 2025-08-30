package co.com.crediya.api;

import co.com.crediya.model.user.exceptions.BootcampRoleNotFoundException;
import co.com.crediya.model.user.exceptions.BootcampUserAlreadyExistsException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.AssertionErrors;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ServerWebExchange exchange;
    private DataBufferFactory bufferFactory;
    private DataBuffer buffer;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        exchange = mock(ServerWebExchange.class, RETURNS_DEEP_STUBS);
        when(exchange.getResponse().setComplete()).thenReturn(Mono.empty());
    }

    @Test
    void handleRoleNotFoundException() {
        BootcampRoleNotFoundException ex = new BootcampRoleNotFoundException(22L);
        handler.handle(exchange, ex).block();
        verify(exchange.getResponse()).setStatusCode(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleUserAlreadyExistsException() {
        BootcampUserAlreadyExistsException ex = new BootcampUserAlreadyExistsException("email", "doc");
        handler.handle(exchange, ex).block();
        verify(exchange.getResponse()).setStatusCode(HttpStatus.CONFLICT);
    }

    @Test
    void handleDefaultException() {
        RuntimeException ex = new RuntimeException("something went wrong");
        handler.handle(exchange, ex).block();
        verify(exchange.getResponse()).setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void handleWhenResponseCommitted() {
        when(exchange.getResponse().isCommitted()).thenReturn(true);
        RuntimeException ex = new RuntimeException("Test exception");
        Mono<Void> result = handler.handle(exchange, ex);
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Test exception")
                )
                .verify();
    }


}
