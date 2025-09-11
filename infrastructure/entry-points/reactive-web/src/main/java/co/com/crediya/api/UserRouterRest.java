package co.com.crediya.api;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class UserRouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/users",
                    beanClass = UserHandler.class,
                    beanMethod = "listenSaveUser"
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    beanClass = UserHandler.class,
                    beanMethod = "listenLogin"
            )
    })
    public RouterFunction<ServerResponse> routerFunction(UserHandler userHandler) {
        return route(POST("/api/v1/users"), userHandler::listenSaveUser)
                .andRoute(GET("/api/v1/users/existsByDocument/{identityDocument}"), userHandler::listenExistsByDocument)
                .andRoute(POST("/api/v1/login"), userHandler::listenLogin);
    }
}
