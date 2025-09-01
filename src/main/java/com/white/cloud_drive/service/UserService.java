package com.white.cloud_drive.service;

import com.white.cloud_drive.dto.UserLoginDTO;
import com.white.cloud_drive.dto.UserRegisterDTO;
import com.white.cloud_drive.model.User;
import com.white.cloud_drive.repository.UserRepository;
import com.white.cloud_drive.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public User registerUser(UserRegisterDTO dto){
        if (userRepository.existsByUsername(dto.getUsername())){
            throw new RuntimeException("username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return userRepository.save(user);
    }

    public String login(UserLoginDTO dto){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );
        return jwtUtil.generateToken(dto.getUsername());

    }
}
