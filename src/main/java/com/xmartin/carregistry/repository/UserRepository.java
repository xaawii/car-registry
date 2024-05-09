package com.xmartin.carregistry.repository;

import com.xmartin.carregistry.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
}
