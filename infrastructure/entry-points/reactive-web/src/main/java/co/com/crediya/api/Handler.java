package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserDTO;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.api.helper.ApiResponse;
import co.com.crediya.api.mapper.UserMapperDTO;
import co.com.crediya.model.role.Role;
import co.com.crediya.model.user.User;
import co.com.crediya.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private  final UserUseCase userUseCase;
    private  final UserMapperDTO userMapper;
    private final Validator validator;
//private  final UseCase2 useCase2;
 //agregar doc swagger
    @Operation(
            operationId = "listenSaveUser",
            summary = "Register new user",
            description = "Receive a UserRequestDTO object and store a user in the system.",
            requestBody = @RequestBody(
                    required = true,
                    description = "User data to be registered",
                    content = @Content(schema = @Schema(implementation = CreateUserDTO.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            }
    )
    public Mono<ServerResponse> listenSaveUser(ServerRequest request) {
        return request.bodyToMono(CreateUserDTO.class)
                .doOnNext(dto -> log.info("Received request to save user"))
                .flatMap(dto -> {//pasar a metodo para reutilizar
                    Set<ConstraintViolation<CreateUserDTO>> violations = validator.validate(dto);
                    if (!violations.isEmpty()) {
                        return Mono.error(new ConstraintViolationException(violations));
                    }
                    return Mono.just(dto);
                })
                .map(userMapper::toModel)
                .flatMap(userUseCase::saveUser)
                .map(userMapper::toResponse)
                .flatMap(savedUserDto -> {
                    ApiResponse<UserDTO> response = new ApiResponse<>(
                            HttpStatus.CREATED.value(),
                            savedUserDto
                    );
                    return ServerResponse
                            .status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .doOnNext(dto -> log.info("User created successfully"));
    }

    public Mono<ServerResponse> listenGETUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        // useCase2.logic();
        return ServerResponse.ok().bodyValue("");
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }
}
