package com.bureau.translateit.controllers;

import com.bureau.translateit.models.Document;
import com.bureau.translateit.models.dtos.DocumentDto;
import com.bureau.translateit.services.DocumentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/create")
    public ResponseEntity<Document> createDocument(@RequestBody @Valid DocumentDto documentDto) {
        Document document = documentService.create(documentDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(document.getId()).toUri();
        return ResponseEntity.created(location).body(document);
    }

    @PostMapping("/upload")
    public ResponseEntity<List<Document>> uploadDocumentsCsv(@RequestParam("file") MultipartFile file) {
        List<Document> documents = documentService.createFromCsv(file);
        return ResponseEntity.ok(documents);
    }

    @GetMapping
    public ResponseEntity<Page<Document>> getDocuments(
            @RequestParam(required = false) String locale,
            @RequestParam(required = false) String author,
            Pageable pageable) {
        Page<Document> documents = documentService.getDocuments(locale, author, pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable UUID id) {
        Document document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable UUID id, @RequestBody @Valid DocumentDto updateDocumentDto) {
        Document updatedDocument = documentService.update(id, updateDocumentDto);
        return ResponseEntity.ok(updatedDocument);
    }

    @PutMapping("/upload/update")
    public ResponseEntity<List<Document>> updateCsv(@RequestParam("file") MultipartFile file) {
        List<Document> updatedDocuments = documentService.updateFromCsv(file);
        return ResponseEntity.ok(updatedDocuments);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
