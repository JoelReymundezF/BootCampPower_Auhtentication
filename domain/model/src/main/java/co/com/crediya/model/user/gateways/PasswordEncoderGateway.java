package co.com.crediya.model.user.gateways;

public interface PasswordEncoderGateway {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
