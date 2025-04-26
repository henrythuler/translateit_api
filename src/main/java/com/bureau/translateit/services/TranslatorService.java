package com.bureau.translateit.services;

import com.bureau.translateit.exceptions.EmailAlreadyUsedException;
import com.bureau.translateit.exceptions.TranslatorNotFoundException;
import com.bureau.translateit.models.Translator;
import com.bureau.translateit.models.dtos.TranslatorDto;
import com.bureau.translateit.repositories.DocumentRepository;
import com.bureau.translateit.repositories.TranslatorRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TranslatorService {

    @Autowired
    private TranslatorRepository translatorRepository;
    @Autowired
    private DocumentRepository documentRepository;

    public Translator create(TranslatorDto translatorDto) {
        if (translatorRepository.findByEmail(translatorDto.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException(translatorDto.getEmail());
        }

        Translator translator = new Translator();
        BeanUtils.copyProperties(translatorDto, translator);

        return translatorRepository.save(translator);
    }

    public Page<Translator> getTranslators(Pageable pageable) {
        return translatorRepository.findAll(pageable);
    }

    public Translator getTranslatorById(UUID id) {
        return translatorRepository.findById(id).orElseThrow(() -> new TranslatorNotFoundException(id));
    }

    public Translator getTranslatorByEmail(String email) {
        return translatorRepository.findByEmail(email).orElseThrow(() -> new TranslatorNotFoundException(email));
    }

    public Translator update(UUID id, TranslatorDto translatorDTO) {

        try {
            Translator foundTranslator = translatorRepository.getReferenceById(id);

            // If a new email has been passed, we need to check if it's not already in use
            if (translatorDTO.getEmail() != null && !translatorDTO.getEmail().equals(foundTranslator.getEmail())) {
                if (translatorRepository.findByEmail(translatorDTO.getEmail()).isPresent()) {
                    throw new EmailAlreadyUsedException(translatorDTO.getEmail());
                }
            }

            BeanUtils.copyProperties(translatorDTO, foundTranslator);

            return translatorRepository.save(foundTranslator);

        } catch (EntityNotFoundException e) {
            throw new TranslatorNotFoundException(id);
        }

    }

    public void delete(UUID id) {
        if (!translatorRepository.existsById(id)) {
            throw new TranslatorNotFoundException(id);
        }

        translatorRepository.deleteById(id);
    }
}