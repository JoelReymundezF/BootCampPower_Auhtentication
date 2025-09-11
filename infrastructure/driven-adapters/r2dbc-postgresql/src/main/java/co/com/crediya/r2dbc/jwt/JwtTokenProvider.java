package co.com.crediya.r2dbc.jwt;

import co.com.crediya.model.role.Role;
import co.com.crediya.model.role.gateways.RoleRepository;
import co.com.crediya.model.token.InvalidJwtTokenException;
import co.com.crediya.model.token.gateways.TokenProviderGateway;
import co.com.crediya.model.user.User;
import co.com.crediya.model.user.gateways.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtTokenProvider implements TokenProviderGateway {

    private final SecretKey secretKey;
    private final long validityInMs;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration}") long validityInMs,
            UserRepository userRepository,
            RoleRepository roleRepository
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.validityInMs = validityInMs;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public Mono<String> generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMs);

        return roleRepository.findById(user.getRoleId())
                .map(role -> Jwts.builder()
                        .setSubject(user.getIdentityDocument())
                        .claim("role", role.getName())
                        .setIssuedAt(now)
                        .setExpiration(expiry)
                        .signWith(secretKey, SignatureAlgorithm.HS256)
                        .compact()
                );
    }

    public void init() {
        System.out.println("JWT Secret length: " + secretKey.getEncoded().length);
        System.out.println("JWT Expiration: " + validityInMs);
    }
    @Override
    public Mono<Boolean> validateToken(String token) {
        return Mono.fromSupplier(() -> {
            try {
                Jwts.parserBuilder()
                        .setSigningKey(secretKey)
                        .build()
                        .parseClaimsJws(token);;
                return true;
            } catch (JwtException | IllegalArgumentException e){
                log.info("Invalid Jwt Token : {}",e.getMessage());
                log.info("Invalid Jwt Token trace.",e);
                return false;
            }
        });
    }

    @Override
    public Mono<String> getUsernameFromToken(String token) {
        return Mono.fromSupplier(() -> {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        });
    }

    @Override
    public Mono<User> getUserFromToken(String token) {
        return getUsernameFromToken(token)
                .flatMap(userRepository::findByIdentityDocument);
    }
}
