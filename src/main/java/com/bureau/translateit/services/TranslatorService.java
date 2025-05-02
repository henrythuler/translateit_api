package com.bureau.translateit.services;

import com.bureau.translateit.exceptions.EmailAlreadyUsedException;
import com.bureau.translateit.exceptions.InvalidTranslatorCsvException;
import com.bureau.translateit.exceptions.NoRecordsFoundException;
import com.bureau.translateit.exceptions.TranslatorNotFoundException;
import com.bureau.translateit.models.Document;
import com.bureau.translateit.models.Translator;
import com.bureau.translateit.models.dtos.TranslatorDto;
import com.bureau.translateit.repositories.DocumentRepository;
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
import java.util.Optional;
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
        translator.setName(translatorDto.getName());
        //Verifying if the email is valid
        if(!CheckIsValidEmail.isValid(translatorDto.getEmail())){
            throw new IllegalArgumentException("Email: " + translatorDto.getEmail() + " is not valid");
        }
        translator.setEmail(translatorDto.getEmail());
        translator.setSourceLanguage(translatorDto.getSourceLanguage());
        translator.setTargetLanguage(translatorDto.getTargetLanguage());

        return translatorRepository.save(translator);
    }

    public List<Translator> createFromCsv(MultipartFile file) {
        List<Translator> translators = new ArrayList<>();
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).withCSVParser(parser).build();
            String[] headers = csvReader.readNext();

            //Headers should be: name;email;source_language;target_language
            if(headers == null || headers.length < 4) {
                throw new InvalidTranslatorCsvException();
            }

            String[] row;
            while((row = csvReader.readNext()) != null) {
                String name = row[0];
                String email = row[1];
                String sourceLanguage = row[2];
                String targetLanguage = row[3];

                //Verifying is there's an empty value
                if(name.isEmpty() || email.isEmpty() || sourceLanguage.isEmpty() || targetLanguage.isEmpty()){
                    throw new InvalidTranslatorCsvException();
                }

                if (translatorRepository.findByEmail(email).isPresent()) {
                    throw new EmailAlreadyUsedException(email);
                }

                //Verifying if the email is valid
                if(!CheckIsValidEmail.isValid(email)){
                    throw new IllegalArgumentException("Email: " + email + " is not valid");
                }

                Translator translator = new Translator();
                translator.setName(name);
                translator.setEmail(email);
                translator.setSourceLanguage(sourceLanguage);
                translator.setTargetLanguage(targetLanguage);

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
        Page<Translator> foundTranslators = translatorRepository.findAll(pageable);
        if(foundTranslators.isEmpty()) throw new NoRecordsFoundException("Translators");
        return foundTranslators;
    }

    public Translator getById(UUID id) {
        return translatorRepository.findById(id).orElseThrow(() -> new TranslatorNotFoundException(id));
    }

    public Translator update(UUID id, TranslatorDto translatorDTO) {

        try {
            Translator foundTranslator = translatorRepository.getReferenceById(id);

            // If a new email has been passed, we need to check if it's not already in use
            if (translatorDTO.getEmail() != null && !translatorDTO.getEmail().equals(foundTranslator.getEmail())) {
                Optional<Translator> translatorByEmail = translatorRepository.findByEmail(translatorDTO.getEmail());
                if (translatorByEmail.isPresent()) {
                    throw new EmailAlreadyUsedException(translatorDTO.getEmail(), translatorByEmail.get().getName());
                }else{
                    //Updating documents author
                    documentRepository.updateAuthor(foundTranslator.getEmail(), translatorDTO.getEmail());
                    foundTranslator.setEmail(translatorDTO.getEmail());
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

                String name = row[1];

                if(name != null && !name.isEmpty()) {
                    foundTranslator.setName(name);
                }

                String email = row[2];

                // If a new email has been passed, we need to check if it's not already in use
                if((email != null && !email.equals(foundTranslator.getEmail()))) {
                    Optional<Translator> translatorByEmail = translatorRepository.findByEmail(email);
                    if(translatorByEmail.isPresent()) {
                        throw new EmailAlreadyUsedException(email, translatorByEmail.get().getName());
                    }else if(!CheckIsValidEmail.isValid(email)){
                        throw new IllegalArgumentException("Email: " + email + " is not valid.");
                    }else{
                        //Updating documents author
                        documentRepository.updateAuthor(foundTranslator.getEmail(), email);
                        foundTranslator.setEmail(email);
                    }
                }

                String sourceLanguage = row[3];

                if(sourceLanguage != null && !sourceLanguage.isEmpty()) {
                    foundTranslator.setSourceLanguage(sourceLanguage);
                }

                String targetLanguage = row[4];

                if(targetLanguage != null && !targetLanguage.isEmpty()) {
                    foundTranslator.setTargetLanguage(targetLanguage);
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