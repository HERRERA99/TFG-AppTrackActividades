package com.aitor.api_tfg.repositories;

import com.aitor.api_tfg.model.db.Follow;
import com.aitor.api_tfg.model.db.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFollowerAndFollowed(User follower, User followed);
    Optional<Follow> findByFollowerAndFollowed(User follower, User followed);
    List<Follow> findAllByFollowed(User followed);
    List<Follow> findAllByFollower(User follower);
    long countByFollowed(User followed);
    long countByFollower(User follower);
}
