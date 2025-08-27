package co.com.crediya.model.user;

import co.com.crediya.model.role.Role;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

	private String firstName;

	private String lastName;

	private LocalDate birthDate;

	private String address;

	private String identityDocument;

	private String phone;

	private String email;

	private BigDecimal baseSalary;

	private Long roleId;
}
