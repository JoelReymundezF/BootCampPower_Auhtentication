package co.com.crediya.model.exeption;

public enum BootcampRuleCode {

    USER_ALREADY_EXISTS("USER_EXISTS", "User with email or document already exists"),
    USER_NOT_FOUND("USER_NOT_FOUND", "User not found"),
    ROLE_NOT_FOUND("ROLE_NOT_FOUND", "The role assigned does not exist"),
    INVALID_PASSWORD("INVALID_PASSWORD", "Password does not meet the requirements"),
    INCORRECT_PASSWORD("INCORRECT_PASSWORD", "Incorrect password"),
    LOAN_TYPE_NOT_FOUND("LOAN_TYPE_NOT_FOUND", "Loan type does not exist"),
    INVALID_DOCUMENT("INVALID_DOCUMENT", "The document is not valid");

    private final String code;
    private final String message;

    BootcampRuleCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}