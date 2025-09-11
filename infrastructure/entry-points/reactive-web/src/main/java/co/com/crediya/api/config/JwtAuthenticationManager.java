package co.com.crediya.api.config;

import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.token.gateways.TokenProviderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final TokenProviderGateway tokenProvider;
    private final RoleRepository roleRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return tokenProvider.validateToken(token)
                .flatMap(valid -> {
                    if (!valid) {
                        return Mono.error(new BadCredentialsException("Invalid token"));
                    }
                    return tokenProvider.getUserFromToken(token)
                            .flatMap(user -> roleRepository.findById(user.getRoleId())
                                    .map(role -> {
                                        List<GrantedAuthority> authorities = List.of(
                                                new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase())
                                        );
                                        return new UsernamePasswordAuthenticationToken(user, null, authorities);
                                    })
                            );
                });

    }
}
