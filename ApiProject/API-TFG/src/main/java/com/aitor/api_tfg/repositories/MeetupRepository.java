package com.aitor.api_tfg.repositories;

import com.aitor.api_tfg.model.db.Follow;
import com.aitor.api_tfg.model.db.Meetup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetupRepository extends JpaRepository<Meetup, Long> {
}
