package com.zoarial.TestAPI.user.service;

import com.zoarial.TestAPI.user.entity.User;
import com.zoarial.TestAPI.user.model.UserDTO;
import com.zoarial.TestAPI.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;


    public void createUser(UserDTO.Request.Create userRequest) {

        log.info("Creating user:");
        // Check for existing username
        String username = userRequest.getUsername();

        log.info("Username: " + username);
        log.info("Password: " + userRequest.getPassword());

        if(userRepo.findById(username).isPresent()) {
            throw new RuntimeException("Username is already registered");
        }

        // Persist user to database
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setAuthGroups(Collections.emptyList());
        try {
            userRepo.save(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }

    }
}
