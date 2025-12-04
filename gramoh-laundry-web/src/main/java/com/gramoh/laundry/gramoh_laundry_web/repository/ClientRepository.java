package com.gramoh.laundry.gramoh_laundry_web.repository;

import com.gramoh.laundry.gramoh_laundry_web.model.Client;
import com.gramoh.laundry.gramoh_laundry_web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client , Long> {

    Optional<Client> findByUserId(Long userId);

    Optional<Client> findByUser(User user);
}
