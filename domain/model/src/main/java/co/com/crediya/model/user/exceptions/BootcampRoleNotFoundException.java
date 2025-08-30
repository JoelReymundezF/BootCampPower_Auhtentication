package co.com.crediya.model.user.exceptions;

public class BootcampRoleNotFoundException extends RuntimeException {
    public BootcampRoleNotFoundException(Long roleId) {
        super("The role with id " + roleId + " does not exist");
    }
}