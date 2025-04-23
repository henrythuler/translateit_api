package com.bureau.translateit.repositories;

import com.bureau.translateit.models.Translator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TranslatorRepository extends JpaRepository<Translator, UUID> {
    Optional<Translator> findByEmail(String email);
}
