package com.bureau.translateit.repositories;

import com.bureau.translateit.models.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    Page<Document> findByAuthorAndLocale(String author, String locale, Pageable pageable);
    Page<Document> findByLocale(String locale, Pageable pageable);
    Page<Document> findByAuthor(String author, Pageable pageable);
    @Transactional
    @Modifying
    @Query("UPDATE Document d set d.author = :newAuthor where d.author = :author")
    void updateAuthor(String author, String newAuthor);
}
