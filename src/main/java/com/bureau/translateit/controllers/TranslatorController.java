package com.bureau.translateit.controllers;

import com.bureau.translateit.models.Translator;
import com.bureau.translateit.models.dtos.TranslatorDto;
import com.bureau.translateit.services.TranslatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/translators")
public class TranslatorController {

    @Autowired
    private TranslatorService translatorService;

    @PostMapping("/create")
    public ResponseEntity<Translator> createTranslator(@RequestBody @Valid TranslatorDto translatorDTO) {
        Translator translator = translatorService.create(translatorDTO);
        return ResponseEntity.status(201).body(translator);
    }

    @PostMapping("/create/upload")
    public ResponseEntity<List<Translator>> uploadTranslatorsCsv(@RequestPart("file") MultipartFile file) {
        List<Translator> translators = translatorService.createFromCsv(file);
        return ResponseEntity.ok(translators);
    }

    @GetMapping
    public ResponseEntity<Page<Translator>> getTranslators(Pageable pageable) {
        Page<Translator> translators = translatorService.getAll(pageable);
        return ResponseEntity.ok(translators);
    }

    @GetMapping
    public ResponseEntity<Translator> getTranslatorByEmail(@RequestParam String email) {
        Translator translator = translatorService.getByEmail(email);
        return ResponseEntity.ok(translator);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Translator> getTranslatorById(@PathVariable UUID id) {
        Translator translator = translatorService.getById(id);
        return ResponseEntity.ok(translator);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Translator> updateTranslator(@PathVariable UUID id, @RequestBody TranslatorDto translatorDTO) {
        Translator updatedTranslator = translatorService.update(id, translatorDTO);
        return ResponseEntity.ok(updatedTranslator);
    }

    @PutMapping("/update/upload")
    public ResponseEntity<List<Translator>> updateTranslatorsCsv(@RequestPart("file") MultipartFile file) {
        List<Translator> updatedTranslators = translatorService.updateFromCsv(file);
        return ResponseEntity.ok(updatedTranslators);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranslator(@PathVariable UUID id) {
        translatorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}