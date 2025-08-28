package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final Handler userHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    beanClass = Handler.class,
                    beanMethod = "listenSaveUser"
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler userHandler) {
        return route(POST("/api/v1/users"), userHandler::listenSaveUser);
    }
}
