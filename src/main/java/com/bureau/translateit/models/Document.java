package com.bureau.translateit.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;

import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(
    name = "T_DOCUMENT",
    indexes = {
        @Index(name = "idx_document_author", columnList = "author"),
        @Index(name = "idx_document_locale_author", columnList = "locale,author")
    }
)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private String locale;

    @Column(nullable = false)
    private String author;

    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "translator_id", nullable = false)
    @JsonBackReference
    private Translator translator;
}