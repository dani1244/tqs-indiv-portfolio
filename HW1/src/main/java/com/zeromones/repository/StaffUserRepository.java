package com.zeromones.repository;

import com.zeromones.model.StaffUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {

    Optional<StaffUser> findByUsername(String username);

    Optional<StaffUser> findByUsernameAndPassword(String username, String password);

    boolean existsByUsername(String username);
}
