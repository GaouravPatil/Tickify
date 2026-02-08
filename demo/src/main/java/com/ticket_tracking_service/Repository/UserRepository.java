package com.ticket_tracking_service.Repository;

import com.ticket_tracking_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


        Optional<User> findByEmailAndIsActiveTrue(String email);

        Optional<User> findByEmailAndRoleAndIsActiveTrue(String email,String role);

        boolean existsByUsernameOrEmail(String username, String email);


}

