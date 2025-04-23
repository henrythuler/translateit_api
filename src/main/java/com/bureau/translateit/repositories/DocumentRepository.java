package com.bureau.translateit.repositories;

import com.bureau.translateit.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByLocale(String locale);
    List<Document> findByAuthor(String author);
}
