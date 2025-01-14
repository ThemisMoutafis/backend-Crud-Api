package gr.aueb.cf.finalproject.service;

import gr.aueb.cf.finalproject.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.finalproject.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.finalproject.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.finalproject.core.exceptions.AppObjectNotFoundException;
import gr.aueb.cf.finalproject.core.mapper.Mapper;
import gr.aueb.cf.finalproject.dto.UserInsertDTO;
import gr.aueb.cf.finalproject.dto.UserReadOnlyDTO;
import gr.aueb.cf.finalproject.dto.UserUpdateDTO;
import gr.aueb.cf.finalproject.model.User;
import gr.aueb.cf.finalproject.model.static_data.Country;
import gr.aueb.cf.finalproject.repository.CountryRepository;
import gr.aueb.cf.finalproject.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final Mapper mapper;
    private final CountryRepository countryRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO saveUser(UserInsertDTO userInsertDTO)
    throws AppObjectAlreadyExistsException , AppObjectInvalidArgumentException {
        Country country = countryRepository.findByName(userInsertDTO.getCountryName())
                .orElseThrow(() -> new AppObjectInvalidArgumentException("Country","Invalid country name."));

        if (userRepository.findByUsername(userInsertDTO.getUsername()).isPresent()) {
            throw new AppObjectAlreadyExistsException("Username", userInsertDTO.getUsername() + " already exists.");
        }

        if (userRepository.findByEmail(userInsertDTO.getEmail()).isPresent()) {
            throw new AppObjectAlreadyExistsException("Email", userInsertDTO.getEmail() + " already exists.");
        }

        try {
            User user = mapper.mapToUserEntity(userInsertDTO);
            user.setPassword(passwordEncoder.encode(userInsertDTO.getPassword()));
            user.setIsActive(true);
            // uncomment to make an admin, comment otherwise
//            user.setRole(Role.ADMIN);
            userRepository.save(user);
            return mapper.mapToUserReadOnlyDTO(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new AppObjectInvalidArgumentException("Error saving user: ", "invalid argument");
        }


    }

    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO updateUser(String username,UserUpdateDTO userUpdateDTO)
            throws AppObjectNotFoundException, AppObjectInvalidArgumentException, AppObjectAlreadyExistsException {

        User user  = userRepository.findByUsername(username).orElseThrow(() -> new AppObjectNotFoundException("Username", "Username not found"));
        if (userRepository.findByEmail(userUpdateDTO.getEmail()).isPresent()
                && !user.getEmail().equals(userUpdateDTO.getEmail())) {
            throw new AppObjectAlreadyExistsException("Email", userUpdateDTO.getEmail() + " already exists.");
        }
        if (!(passwordEncoder.matches(userUpdateDTO.getOldPassword(), user.getPassword()))) {
            throw new AppObjectInvalidArgumentException("password", "password does not match.");
        }
        Country country = countryRepository.findByName(userUpdateDTO.getCountryName())
                .orElseThrow(() -> new AppObjectInvalidArgumentException("Country","Invalid country name."));
        if (Objects.equals(userUpdateDTO.getOldPassword(), userUpdateDTO.getPassword())) {
            throw new AppObjectAlreadyExistsException("Password","Password already exists.");
        }
        try {
            User userToUpdate = mapper.mapToUpdateUserEntity(userUpdateDTO,user);
            if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
            } else if (userUpdateDTO.getOldPassword() != null && !userUpdateDTO.getOldPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userUpdateDTO.getOldPassword()));
            }
            userRepository.save(userToUpdate);
            return mapper.mapToUserReadOnlyDTO(userToUpdate);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new AppObjectInvalidArgumentException("Error saving user: ", "invalid argument");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO setUserInactive(String username)
            throws AppObjectNotFoundException, AppObjectInvalidArgumentException, AppObjectNotAuthorizedException {

        User user  = userRepository.findByUsername(username).orElseThrow(() -> new AppObjectNotFoundException("Username", "Username not found"));
        try {
            user.setIsActive(false);
            userRepository.save(user);
            return mapper.mapToUserReadOnlyDTO(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new AppObjectInvalidArgumentException("Error setting inactive user: ", "invalid argument");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO setUserActive(String username)
            throws AppObjectNotFoundException, AppObjectInvalidArgumentException {

        User user  = userRepository.findByUsername(username).orElseThrow(() -> new AppObjectNotFoundException("Username", "Username not found"));
        try {
            user.setIsActive(true);
            userRepository.save(user);
            return mapper.mapToUserReadOnlyDTO(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new AppObjectInvalidArgumentException("Error setting active user: ", "invalid argument");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public void deleteUser(Long id) throws AppObjectNotFoundException, AppObjectInvalidArgumentException {

        User user = userRepository.findById(id).orElseThrow(() -> new AppObjectNotFoundException("Username", "Username not found"));
        try {
            userRepository.delete(user);
            mapper.mapToUserReadOnlyDTO(user);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new AppObjectInvalidArgumentException("Error deleting user: ", "invalid argument");
        }
    }
    @Transactional
    public List<User> getUsersByCountry(Country country){
        return userRepository.findByCountry(country);
    }

    @Transactional
    public Page<UserReadOnlyDTO> getPaginatedUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(mapper::mapToUserReadOnlyDTO);
    }


}
