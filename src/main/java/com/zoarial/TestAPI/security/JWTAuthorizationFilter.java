package com.zoarial.TestAPI.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Get cookies
        var cookies = request.getCookies();
        String token = null;

        // Check for auth cookie
        if(cookies == null) {
            log.info("No cookies found.");
        } else {
            for (Cookie c : cookies) {
                if (c.getName().equals(AuthenticationConfigConstants.HEADER_STRING)) {
                    token = c.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            log.info("No auth cookie found.");
        } else {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        // Continue chain
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // Sanity check for cookies
        var cookies = request.getCookies();
        String token = null;
        if(cookies == null) {
            log.warn("I should have cookies at this point.");
            return null;
        }
        for(Cookie c : cookies) {
            if(c.getName().equals(AuthenticationConfigConstants.HEADER_STRING)) {
                log.info("Found auth cookie: " + c.getValue());
                token = c.getValue();
                break;
            }
        }
        if (token != null) {
            // parse the token.
            String user = JWT.require(Algorithm.HMAC512(AuthenticationConfigConstants.SECRET.getBytes()))
                    .build()
                    .verify(token)
                    .getSubject();
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        log.warn("I should have found an auth cookie.");
        return null;
    }
}
