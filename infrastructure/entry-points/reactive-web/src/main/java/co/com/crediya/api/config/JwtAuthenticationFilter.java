package co.com.crediya.api.config;

import co.com.crediya.model.token.gateways.TokenProviderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {


    public JwtAuthenticationFilter(JwtAuthenticationManager authManager) {
        super(authManager);
        setServerAuthenticationConverter(exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                return Mono.just(new UsernamePasswordAuthenticationToken(null, token));
            }
            return Mono.empty();
        });
    }

}
