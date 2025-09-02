package com.aitor.api_tfg.controllers;

import com.aitor.api_tfg.model.db.Modalidades;
import com.aitor.api_tfg.model.dto.CommentDTO;
import com.aitor.api_tfg.model.dto.PageDTO;
import com.aitor.api_tfg.model.dto.PageInfoDTO;
import com.aitor.api_tfg.model.dto.PublicationDTO;
import com.aitor.api_tfg.model.request.FiltroRequest;
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

    @PostMapping
    public ResponseEntity<PublicationDTO> createPublication(
            @RequestParam Long activityId,
            Authentication authentication) {

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

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long id) {
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

    @GetMapping("/user/{id}/filtro")
    public ResponseEntity<PageDTO<PublicationDTO>> getFilteredUserPublications(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Modalidades activityType,

            @RequestParam(defaultValue = "0") float distanciaMin,
            @RequestParam(defaultValue = "0") float distanciaMax,

            @RequestParam(defaultValue = "0") double positiveElevationMin,
            @RequestParam(defaultValue = "0") double positiveElevationMax,

            @RequestParam(defaultValue = "0") long durationMin,
            @RequestParam(defaultValue = "0") long durationMax,

            @RequestParam(defaultValue = "0") float averageSpeedMin,
            @RequestParam(defaultValue = "0") float averageSpeedMax,

            HttpServletRequest request) {

        // Construir el filtro a partir de estos params:
        FiltroRequest filtro = new FiltroRequest();
        filtro.setNombre(nombre);
        filtro.setActivityType(activityType);
        filtro.setDistanciaMin(distanciaMin);
        filtro.setDistanciaMax(distanciaMax);
        filtro.setPositiveElevationMin(positiveElevationMin);
        filtro.setPositiveElevationMax(positiveElevationMax);
        filtro.setDurationMin(durationMin);
        filtro.setDurationMax(durationMax);
        filtro.setAverageSpeedMin(averageSpeedMin);
        filtro.setAverageSpeedMax(averageSpeedMax);

        Page<PublicationDTO> publications = publicationService.getFilteredPublicationsByUser(id, filtro, page - 1, size);

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
