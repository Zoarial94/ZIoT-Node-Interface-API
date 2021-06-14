package com.zoarial.TestAPI.user.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@Entity
@Setter
@Getter
@Component
public class User {

    @Id
    String username;

    @NotNull
    @NotBlank
    String password;

    @NotNull
    @ElementCollection
    @CollectionTable(name = "User_authGroups", uniqueConstraints = {@UniqueConstraint(columnNames = {"User_username", "authGroup"})})
    @Column(name = "authGroup")
    List<String> authGroups;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return username.equals(user.username) && password.equals(user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }
}
