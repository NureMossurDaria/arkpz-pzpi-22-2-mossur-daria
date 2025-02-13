package ua.nure.mossurd.blooddosyst.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;
import ua.nure.mossurd.blooddosyst.dto.LoginRequestDto;
import ua.nure.mossurd.blooddosyst.dto.UserDto;
import ua.nure.mossurd.blooddosyst.enums.UserType;
import ua.nure.mossurd.blooddosyst.security.service.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JdbcUserDetailsManager jdbcUserDetailsManager;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public UserDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.username(), loginRequestDto.password())
        );
        String jwtToken = jwtService.generateToken(authentication);

        return new UserDto(loginRequestDto.username(), jwtToken);
    }

    public void createUser(String username, UserType userType) {
        jdbcUserDetailsManager.createUser(User.builder()
                .username(username)
                .password(passwordEncoder.encode("changeme"))
                .roles(userType.name())
                .build());
    }
}
