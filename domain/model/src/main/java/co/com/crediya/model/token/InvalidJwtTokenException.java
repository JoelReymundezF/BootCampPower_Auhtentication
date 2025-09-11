package co.com.crediya.model.token;

public class InvalidJwtTokenException extends RuntimeException {
    public InvalidJwtTokenException(String msg) {
        super(msg);
    }

    public InvalidJwtTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
