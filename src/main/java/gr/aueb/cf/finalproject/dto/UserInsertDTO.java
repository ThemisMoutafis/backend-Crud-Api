package gr.aueb.cf.finalproject.dto;

import gr.aueb.cf.finalproject.core.enums.Role;
import gr.aueb.cf.finalproject.model.static_data.Country;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserInsertDTO {
    @NotEmpty(message ="username name is required")
    private String username;

    @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?\\d)(?=.*?[@#$!%&*]).{8,}$",
            message = "Invalid Password")
    private String password;

    @NotEmpty(message ="firstname name is required")
    private String firstname;
    @NotEmpty(message ="lastname name is required")
    private String lastname;
    @Email(message = "invalid email")
    private String email;
    @NotNull(message = "birthdate is required")
    private LocalDate birthdate;

    @NotNull(message ="Country is required")
    private String countryName;
}
