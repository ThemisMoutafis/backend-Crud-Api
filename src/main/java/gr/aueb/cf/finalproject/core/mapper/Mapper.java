package gr.aueb.cf.finalproject.core.mapper;

import gr.aueb.cf.finalproject.core.enums.Role;
import gr.aueb.cf.finalproject.dto.UserInsertDTO;
import gr.aueb.cf.finalproject.dto.UserReadOnlyDTO;
import gr.aueb.cf.finalproject.dto.UserUpdateDTO;
import gr.aueb.cf.finalproject.model.User;
import gr.aueb.cf.finalproject.model.static_data.Country;
import gr.aueb.cf.finalproject.repository.CountryRepository;
import gr.aueb.cf.finalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


/**
 * Doing all the necessary mapping, assigning DTOs to user entities and the reverse.
 */
@Component
@RequiredArgsConstructor
public class Mapper {

    private final CountryRepository countryRepository;
    private final UserRepository userRepository;

    public User mapToUserEntity(UserInsertDTO userInsertDTO) {
        User user = new User();
        user.setFirstname(userInsertDTO.getFirstname());
        user.setLastname(userInsertDTO.getLastname());
        user.setEmail(userInsertDTO.getEmail());
        user.setUsername(userInsertDTO.getUsername());
        user.setPassword(userInsertDTO.getPassword());
        user.setRole(Role.USER);
        user.setBirthdate(userInsertDTO.getBirthdate());
        // Fetch the Country by ID from the database (or handle null cases)
        Country country = countryRepository.findByName(userInsertDTO.getCountryName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid country ID"));
        user.setCountry(country);
        return user;
    }
    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(user.getId(),user.getUsername(),user.getFirstname(),user.getLastname(),user.getEmail(),user.getCountry().getName(), user.getIsActive());
    }

    public User mapToUpdateUserEntity(UserUpdateDTO userUpdateDTO, User user)  {
        user.setFirstname(userUpdateDTO.getFirstname());
        user.setLastname(userUpdateDTO.getLastname());
        user.setEmail(userUpdateDTO.getEmail());
        Country country = countryRepository.findByName(userUpdateDTO.getCountryName())
                .orElseThrow(() -> new IllegalArgumentException("Invalid country ID"));
        user.setCountry(country);
        user.setPassword(userUpdateDTO.getPassword());
        user.setBirthdate(userUpdateDTO.getBirthdate());

        return user;
    }
}
