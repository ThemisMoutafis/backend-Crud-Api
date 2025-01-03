package gr.aueb.cf.finalproject.rest;

import gr.aueb.cf.finalproject.core.exceptions.AppObjectAlreadyExistsException;
import gr.aueb.cf.finalproject.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.finalproject.core.exceptions.ValidationException;
import gr.aueb.cf.finalproject.core.mapper.Mapper;
import gr.aueb.cf.finalproject.dto.UserInsertDTO;
import gr.aueb.cf.finalproject.dto.UserReadOnlyDTO;
import gr.aueb.cf.finalproject.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userService;
    private final Mapper mapper;

    @PostMapping("/users/save")
    public ResponseEntity<UserReadOnlyDTO> saveUser(@Valid @RequestBody UserInsertDTO userInsertDTO, BindingResult bindingResult)
    throws ValidationException, AppObjectAlreadyExistsException,AppObjectInvalidArgumentException, DataIntegrityViolationException {
        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        UserReadOnlyDTO userReadOnlyDTO = userService.saveUser(userInsertDTO);
        return new ResponseEntity<>(userReadOnlyDTO, HttpStatus.CREATED);
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
