package co.com.crediya.api;

import co.com.crediya.api.dto.CreateUserDTO;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.api.helper.ApiResponse;
import co.com.crediya.api.helper.validation.ValidationUtil;
import co.com.crediya.api.mapper.UserMapperDTO;
import co.com.crediya.usecase.user.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
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
    private  final UserUseCase userUseCase;
    private  final UserMapperDTO userMapper;
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

}
