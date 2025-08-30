package co.com.crediya.model.user.exceptions;

public class BootcampUserAlreadyExistsException extends RuntimeException {
    public BootcampUserAlreadyExistsException(String email, String doc) {
        super(String.format("The email '%s' or identity document '%s' is already registered", email, doc));
    }
}