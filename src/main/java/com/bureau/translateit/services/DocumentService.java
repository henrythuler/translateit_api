package com.bureau.translateit.services;

import com.bureau.translateit.exceptions.*;
import com.bureau.translateit.models.Document;
import com.bureau.translateit.models.Translator;
import com.bureau.translateit.models.dtos.DocumentDto;
import com.bureau.translateit.openai.OpenAiApiClient;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TranslatorRepository translatorRepository;

    @Autowired
    private OpenAiApiClient openAiApiClient;

    public Document create(DocumentDto documentDto){
            Translator translator = translatorRepository.findByEmail(documentDto.getAuthor()).orElseThrow(() -> new TranslatorNotFoundException(documentDto.getAuthor()));
            Document newDocument = new Document();
            BeanUtils.copyProperties(documentDto, newDocument);
            newDocument.setTranslator(translator);
            if(documentDto.getLocale().isEmpty()){
                String locale = openAiApiClient.getLocale(documentDto.getContent());
                if(!locale.isEmpty()){
                    newDocument.setLocale(locale);
                }
            }
            return documentRepository.save(newDocument);
    }

    public List<Document> createFromCsv(MultipartFile file) {
        List<Document> documents = new ArrayList<>();
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).withCSVParser(parser).build();
            String[] headers = csvReader.readNext();

            //Headers should be: subject,content,locale(optional),author
            if(headers == null || headers.length < 3) {
                throw new InvalidDocumentCsvException();
            }

            String[] row;
            while((row = csvReader.readNext()) != null) {
                Document document = new Document();
                document.setSubject(row[0]);
                document.setContent(row[1]);

                final String author;

                //If there's 4 headers, means that locale exists
                if(row.length == 4){
                    if(row[0].isEmpty() || row[1].isEmpty() || row[3].isEmpty()){
                        throw new InvalidDocumentCsvException();
                    }
                    document.setLocale(!row[2].isEmpty() ? row[2] : openAiApiClient.getLocale(row[1]));
                    author = row[3];
                }else{
                    if(row[0].isEmpty() || row[1].isEmpty() || row[2].isEmpty()){
                        throw new InvalidDocumentCsvException();
                    }       
                    author = row[2];
                    document.setLocale(openAiApiClient.getLocale(row[1]));
                }

                Translator translator = translatorRepository.findByEmail(author).orElseThrow(() -> new TranslatorNotFoundException(author));
                document.setAuthor(author);
                document.setTranslator(translator);

                documents.add(document);
            }
        } catch (IOException | CsvValidationException | ArrayIndexOutOfBoundsException e) {
            throw new InvalidDocumentCsvException();
        }
        return documentRepository.saveAll(documents);
    }

    public Page<Document> getAll(String author, String locale, Pageable pageable) {
        Page<Document> foundDocuments;

        //Get by author and locale
        if((author != null && !author.isEmpty()) && (locale != null && !locale.isEmpty())){
            foundDocuments = documentRepository.findByAuthorAndLocale(author, locale, pageable);
            if(foundDocuments.isEmpty()) throw new DocumentNotFoundException(author + " " + locale);
        //Get by author
        } else if(author != null && !author.isEmpty()) {
            foundDocuments = documentRepository.findByAuthor(author, pageable);
            if(foundDocuments.isEmpty()) throw new DocumentNotFoundException(author);
        //Get by locale
        } else if(locale != null && !locale.isEmpty()){
            foundDocuments = documentRepository.findByLocale(locale, pageable);
            if(foundDocuments.isEmpty()) throw new DocumentNotFoundException(locale);
        //Get all
        } else{
            foundDocuments = documentRepository.findAll(pageable);
            if(foundDocuments.isEmpty()) throw new NoRecordsFoundException("Documents");
        }

        return foundDocuments;
    }

    public Document getById(UUID id) {
        return documentRepository.findById(id).orElseThrow(() -> new DocumentNotFoundException(id));
    }

    public Document update(UUID id, DocumentDto documentDto) {
        try {
            Document foundDocument = documentRepository.getReferenceById(id);

            BeanUtils.copyProperties(documentDto, foundDocument);

            Translator translator = translatorRepository.findByEmail(documentDto.getAuthor()).orElseThrow(() -> new TranslatorNotFoundException(documentDto.getAuthor()));
            foundDocument.setAuthor(documentDto.getAuthor());
            foundDocument.setTranslator(translator);

            return documentRepository.save(foundDocument);
        } catch (EntityNotFoundException e){
            throw new DocumentNotFoundException(id);
        }
    }

    public List<Document> updateFromCsv(MultipartFile file) {
        List<Document> updatedDocuments = new ArrayList<>();
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            CSVReader csvReader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream())).withCSVParser(parser).build();
            String[] headers = csvReader.readNext();

            //Headers should be: id,subject,content,locale(optional),author
            if (headers == null || headers.length < 4) {
                throw new InvalidDocumentCsvException();
            }

            String[] row;
            while((row = csvReader.readNext()) != null) {
                UUID id;
                try {
                    id = UUID.fromString(row[0]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid UUID: " + row[0]);
                }

                Document foundDocument = documentRepository.findById(id).orElseThrow(() -> new DocumentNotFoundException(id));

                if(row[1] != null && !row[1].isEmpty()) {
                    foundDocument.setSubject(row[1]);
                }

                if(row[2] != null && !row[2].isEmpty()) {
                    foundDocument.setContent(row[2]);
                }

                String author;

                //Verifying if the row has its 5 values (locale might be empty, but has been passed)
                if(row.length == 5){
                    foundDocument.setLocale(!row[3].isEmpty() ? row[3] : openAiApiClient.getLocale(row[1]));
                    if(row[4] != null && !row[4].isEmpty()) {
                        if(!CheckIsValidEmail.isValid(row[4])){
                            throw new IllegalArgumentException("Email: " + row[4] + " is not valid.");
                        }
                        author = row[4];
                    }else{
                        author = "";
                    }
                }else{
                    if(row[3] != null && !row[3].isEmpty()) {
                        if(!CheckIsValidEmail.isValid(row[3])){
                            throw new IllegalArgumentException("Email: " + row[3] + " is not valid.");
                        }
                        author = row[3];
                    }else{
                        author = "";
                    }
                }

                if(!author.isEmpty()){
                    foundDocument.setAuthor(author);
                    Translator translator = translatorRepository.findByEmail(author).orElseThrow(() -> new TranslatorNotFoundException(author));
                    foundDocument.setTranslator(translator);
                }

                updatedDocuments.add(foundDocument);
            }
        } catch (IOException | CsvValidationException | ArrayIndexOutOfBoundsException e) {
            throw new InvalidDocumentCsvException();
        }
        
        return documentRepository.saveAll(updatedDocuments);
    }

    public void delete(UUID id) {
        if(!documentRepository.existsById(id)) {
            throw new DocumentNotFoundException(id);
        }
        documentRepository.deleteById(id);
    }

}