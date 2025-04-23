package com.bureau.translateit.repositories;

import com.bureau.translateit.models.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Page<Document> findByAuthorAndLocale(String author, String locale, Pageable pageable);
    Page<Document> findByLocale(String locale, Pageable pageable);
    Page<Document> findByAuthor(String author, Pageable pageable);
}
