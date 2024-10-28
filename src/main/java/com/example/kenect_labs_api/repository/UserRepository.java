package com.example.kenect_labs_api.repository;

import com.example.kenect_labs_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
