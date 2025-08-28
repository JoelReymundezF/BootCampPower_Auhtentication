package co.com.crediya.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());

        if (ex instanceof ConstraintViolationException validationEx) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);

            Map<String, String> errors = validationEx.getConstraintViolations()
                    .stream()
                    .collect(Collectors.toMap(
                            cv -> cv.getPropertyPath().toString(),
                            ConstraintViolation::getMessage,
                            (m1, m2) -> m1
                    ));

            body.put("status", HttpStatus.BAD_REQUEST.value());// no repetir codigo
            body.put("errors", errors);

        } else if (ex instanceof IllegalArgumentException) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            body.put("status", HttpStatus.BAD_REQUEST.value());
            body.put("error", ex.getMessage());

        } else if (ex instanceof DuplicateKeyException) {
            exchange.getResponse().setStatusCode(HttpStatus.CONFLICT);
            body.put("status", HttpStatus.CONFLICT.value());
            body.put("error", "The resource already exists or violates a unique constraint.");

        } else {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            body.put("error", ex.getMessage());
        }

        return writeResponse(exchange, body);
    }


    private Mono<Void> writeResponse(ServerWebExchange exchange, Map<String, Object> body) {
        try {
            byte[] bytes = objectMapper.writeValueAsBytes(body);
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap(bytes)));
        } catch (Exception e) {
            return exchange.getResponse()
                    .writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory()
                            .wrap("{\"status\":500,\"error\":\"Error serializando respuesta\"}".getBytes())));
        }
    }
}