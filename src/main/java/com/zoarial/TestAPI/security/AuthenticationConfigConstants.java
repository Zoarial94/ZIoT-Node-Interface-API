package com.zoarial.TestAPI.security;

class AuthenticationConfigConstants {
    public static final String SECRET = "Hunter_Secret";
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/user/signup";
    public static final String LOGIN_URL = "/user/login";
}
