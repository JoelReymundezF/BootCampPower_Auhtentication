package co.com.crediya.usecase.user;

import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.token.gateways.TokenProviderGateway;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.BootcampRoleNotFoundException;
import co.com.crediya.model.user.exceptions.BootcampUserAlreadyExistsException;
import co.com.crediya.model.user.gateways.PasswordEncoderGateway;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoderGateway passwordEncoderGateway;
    private final TokenProviderGateway tokenProvider;

    public Mono<User> saveUser(User user) {
        return roleRepository.existsById(user.getRoleId())
                .flatMap(existsRole -> {
                    if (!existsRole) {
                        return Mono.error(new BootcampRoleNotFoundException(user.getRoleId()));
                    }
                    return userRepository.existsByEmailOrIdentityDocument(user.getEmail(), user.getIdentityDocument())
                            .flatMap(existsUser -> {
                                if (existsUser) {
                                    return Mono.error(new BootcampUserAlreadyExistsException(
                                            user.getEmail(), user.getIdentityDocument()));
                                }
                                User userToSave = user.toBuilder()
                                        .password(passwordEncoderGateway.encode(user.getPassword()))
                                        .build();
                                return userRepository.save(userToSave);
                            });
                });
    }


    public Mono<Boolean> existsByDocument(String identityDocument) {
        return userRepository.existsByIdentityDocument(identityDocument);
    }

    public Mono<String> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .flatMap(user -> {
                    if (!passwordEncoderGateway.matches(rawPassword, user.getPassword())) {
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }
                    return tokenProvider.generateToken(user);
                });
    }

}
