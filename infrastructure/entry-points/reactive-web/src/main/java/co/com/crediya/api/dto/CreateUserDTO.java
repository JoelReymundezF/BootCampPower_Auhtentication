package co.com.crediya.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    private String address;

    private String phone;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Identity Document is required")
    private String identityDocument;

    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0", message = "Base salary must be >= 0")
    @DecimalMax(value = "15000000", message = "Base salary must be <= 15000000")
    private BigDecimal baseSalary;

    @NotNull(message = "Role id is required")
    private Long roleId;
}