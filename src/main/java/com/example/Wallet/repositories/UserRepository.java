package com.example.Wallet.repositories;

import com.example.Wallet.entities.User;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    List<User> findByNameContainingIgnoreCase(String name);
    
}
