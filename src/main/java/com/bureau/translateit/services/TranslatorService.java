package com.bureau.translateit.services;

import com.bureau.translateit.exceptions.EmailAlreadyUsedException;
import com.bureau.translateit.exceptions.InvalidCsvException;
import com.bureau.translateit.exceptions.TranslatorNotFoundException;
import com.bureau.translateit.models.Translator;
import com.bureau.translateit.models.dtos.TranslatorDto;
import com.bureau.translateit.repositories.TranslatorRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TranslatorService {

    @Autowired
    private TranslatorRepository translatorRepository;

    public Translator create(TranslatorDto translatorDto) {
        if (translatorRepository.findByEmail(translatorDto.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException(translatorDto.getEmail());
        }

        Translator translator = new Translator();
        BeanUtils.copyProperties(translatorDto, translator);

        return translatorRepository.save(translator);
    }

    public List<Translator> createFromCsv(MultipartFile file) {
        List<Translator> translators = new ArrayList<>();
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
            String[] headers = csvReader.readNext();

            //Headers should be: name,email,source_language,target_language
            if(headers == null || headers.length < 4) {
                throw new InvalidCsvException();
            }

            String[] row;
            while((row = csvReader.readNext()) != null) {
                if (translatorRepository.findByEmail(row[1]).isPresent()) {
                    throw new EmailAlreadyUsedException(row[1]);
                }

                Translator translator = new Translator();
                translator.setName(row[0]);
                translator.setEmail(row[1]);
                translator.setSourceLanguage(row[2]);
                translator.setTargetLanguage(row[3]);

                translators.add(translator);
            }
        } catch (IOException | CsvValidationException e) {
            throw new InvalidCsvException();
        }
        return translatorRepository.saveAll(translators);
    }

    public Page<Translator> getAll(Pageable pageable) {
        return translatorRepository.findAll(pageable);
    }

    public Translator getById(UUID id) {
        return translatorRepository.findById(id).orElseThrow(() -> new TranslatorNotFoundException(id));
    }

    public Translator getByEmail(String email) {
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

    public List<Translator> updateFromCsv(MultipartFile file) {
        List<Translator> updatedTranslators = new ArrayList<>();
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
            String[] headers = csvReader.readNext();

            //Headers should be: id,name,email,source_language,target_language
            if (headers == null || headers.length < 5) {
                throw new InvalidCsvException();
            }

            String[] row;
            while((row = csvReader.readNext()) != null) {
                UUID id;
                try {
                    id = UUID.fromString(row[0]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid UUID: " + row[0]);
                }

                Translator foundTranslator = translatorRepository.findById(id).orElseThrow(() -> new TranslatorNotFoundException(id));

                if(row[1] != null && !row[1].isEmpty()) {
                    foundTranslator.setName(row[1]);
                }
                // If a new email has been passed, we need to check if it's not already in use
                if (row[2] != null && !row[2].equals(foundTranslator.getEmail())) {
                    if (translatorRepository.findByEmail(row[2]).isPresent()) {
                        throw new EmailAlreadyUsedException(row[2]);
                    }else{
                        foundTranslator.setEmail(row[2]);
                    }
                }
                if(row[3] != null && !row[3].isEmpty()) {
                    foundTranslator.setSourceLanguage(row[3]);
                }
                if(row[4] != null && !row[4].isEmpty()) {
                    foundTranslator.setTargetLanguage(row[4]);
                }

                updatedTranslators.add(foundTranslator);
            }
        } catch (IOException | CsvValidationException e) {
            throw new InvalidCsvException();
        }

        return translatorRepository.saveAll(updatedTranslators);
    }

    public void delete(UUID id) {
        if (!translatorRepository.existsById(id)) {
            throw new TranslatorNotFoundException(id);
        }

        translatorRepository.deleteById(id);
    }
}