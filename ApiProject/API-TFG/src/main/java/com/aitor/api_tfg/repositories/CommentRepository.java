package com.aitor.api_tfg.repositories;

import com.aitor.api_tfg.model.db.Comment;
import com.aitor.api_tfg.model.db.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findCommentsByPublicationId(Long publicationId);
}
