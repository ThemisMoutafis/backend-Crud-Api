package gr.aueb.cf.finalproject.rest;
import gr.aueb.cf.finalproject.core.enums.Role;
import gr.aueb.cf.finalproject.core.exceptions.*;
import gr.aueb.cf.finalproject.dto.AuthenticationResponseDTO;
import gr.aueb.cf.finalproject.dto.UserInsertDTO;
import gr.aueb.cf.finalproject.dto.UserReadOnlyDTO;
import gr.aueb.cf.finalproject.dto.UserUpdateDTO;
import gr.aueb.cf.finalproject.model.User;
import gr.aueb.cf.finalproject.repository.UserRepository;
import gr.aueb.cf.finalproject.security.JwtService;
import gr.aueb.cf.finalproject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.util.Objects;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    /**
     *  Takes a UserInsertDTO post request and saves the user to the database
     * @param userInsertDTO the user information to save
     * @param bindingResult the validation result
     * @return a simple UserReadOnlyDTO with the information of the user that was just saved.
     */
    @PostMapping("/users/save")
    public ResponseEntity<UserReadOnlyDTO> saveUser(@Valid @RequestBody UserInsertDTO userInsertDTO, BindingResult bindingResult)
    throws ValidationException, AppObjectAlreadyExistsException,AppObjectInvalidArgumentException, DataIntegrityViolationException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        UserReadOnlyDTO userReadOnlyDTO = userService.saveUser(userInsertDTO);
        LOGGER.info("Saved user: {}", userInsertDTO.getUsername());
        return new ResponseEntity<>(userReadOnlyDTO, HttpStatus.CREATED);
    }

    /**
     * Updates a user's information through the usage of the username in the PUT request.
     * Some information like the username cannot be changed by design
     * @param username the username the query will be based upon.
     * @param userUpdateDTO the information the request has to pass so that the update will take place.
     * @param bindingResult validation results.
     * @throws ValidationException what happens if validation results have errors written in it.
     * @throws AppObjectInvalidArgumentException if for example, the country name does not match a country from db, or the date of birth is not a real date.
     * @throws AppObjectNotAuthorizedException only the already authenticated user may use this service since it's updating personal information
     * like passwords.
     * @throws AppObjectNotFoundException makes sure the username will match a user in the database.
     * @throws AppObjectAlreadyExistsException if user tries , for example, to change his email to an email that already exists.
     */
    @PutMapping("/user/{username}/update")
    public ResponseEntity<AuthenticationResponseDTO> updateUser(@PathVariable("username") String username,
                                                      @Valid @RequestBody UserUpdateDTO userUpdateDTO, BindingResult bindingResult)
            throws ValidationException, AppObjectInvalidArgumentException, DataIntegrityViolationException, AppObjectNotAuthorizedException, AppObjectNotFoundException, AppObjectAlreadyExistsException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!username.equals(authenticatedUsername)) {
            throw new AppObjectNotAuthorizedException("User Update","not authorized");
        }
        UserReadOnlyDTO updatedUser = userService.updateUser(username,userUpdateDTO);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppObjectNotFoundException("User","not found"));

        AuthenticationResponseDTO authenticationResponseDTO = new AuthenticationResponseDTO();
        String token =jwtService.generateToken(user.getUsername(), user.getRole().name(), user.getFirstname(),user.getLastname(), user.getEmail(), user.getBirthdate().toString(), user.getCountry().getName());
        authenticationResponseDTO.setToken(token);
        LOGGER.info("User updated: {}", updatedUser);
        return ResponseEntity.ok(authenticationResponseDTO);
        }

    /**
     * deactivating a user to avoid permanent delete. same logic as  {@link #updateUser(String, UserUpdateDTO, BindingResult)}, but this one can also be done by admin role authority.
     * @throws AppObjectNotAuthorizedException admin role or authenticated user required.
     */
    @PutMapping("/user/{username}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable("username") String username) throws AppObjectNotAuthorizedException, AppObjectInvalidArgumentException, AppObjectNotFoundException {

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        if ((!username.equals(authenticatedUsername)) && !Objects.equals(userRole, Role.ADMIN.toString())) {
            throw new AppObjectNotAuthorizedException("User deletion ","not authorized");
        }
        UserReadOnlyDTO updatedUser = userService.setUserInactive(username);
        LOGGER.info("User deactivated: {}", updatedUser);
        return ResponseEntity.noContent().build();
    }

    /**
     * deleting a user. while deactivating is an option, nevertheless, a delete option is there for admin users.
     * @throws AppObjectNotAuthorizedException admin role is required here.
     */
    @DeleteMapping("/user/{id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws AppObjectNotAuthorizedException,AppObjectInvalidArgumentException,AppObjectNotFoundException {
        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        if (!Objects.equals(userRole, Role.ADMIN.toString())) {
            throw new AppObjectNotAuthorizedException("User deletion ","not authorized");
        }
        userService.deleteUser(id);
        LOGGER.info("User deleted: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     *activating a user to avoid permanent delete. same logic as  {@link #updateUser(String, UserUpdateDTO, BindingResult)}, but this one can also be done by admin role authority.
     * @throws AppObjectNotAuthorizedException admin role, or authenticated user required.
     */
    @PutMapping("/user/{username}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable("username") String username) throws AppObjectNotAuthorizedException, AppObjectInvalidArgumentException, AppObjectNotFoundException {

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        if ((!username.equals(authenticatedUsername)) && !Objects.equals(userRole, Role.ADMIN.toString())) {
            throw new AppObjectNotAuthorizedException("User activation ","not authorized");
        }
        UserReadOnlyDTO updatedUser = userService.setUserActive(username);
        LOGGER.info("User activated: {}", updatedUser);
        return ResponseEntity.noContent().build();
    }

    /**
     *  produces a paginated list of all the users.
     * @param page the page that is returned
     * @param size the size of each page when generated.
     */
    @GetMapping("/users")
    public ResponseEntity<Page<UserReadOnlyDTO>> getPaginatedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
            ) {

        Page<UserReadOnlyDTO> usersPage = userService.getPaginatedUsers(page, size);
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
    }
}
