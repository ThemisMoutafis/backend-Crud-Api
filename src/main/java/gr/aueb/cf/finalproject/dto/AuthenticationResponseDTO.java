package gr.aueb.cf.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDTO {
    private String firstname;
    private String email;
    private LocalDate dateOfBirth;
    private String countryName;
    private String token;
}
