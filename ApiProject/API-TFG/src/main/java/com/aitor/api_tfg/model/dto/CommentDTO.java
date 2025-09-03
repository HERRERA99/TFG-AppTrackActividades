package com.aitor.api_tfg.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private Integer userId;
    private Long publicationId;
    private String userName;
    private String userImage;
    private String comment;
    private LocalDateTime creationDate;
}
