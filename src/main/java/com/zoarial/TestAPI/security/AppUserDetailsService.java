package com.zoarial.TestAPI.security;

import com.zoarial.TestAPI.user.entity.User;
import com.zoarial.TestAPI.user.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        log.info("Loading user: " + s);
        Optional<User> userOptional = userRepo.findByIdWithFetching(s);

        if(userOptional.isEmpty()) {
            log.info("User not found.");
            throw new UsernameNotFoundException("Unable to find username: " + s);
        }
        log.info("Found user: " + s);

        // TODO: make sure 'user' always has a list
        return new UserPrincipal(userOptional.get());

    }
}
