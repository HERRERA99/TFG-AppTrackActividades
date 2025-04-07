package com.aitor.api_tfg.services;

import com.aitor.api_tfg.mappers.ActivityMapper;
import com.aitor.api_tfg.mappers.CommentMapper;
import com.aitor.api_tfg.model.db.Activity;
import com.aitor.api_tfg.model.db.Comment;
import com.aitor.api_tfg.model.db.Publication;
import com.aitor.api_tfg.model.dto.CommentDTO;
import com.aitor.api_tfg.model.dto.PublicationDTO;
import com.aitor.api_tfg.repositories.CommentRepository;
import com.aitor.api_tfg.repositories.PublicationRepository;
import com.aitor.api_tfg.model.db.User;
import com.aitor.api_tfg.repositories.UserRepository;
import com.aitor.api_tfg.repositories.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PublicationService {

    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final ActivityRepository activityRepository;
    private final CommentRepository commentRepository;
    private final ActivityMapper activityMapper;
    private final CommentMapper commentMapper;

    public PublicationDTO createPublication(Long activityId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        Activity activity = activityRepository.findById(activityId).orElseThrow(() -> new RuntimeException("Activity not found"));

        Publication publication = activityMapper.mapToPublicationEntity(activity, user);

        Publication createdPublication = publicationRepository.save(publication);

        return activityMapper.mapToPublicationDTO(createdPublication);
    }

    public Page<PublicationDTO> getPublicPublications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("creationDate").descending());
        Page<Publication> publications = publicationRepository.findByIsPublicTrue(pageable);

        return publications.map(activityMapper::mapToPublicationDTO);
    }

    public PublicationDTO addLike(Long publicationId, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Publication publication = publicationRepository.findById(publicationId).orElseThrow(() -> new RuntimeException("Publication not found"));
        Publication updatedPublication = publication;

        if (publication.isLiked(user)) {
            throw new RuntimeException("User already liked this publication");
        } else {
            publication.addLike(user);
            updatedPublication = publicationRepository.save(publication);
        }

        return activityMapper.mapToPublicationDTO(updatedPublication);

    }

    public PublicationDTO removeLike(Long publicationId, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Publication publication = publicationRepository.findById(publicationId).orElseThrow(() -> new RuntimeException("Publication not found"));
        Publication updatedPublication = publication;

        if (!publication.isLiked(user)) {
            throw new RuntimeException("User not liked this publication");
        } else {
            publication.removeLike(user);
            updatedPublication = publicationRepository.save(publication);
        }

        return activityMapper.mapToPublicationDTO(updatedPublication);

    }

    public CommentDTO addComment(Long publicationId, Integer userId, String text) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Publication publication = publicationRepository.findById(publicationId).orElseThrow(() -> new RuntimeException("Publication not found"));
        Comment comment = Comment.builder()
                .publication(publication)
                .text(text)
                .user(user)
                .creationDate(LocalDateTime.now())
                .build();

        Comment createdComment = commentRepository.save(comment);

        return commentMapper.toDTO(createdComment);
    }

    public List<CommentDTO> getComments(Long publicationId) {
        List<Comment> coments  = commentRepository.findCommentsByPublicationId(publicationId);

        return coments.stream().map(commentMapper::toDTO).collect(Collectors.toList());
    }
}
