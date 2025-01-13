package gr.aueb.cf.finalproject.rest;

import gr.aueb.cf.finalproject.authentication.AuthenticationService;
import gr.aueb.cf.finalproject.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.finalproject.dto.AuthenticationRequestDTO;
import gr.aueb.cf.finalproject.dto.AuthenticationResponseDTO;
import gr.aueb.cf.finalproject.model.User;
import gr.aueb.cf.finalproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthRestController.class);
    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequestDTO authenticationRequestDTO)
    throws AppObjectNotAuthorizedException {
        Optional<User> user = userRepository.findByUsername(authenticationRequestDTO.getUsername());
        if (user.isPresent() && Boolean.FALSE.equals(user.get().getIsActive())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "error", "inactive",
                            "message", "User account is inactive. Please contact support."
                    ));
        }
        AuthenticationResponseDTO authenticationResponseDTO = authenticationService.authenticate(authenticationRequestDTO);
        LOGGER.info("User Authenticated: {}", authenticationResponseDTO);
        return new ResponseEntity<>(authenticationResponseDTO, HttpStatus.OK);
    }
}
