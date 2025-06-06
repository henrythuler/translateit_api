package com.bureau.translateit.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(
    name = "T_TRANSLATOR",
    indexes = {
        @Index(name = "idx_translator_email", columnList = "email"),
    }
)
public class Translator {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "source_language", nullable = false)
    private String sourceLanguage;

    @Column(name = "target_language", nullable = false)
    private String targetLanguage;

    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @OneToMany(mappedBy = "translator", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Document> documents = new ArrayList<>();
}
