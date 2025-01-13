package gr.aueb.cf.finalproject.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserReadOnlyDTO {
    private long id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String countryName;
    private boolean isActive;

    @Override
    public String toString() {
        return "UserReadOnlyDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", countryName='" + countryName + '\'' +
                ", isActive=" + isActive +
                '}';
    }

}


