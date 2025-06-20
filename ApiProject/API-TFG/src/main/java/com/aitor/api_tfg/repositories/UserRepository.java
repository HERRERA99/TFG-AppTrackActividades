package com.aitor.api_tfg.repositories;

import com.aitor.api_tfg.model.db.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(u.firstname) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(u.lastname) LIKE LOWER(CONCAT('%', :text, '%')) OR " +
            "LOWER(CONCAT(u.firstname, ' ', u.lastname)) LIKE LOWER(CONCAT('%', :text, '%'))")
    Page<User> searchUsersByText(@Param("text") String text, Pageable pageable);

    User getUserByUsername(String username);
    Optional<User> findByVerificationToken(String token);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
