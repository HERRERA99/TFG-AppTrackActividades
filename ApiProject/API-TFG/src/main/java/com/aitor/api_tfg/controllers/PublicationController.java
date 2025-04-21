package com.aitor.api_tfg.controllers;

import com.aitor.api_tfg.model.dto.CommentDTO;
import com.aitor.api_tfg.model.dto.PageDTO;
import com.aitor.api_tfg.model.dto.PageInfoDTO;
import com.aitor.api_tfg.model.dto.PublicationDTO;
import com.aitor.api_tfg.services.PublicationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationController {

    private final PublicationService publicationService;

    @PostMapping("/{activityId}")
    public ResponseEntity<PublicationDTO> createActivity(@PathVariable Long activityId, Authentication authentication) {
        // Asociar automaticamente el usuario autentificado
        String username = authentication.getName();

        PublicationDTO publicacion = publicationService.createPublication(activityId, username);

        return ResponseEntity.ok(publicacion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicationDTO> getPublication(@PathVariable Long id) {
        PublicationDTO publicacion = publicationService.getPublication(id);
        return ResponseEntity.ok(publicacion);
    }

    @GetMapping("/public")
    public ResponseEntity<PageDTO<PublicationDTO>> getPublicPublications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Page<PublicationDTO> publications = publicationService.getPublicPublications(page - 1, size);

        return getPageDTOResponseEntity(page, request, publications);
    }

    @PutMapping("/{id}/like")
    public ResponseEntity<PublicationDTO> likePublication(@PathVariable Long id, @RequestParam Integer userId) {
        PublicationDTO publication = publicationService.addLike(id, userId);
        return ResponseEntity.ok(publication);
    }

    @DeleteMapping("/{id}/removeLike")
    public ResponseEntity<PublicationDTO> unlikePublication(@PathVariable Long id, @RequestParam Integer userId) {
        PublicationDTO publication = publicationService.removeLike(id, userId);
        return ResponseEntity.ok(publication);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long id, @RequestParam Integer userId, @RequestParam String text) {
        CommentDTO comment = publicationService.addComment(id, userId, text);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{id}/comment")
    public ResponseEntity<List<CommentDTO>> getComment(@PathVariable Long id) {
        List<CommentDTO> comments = publicationService.getComments(id);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<PageDTO<PublicationDTO>> getUserPublications(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Page<PublicationDTO> publications = publicationService.getPublicationsByUser(id, page - 1, size);

        return getPageDTOResponseEntity(page, request, publications);
    }

    private ResponseEntity<PageDTO<PublicationDTO>> getPageDTOResponseEntity(@RequestParam(defaultValue = "1") int page, HttpServletRequest request, Page<PublicationDTO> publications) {
        String baseUrl = request.getRequestURL().toString();
        String nextUrl = (page < publications.getTotalPages()) ? baseUrl + "?page=" + (page + 1) : null;
        String prevUrl = (page > 1) ? baseUrl + "?page=" + (page - 1) : null;

        PageInfoDTO pageInfo = new PageInfoDTO(
                (int) publications.getTotalElements(),
                publications.getTotalPages(),
                nextUrl,
                prevUrl
        );

        PageDTO<PublicationDTO> response = new PageDTO<>(pageInfo, publications.getContent());

        return ResponseEntity.ok(response);
    }
}
