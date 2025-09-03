package com.aitor.api_tfg.mappers;

import com.aitor.api_tfg.model.db.Comment;
import com.aitor.api_tfg.model.dto.CommentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {
    public CommentDTO toDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .publicationId(comment.getPublication().getId())
                .userName(comment.getUser().getUsername())
                .userImage(comment.getUser().getImageUrl())
                .comment(comment.getText())
                .creationDate(comment.getCreationDate())
                .build();
    }
}
