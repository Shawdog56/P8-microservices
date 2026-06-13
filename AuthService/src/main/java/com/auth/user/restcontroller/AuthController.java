package com.auth.user.restcontroller;

import com.auth.user.config.JwtService;
import com.auth.user.dto.LoginDto;
import com.auth.user.dto.UserDto;
import com.auth.user.entity.User;
import com.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService; // Or your custom UserService
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository; // Inject your DB repository here

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        Map<String, String> response = new HashMap<>();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUsername());

            String jwtToken = jwtService.generateToken(userDetails);

            response.put("message", "Te has conectado correctamente.");
            response.put("token", jwtToken);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Error al tratar de autenticar: " + e.getMessage());
            response.put("error", "Credenciales inválidas.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserDto userDto) {
        Map<String, String> response = new HashMap<>();

        if (userRepository.findByUsernameOrEmail(userDto.getUsername()).isPresent()) {
            response.put("error", "El correo ya está registrado.");
            return ResponseEntity.badRequest().body(response);
        }

        User newUser = new User();
        newUser.setEmail(userDto.getUsername());

        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(newUser);
        org.springframework.security.core.userdetails.User userDetails = new org.springframework.security.core.userdetails.User(
                newUser.getUsername(),
                newUser.getPassword(),
                newUser.getRoles()
                        .stream()
                        .map(rol -> new SimpleGrantedAuthority(
                                rol.getDescription()))
                        .collect(Collectors.toSet()));

        String jwtToken = jwtService.generateToken(userDetails);

        response.put("message", "Usuario registrado exitosamente.");
        response.put("token", jwtToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}