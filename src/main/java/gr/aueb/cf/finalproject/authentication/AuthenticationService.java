package gr.aueb.cf.finalproject.authentication;

import gr.aueb.cf.finalproject.core.exceptions.AppObjectNotAuthorizedException;
import gr.aueb.cf.finalproject.dto.AuthenticationRequestDTO;
import gr.aueb.cf.finalproject.dto.AuthenticationResponseDTO;
import gr.aueb.cf.finalproject.model.User;
import gr.aueb.cf.finalproject.repository.UserRepository;
import gr.aueb.cf.finalproject.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO)
    throws AppObjectNotAuthorizedException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDTO.getUsername(),
                        authenticationRequestDTO.getPassword())
        );

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppObjectNotAuthorizedException("User","User not found"));

        String token = jwtService.generateToken(authentication.getName(),user.getRole().name(),user.getFirstname(),user.getLastname(),user.getEmail(),user.getBirthdate().toString(),user.getCountry().getName());
        return new AuthenticationResponseDTO(token);
    }
}
