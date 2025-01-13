package gr.aueb.cf.finalproject.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserUpdateDTO {

    @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$",
            message = "Invalid Password")
    private String password;
    @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$",
            message = "Invalid Password")
    private String oldPassword;
    @NotEmpty(message ="firstname name is required")
    private String firstname;
    @NotEmpty(message ="lastname name is required")
    private String lastname;
    @Email(message = "invalid email")
    @NotEmpty(message = "email is required")
    private String email;
    @NotNull(message = "birthdate is required")
    private LocalDate birthdate;
    @NotNull(message ="Country is required")
    private String countryName;
}
