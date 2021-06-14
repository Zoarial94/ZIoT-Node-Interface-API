package com.zoarial.TestAPI.controller;

import com.zoarial.TestAPI.user.model.UserDTO;
import com.zoarial.TestAPI.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    ResponseEntity<String> userSignup(UserDTO.Request.Create user) {
        try {
            userService.createUser(user);
        } catch(Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

        return ResponseEntity.ok("Successfully created user");

    }

    // This serves basically as a login test.
    @GetMapping("/")
    ResponseEntity<String> loginTest(Principal principal) {
        log.info("Login test success from: " + principal.getName());

        return ResponseEntity.ok("Successful login from: " + principal.getName());
    }

}
