package com.aitor.api_tfg.model.db;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"}),
        indexes = {
                @Index(name = "idx_follower", columnList = "follower_id"),
                @Index(name = "idx_followed", columnList = "followed_id")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;

    @Column(nullable = false, updatable = false)
    private LocalDateTime followedAt;

    @Column(nullable = true)
    private LocalDateTime unfollowedAt;

    @PrePersist
    protected void onFollow() {
        this.followedAt = LocalDateTime.now();
    }
}


