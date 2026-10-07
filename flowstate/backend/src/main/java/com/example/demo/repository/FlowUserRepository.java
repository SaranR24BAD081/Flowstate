package com.example.demo.repository;

import com.example.demo.entity.FlowUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FlowUserRepository extends JpaRepository<FlowUser, Long> {

    Optional<FlowUser> findByUsername(String username);

    Optional<FlowUser> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<FlowUser> findByRole(FlowUser.UserRole role);

    @Query("SELECT u FROM FlowUser u WHERE u.isActive = true AND u.role = :role")
    List<FlowUser> findActiveByRole(@Param("role") FlowUser.UserRole role);

    @Query("SELECT COUNT(u) FROM FlowUser u WHERE u.role = :role AND u.isActive = true")
    long countActiveByRole(@Param("role") FlowUser.UserRole role);
}
