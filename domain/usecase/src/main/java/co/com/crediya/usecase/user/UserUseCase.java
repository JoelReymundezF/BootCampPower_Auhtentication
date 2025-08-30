package co.com.crediya.usecase.user;

import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.exceptions.BootcampRoleNotFoundException;
import co.com.crediya.model.user.exceptions.BootcampUserAlreadyExistsException;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

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
                                return userRepository.save(user);
                            });
                });
    }


}
