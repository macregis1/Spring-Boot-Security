package com.regis.security.security.auth;

import com.regis.security.security.config.JwtService;
import com.regis.security.user.Role;
import com.regis.security.user.User;
import com.regis.security.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user, user.getFirstName(), String.valueOf(user.getRole()));
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
    public AuthenticationResponse updating(UpdateRequest request) {
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setId(user.getId());
        user.setFirstName(request.getFirstname() != null ? request.getFirstname() : user.getFirstName());
        user.setLastName(request.getLastname() != null ? request.getLastname() : user.getLastName());
        user.setEmail(request.getEmail() != null ? request.getEmail() : user.getEmail());
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getRole() != null) {
            user.setRole(Role.valueOf(request.getRole()));
        }
        repository.save(user);
        var jwtToken = jwtService.generateToken(user, user.getFirstName(), String.valueOf(user.getRole()));
        return AuthenticationResponse.builder().token(jwtToken).names(user.getFirstName()).roles(user.getRole().toString()).build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var names = user.getFirstName();
        var role = user.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found"))
                .getAuthority();
        var jwtToken = jwtService.generateToken(user, names, role);
        return AuthenticationResponse.builder().token(jwtToken).names(names).roles(role).build();
    }

}
