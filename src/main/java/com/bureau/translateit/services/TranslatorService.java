package com.bureau.translateit.services;

import com.bureau.translateit.exceptions.EmailAlreadyUsedException;
import com.bureau.translateit.exceptions.InvalidTranslatorCsvException;
import com.bureau.translateit.exceptions.TranslatorNotFoundException;
import com.bureau.translateit.models.Translator;
import com.bureau.translateit.models.dtos.TranslatorDto;
import com.bureau.translateit.repositories.TranslatorRepository;
import com.bureau.translateit.utils.CheckIsValidEmail;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).withCSVParser(parser).build();
            String[] headers = csvReader.readNext();

            //Headers should be: name,email,source_language,target_language
            if(headers == null || headers.length < 4) {
                throw new InvalidTranslatorCsvException();
            }

            String[] row;
            while((row = csvReader.readNext()) != null) {
                if (translatorRepository.findByEmail(row[1]).isPresent()) {
                    throw new EmailAlreadyUsedException(row[1]);
                }

                //Verifying is there's an empty value
                if(row[0].isEmpty() || row[1].isEmpty() || row[2].isEmpty() || row[3].isEmpty()){
                    throw new InvalidTranslatorCsvException();
                }

                //Verifying if the email is valid
                if(!CheckIsValidEmail.isValid(row[1])){
                    throw new IllegalArgumentException("Email: " + row[1] + "is not valid");
                }

                Translator translator = new Translator();
                translator.setName(row[0]);
                translator.setEmail(row[1]);
                translator.setSourceLanguage(row[2]);
                translator.setTargetLanguage(row[3]);

                translators.add(translator);
            }
        } catch (IOException | CsvValidationException | ArrayIndexOutOfBoundsException e) {
            throw new InvalidTranslatorCsvException();
        }
        return translatorRepository.saveAll(translators);
    }

    public Page<Translator> getAll(String email, Pageable pageable) {
        //If this is a search by email
        if(email != null && !email.isEmpty()){
            Translator foundTranslator = translatorRepository.findByEmail(email).orElseThrow(() -> new TranslatorNotFoundException(email));
            return new PageImpl<>(List.of(foundTranslator));
        }
        return translatorRepository.findAll(pageable);
    }

    public Translator getById(UUID id) {
        return translatorRepository.findById(id).orElseThrow(() -> new TranslatorNotFoundException(id));
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
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).withCSVParser(parser).build();
            String[] headers = csvReader.readNext();

            //Headers should be: id,name,email,source_language,target_language
            if(!headers[0].equals("id")) {
                throw new InvalidTranslatorCsvException();
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
                if((row[2] != null && !row[2].equals(foundTranslator.getEmail()))) {
                    if(translatorRepository.findByEmail(row[2]).isPresent()) {
                        throw new EmailAlreadyUsedException(row[2]);
                    }else if(!CheckIsValidEmail.isValid(row[2])){
                        throw new IllegalArgumentException("Email: " + row[2] + " is not valid.");
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
        } catch (IOException | CsvValidationException | ArrayIndexOutOfBoundsException e) {
            throw new InvalidTranslatorCsvException();
        }

        return translatorRepository.saveAll(updatedTranslators);
    }

    public void delete(UUID id) {
        if(!translatorRepository.existsById(id)) {
            throw new TranslatorNotFoundException(id);
        }

        translatorRepository.deleteById(id);
    }
}