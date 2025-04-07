package com.aitor.api_tfg.repositories;

import com.aitor.api_tfg.model.db.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByIsPublicTrue();
}
