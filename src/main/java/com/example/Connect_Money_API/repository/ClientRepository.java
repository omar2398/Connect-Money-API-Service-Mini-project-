package com.example.Connect_Money_API.repository;

import com.example.Connect_Money_API.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findClientByClientId(String clientId);
}
