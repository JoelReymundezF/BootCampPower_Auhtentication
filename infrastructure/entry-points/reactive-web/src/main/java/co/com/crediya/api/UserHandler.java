package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserDTO;
import co.com.crediya.api.dto.LoginRequestDTO;
import co.com.crediya.api.dto.LoginResponseDTO;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.api.helper.ApiResponse;
import co.com.crediya.api.helper.validation.ValidationUtil;
import co.com.crediya.api.mapper.UserMapperDTO;
import co.com.crediya.usecase.login.LoginUseCase;
import co.com.crediya.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {
    private final UserUseCase userUseCase;
    private final LoginUseCase loginUseCase;
    private final UserMapperDTO userMapper;
    private final ValidationUtil validationUtil;

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
                .flatMap(validationUtil::validate)
                .map(userMapper::toModel)
                .flatMap(userUseCase::saveUser)
                .map(userMapper::toResponse)
                .flatMap(savedUserDto -> {
                    ApiResponse<UserDTO> response = new ApiResponse<>(
                            GlobalExceptionHandler.CREATED,
                            savedUserDto
                    );
                    return ServerResponse
                            .status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .doOnNext(dto -> log.info("User created successfully"));
    }

    @Operation(
            operationId = "listenExistsByDocument",
            summary = "Check if a user exists by document",
            description = "Validates if a user exists in the system using the provided identity document.",
            parameters = {
                    @Parameter(
                            name = "identityDocument",
                            description = "Unique identity document number of the user",
                            required = true,
                            example = "12345678"
                    )
            },
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "User existence checked successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    )
            }
    )
    public Mono<ServerResponse> listenExistsByDocument(ServerRequest request) {
        String identityDocument = request.pathVariable("identityDocument");
        log.info("Received request to identityDocument user :: {}", identityDocument);
        return userUseCase.existsByDocument(identityDocument)
                .flatMap(exists -> {
                    ApiResponse<Boolean> response = new ApiResponse<>(
                            GlobalExceptionHandler.OK,
                            exists
                    );
                    return ServerResponse
                            .status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .doOnNext(dto -> log.info("Verify exists user successfully"));
    }

    @Operation(
            operationId = "listenLogin",
            summary = "Authenticate user and generate token",
            description = "Receives login credentials (email and password), validates them, and returns a JWT token if authentication is successful.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Login credentials",
                    content = @Content(schema = @Schema(implementation = LoginRequestDTO.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Login successful, token returned",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "400",
                            description = "Invalid credentials or validation error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid email or password",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class))
                    )
            }
    )
    public Mono<ServerResponse> listenLogin(ServerRequest request) {
        return request.bodyToMono(LoginRequestDTO.class)
                .flatMap(validationUtil::validate)
                .flatMap(dto -> loginUseCase.login(dto.getEmail(), dto.getPassword()))
                .flatMap(token -> {
                            ApiResponse<String> response = new ApiResponse<>(
                                    GlobalExceptionHandler.OK,
                                    token
                            );
                            return ServerResponse.ok()
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(response);
                        }
                ).doOnError(e -> {
                    log.error("Error en login: {}", e.getMessage());
                });
    }

}
