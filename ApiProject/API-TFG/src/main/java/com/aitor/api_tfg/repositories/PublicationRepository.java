package com.aitor.api_tfg.repositories;

import com.aitor.api_tfg.model.db.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Page<Publication> findByIsPublicTrue(Pageable pageable);
    Page<Publication> findByUserIdAndIsPublicTrue(Integer userId, Pageable pageable);
}

