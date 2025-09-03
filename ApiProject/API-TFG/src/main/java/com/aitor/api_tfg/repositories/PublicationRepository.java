package com.aitor.api_tfg.repositories;

import com.aitor.api_tfg.model.db.Modalidades;
import com.aitor.api_tfg.model.db.Publication;
import com.aitor.api_tfg.model.request.FiltroRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Page<Publication> findByIsPublicTrue(Pageable pageable);
    Page<Publication> findByUserIdAndIsPublicTrue(Integer userId, Pageable pageable);

    @Query("""
    SELECT p FROM Publication p
    WHERE p.user.id = :userId
    AND (:nombre IS NULL OR LOWER(p.activity.title) LIKE LOWER(CONCAT('%', :nombre, '%')))
    AND (:activityType IS NULL OR p.activity.activityType = :activityType)
    AND p.activity.distance >= :distanciaMin
    AND (:distanciaMax = 80000 OR p.activity.distance <= :distanciaMax)
    AND p.activity.positiveElevation >= :positiveElevationMin
    AND (:positiveElevationMax = 600 OR p.activity.positiveElevation <= :positiveElevationMax)
    AND p.activity.duration >= :durationMin
    AND (:durationMax = 21600000 OR p.activity.duration <= :durationMax)
    AND p.activity.averageSpeed >= :averageSpeedMin
    AND (:averageSpeedMax = 50 OR p.activity.averageSpeed <= :averageSpeedMax)
""")
    Page<Publication> findAllFilteredByUserId(
            @Param("userId") Integer userId,
            @Param("nombre") String nombre,
            @Param("activityType") Modalidades activityType,
            @Param("distanciaMin") float distanciaMin,
            @Param("distanciaMax") float distanciaMax,
            @Param("positiveElevationMin") double positiveElevationMin,
            @Param("positiveElevationMax") double positiveElevationMax,
            @Param("durationMin") long durationMin,
            @Param("durationMax") long durationMax,
            @Param("averageSpeedMin") float averageSpeedMin,
            @Param("averageSpeedMax") float averageSpeedMax,
            Pageable pageable
    );
}

