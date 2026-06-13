package com.auth.user.service;

import com.auth.user.dto.UserDto;
import com.auth.user.entity.Rol;
import com.auth.user.entity.User;
import com.auth.user.repository.RolRepository;
import com.auth.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RolRepository rolRepository;

    @Transactional
    public void createUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());

        Set<Rol> roles = Set.of(rolRepository
                .findByDescription("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("rol not found")));

        user.setRoles(roles);
        userRepository.save(user);
    }
}
