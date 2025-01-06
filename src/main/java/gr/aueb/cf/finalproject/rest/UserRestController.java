package gr.aueb.cf.finalproject.rest;


import gr.aueb.cf.finalproject.core.enums.Role;
import gr.aueb.cf.finalproject.core.exceptions.*;

import gr.aueb.cf.finalproject.dto.UserInsertDTO;
import gr.aueb.cf.finalproject.dto.UserReadOnlyDTO;
import gr.aueb.cf.finalproject.dto.UserUpdateDTO;
import gr.aueb.cf.finalproject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;


    @PostMapping("/users/save")
    public ResponseEntity<UserReadOnlyDTO> saveUser(@Valid @RequestBody UserInsertDTO userInsertDTO, BindingResult bindingResult)
    throws ValidationException, AppObjectAlreadyExistsException,AppObjectInvalidArgumentException, DataIntegrityViolationException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        UserReadOnlyDTO userReadOnlyDTO = userService.saveUser(userInsertDTO);
        return new ResponseEntity<>(userReadOnlyDTO, HttpStatus.CREATED);
    }

    @PutMapping("/user/{username}/update")
    public ResponseEntity<UserReadOnlyDTO> updateUser(@PathVariable("username") String username,
                                                      @RequestBody UserUpdateDTO userUpdateDTO, BindingResult bindingResult)
            throws ValidationException, AppObjectInvalidArgumentException, DataIntegrityViolationException, AppObjectNotAuthorizedException, AppObjectNotFoundException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!username.equals(authenticatedUsername)) {
            throw new AppObjectNotAuthorizedException("User Update","not authorized");
        }
        UserReadOnlyDTO updatedUser = userService.updateUser(username,userUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/user/{username}/delete")
    public ResponseEntity<UserReadOnlyDTO> deleteUser(@PathVariable("username") String username) throws AppObjectNotAuthorizedException, AppObjectInvalidArgumentException, AppObjectNotFoundException {

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        if ((!username.equals(authenticatedUsername)) && !Objects.equals(userRole, Role.ADMIN.toString())) {
            throw new AppObjectNotAuthorizedException("User deletion ","not authorized");
        }
        UserReadOnlyDTO updatedUser = userService.setUserInactive(username);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/user/{username}/activate")
    public ResponseEntity<UserReadOnlyDTO> activateUser(@PathVariable("username") String username) throws AppObjectNotAuthorizedException, AppObjectInvalidArgumentException, AppObjectNotFoundException {

        String authenticatedUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        String userRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();

        if ((!username.equals(authenticatedUsername)) && !Objects.equals(userRole, Role.ADMIN.toString())) {
            throw new AppObjectNotAuthorizedException("User activation ","not authorized");
        }
        UserReadOnlyDTO updatedUser = userService.setUserActive(username);
        return ResponseEntity.ok(updatedUser);
    }
    @GetMapping("/users")
    public ResponseEntity<Page<UserReadOnlyDTO>> getPaginatedUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Page<UserReadOnlyDTO> usersPage = userService.getPaginatedUsers(page, size);
        return new ResponseEntity<>(usersPage, HttpStatus.OK);
    }
}
