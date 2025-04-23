package com.bureau.translateit.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "documents")
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

    @ManyToOne()
    @JoinColumn(name = "translator_id", nullable = false)
    private Translator translator;
}