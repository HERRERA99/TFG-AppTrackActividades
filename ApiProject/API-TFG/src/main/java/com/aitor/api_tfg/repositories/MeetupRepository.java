package com.aitor.api_tfg.repositories;

import com.aitor.api_tfg.model.db.Meetup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetupRepository extends JpaRepository<Meetup, Long> {
    @Query(value = """
    SELECT m.*, 
           ST_Distance_Sphere(
               POINT(:userLng, :userLat),
               POINT(m.lng_punto_quedada, m.lat_punto_quedada)
           ) AS distanceToStart
    FROM meetup m
    WHERE m.date_time > NOW()
    ORDER BY distanceToStart
    """,
            countQuery = "SELECT COUNT(*) FROM meetup m WHERE m.date_time > NOW()",
            nativeQuery = true)
    Page<Meetup> findMeetupsOrderedByDistance(
            @Param("userLat") double userLat,
            @Param("userLng") double userLng,
            Pageable pageable);

    @Query(value = """
    SELECT m.*
    FROM meetup m
    WHERE m.date_time > NOW()
      AND m.organizer_id = :userId
    ORDER BY m.date_time ASC
    """,
            countQuery = """
    SELECT COUNT(*)
    FROM meetup m
    WHERE m.date_time > NOW()
      AND m.organizer_id = :userId
    """,
            nativeQuery = true)
    Page<Meetup> findMeetupsOrganizedByUserOrderedByDate(
            @Param("userId") Integer userId,
            Pageable pageable);
}
