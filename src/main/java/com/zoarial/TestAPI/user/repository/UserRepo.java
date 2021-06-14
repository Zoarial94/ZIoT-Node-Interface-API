package com.zoarial.TestAPI.user.repository;

import com.zoarial.TestAPI.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.authGroups WHERE u.username = :id")
    Optional<User> findByIdWithFetching(@Param("id")String id);

}
