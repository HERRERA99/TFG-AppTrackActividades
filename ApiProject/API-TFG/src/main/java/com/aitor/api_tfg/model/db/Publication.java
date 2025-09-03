package com.aitor.api_tfg.model.db;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "publications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @ToString.Exclude
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @ToString.Exclude
    @ManyToMany
    @JoinTable(
            name = "publication_likes",
            joinColumns = @JoinColumn(name = "publication_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private List<User> likes = new ArrayList<>();

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }

    public void addLike(User user) {
        if (!likes.contains(user)) {
            likes.add(user);
        }
    }

    public void removeLike(User user) {
        likes.remove(user);
    }

    public boolean isLiked(User user) {
        return likes.contains(user);
    }

    @PreRemove
    private void removeLikeAssociations() {
        likes.clear();
    }

    public boolean isPublic() {
        return isPublic;
    }
}
