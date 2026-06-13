package com.auth.user.config;

import com.auth.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    // Inject your existing UserRepository
    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 1. Define how Spring Security loads user data from your PostgreSQL database
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsernameOrEmail(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .authorities(user.getRoles().stream().map(rol -> new SimpleGrantedAuthority(rol.getDescription())).toList())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    // 2. Define the AuthenticationProvider bean that SecurityConfig is looking for
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 3. Define the password encoder (BCrypt) used to securely check passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 4. Define the AuthenticationManager (useful for your login controller later)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}