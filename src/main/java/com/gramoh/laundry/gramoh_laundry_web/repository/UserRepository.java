package com.gramoh.laundry.gramoh_laundry_web.repository;

import com.gramoh.laundry.gramoh_laundry_web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByRememberToken(String token);
}
