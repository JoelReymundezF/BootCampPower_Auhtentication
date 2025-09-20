package co.com.crediya.model.exeption;


public class BootcampRuleException extends RuntimeException {

    private final BootcampRuleCode ruleCode;

    public BootcampRuleException(BootcampRuleCode ruleCode) {
        super(ruleCode.getMessage());
        this.ruleCode = ruleCode;
    }

    public String getCode() {
        return ruleCode.getCode();
    }
}
