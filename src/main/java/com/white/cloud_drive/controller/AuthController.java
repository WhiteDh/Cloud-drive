package com.white.cloud_drive.controller;

import com.white.cloud_drive.dto.AuthResponseDTO;
import com.white.cloud_drive.dto.UserLoginDTO;
import com.white.cloud_drive.dto.UserRegisterDTO;
import com.white.cloud_drive.model.User;
import com.white.cloud_drive.repository.UserRepository;
import com.white.cloud_drive.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterDTO dto){
        User user = userService.registerUser(dto);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO dto){
        String token = userService.login(dto);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
