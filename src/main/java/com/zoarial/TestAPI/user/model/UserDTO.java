package com.zoarial.TestAPI.user.model;

import lombok.Value;

import javax.validation.constraints.NotBlank;

/*
 * Modeled after https://blog.scottlogic.com/2020/01/03/rethinking-the-java-dto.html
 */
public enum UserDTO {;

    private interface Username {@NotBlank String getUsername(); }
    private interface Password {@NotBlank String getPassword(); }

    public enum Request{;
        @Value public static class Create implements Username, Password {
            String username;
            String password;
        }
        @Value public static class Login implements Username, Password {
            String username;
            String password;
        }
    }

    public enum Response {;
        @Value public static class Public implements Username {
            String username;
        }
    }
}