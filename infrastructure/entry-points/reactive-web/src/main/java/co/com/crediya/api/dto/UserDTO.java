package co.com.crediya.api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDTO {

    private String firstName;

    private String lastName;

    private LocalDate birthDate;

    private String identityDocument;

    private String address;

    private String phone;

    private String email;

    private BigDecimal baseSalary;

    private Long roleId;
}