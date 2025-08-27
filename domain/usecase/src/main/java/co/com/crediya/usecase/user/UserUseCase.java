package co.com.crediya.usecase.user;

import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public Mono<User> saveUser(User user) {
        return roleRepository.findById(user.getRoleId())
                .switchIfEmpty(Mono.error(new IllegalArgumentException("The role does not exist")))
                .flatMap(role -> userRepository.existsByEmail(user.getEmail())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new IllegalArgumentException("The email address is already registered"));
                            }
                            return userRepository.save(user);
                        })
                );
    }

    public Mono<Boolean> existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}
