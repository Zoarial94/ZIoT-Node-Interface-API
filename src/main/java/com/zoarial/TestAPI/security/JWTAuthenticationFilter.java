package com.zoarial.TestAPI.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public JWTAuthenticationFilter() {
        super();
    }
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, String filterProcessesUrl) {
        super(authenticationManager);
        this.setFilterProcessesUrl(filterProcessesUrl);
    }

    @Override public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            String username = this.obtainUsername(request);
            username = username != null ? username : "";
            username = username.trim();
            String password = this.obtainPassword(request);
            password = password != null ? password : "";
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }

    @Override protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        Date expire = new Date(System.currentTimeMillis() + AuthenticationConfigConstants.EXPIRATION_TIME);
        UserPrincipal principal = ((UserPrincipal) auth.getPrincipal());
        String token = JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(expire)
                .sign(Algorithm.HMAC512(AuthenticationConfigConstants.SECRET.getBytes()));
//        response.addHeader(AuthenticationConfigConstants.HEADER_STRING, AuthenticationConfigConstants.TOKEN_PREFIX + token);
//        response.addHeader(AuthenticationConfigConstants.HEADER_EXPIRE_STRING, String.valueOf(expire.getTime()));
        log.info("Token for " + principal.getUsername() + ": " + token);

        Cookie authCookie = new Cookie(AuthenticationConfigConstants.HEADER_STRING, token);
        // Max age is in seconds
        authCookie.setMaxAge((int) (AuthenticationConfigConstants.EXPIRATION_TIME/1000));
        authCookie.setPath("/");
        authCookie.setHttpOnly(true);
        //authCookie.setSecure(true);

        response.addCookie(authCookie);
    }
}
