package com.bureau.translateit.services;

import com.bureau.translateit.exceptions.DocumentNotFoundException;
import com.bureau.translateit.exceptions.InvalidCsvException;
import com.bureau.translateit.exceptions.NoRecordsFoundException;
import com.bureau.translateit.exceptions.TranslatorNotFoundException;
import com.bureau.translateit.models.Document;
import com.bureau.translateit.models.Translator;
import com.bureau.translateit.models.dtos.CreateDocumentDto;
import com.bureau.translateit.models.dtos.UpdateDocumentDto;
import com.bureau.translateit.repositories.DocumentRepository;
import com.bureau.translateit.repositories.TranslatorRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private TranslatorRepository translatorRepository;

    public Document create(CreateDocumentDto createDocumentDto){
            Translator translator = translatorRepository.findByEmail(createDocumentDto.getAuthor()).orElseThrow(() -> new TranslatorNotFoundException(createDocumentDto.getAuthor()));
            Document newDocument = new Document();
            BeanUtils.copyProperties(createDocumentDto, newDocument);
            newDocument.setTranslator(translator);
            return documentRepository.save(newDocument);
    }

    public List<Document> createFromCsv(MultipartFile file) {
        List<Document> documents = new ArrayList<>();
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
            String[] headers = csvReader.readNext();

            //Headers should be: subject,content,locale(optional),author
            if(headers == null || headers.length < 3) {
                throw new InvalidCsvException();
            }

            String[] row;
            while((row = csvReader.readNext()) != null) {
                Document document = new Document();
                document.setSubject(row[0]);
                document.setContent(row[1]);
                //If there's 4 headers, means that locale exists
                document.setLocale(row.length == 4 && !row[2].isEmpty() ? row[2] : "");
                document.setAuthor(row[3]);

                final String author = row[3];
                Translator translator = translatorRepository.findByEmail(row[3]).orElseThrow(() -> new TranslatorNotFoundException(author));
                document.setTranslator(translator);

                documents.add(document);
            }
        } catch (IOException | CsvValidationException e) {
            throw new InvalidCsvException();
        }
        return documentRepository.saveAll(documents);
    }

    public Page<Document> getDocuments(String author, String locale, Pageable pageable) {
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

        return documentRepository.findAll(pageable);
    }

    public Document getDocumentById(UUID id) {
        return documentRepository.findById(id).orElseThrow(() -> new DocumentNotFoundException(id));
    }

    public Document update(UUID id, UpdateDocumentDto updateDocumentDto) {
        try {
            Document foundDocument = documentRepository.getReferenceById(id);

            BeanUtils.copyProperties(updateDocumentDto, foundDocument);

            Translator translator = translatorRepository.findByEmail(updateDocumentDto.getAuthor()).orElseThrow(() -> new TranslatorNotFoundException(updateDocumentDto.getAuthor()));
            foundDocument.setAuthor(updateDocumentDto.getAuthor());
            foundDocument.setTranslator(translator);

            return documentRepository.save(foundDocument);
        } catch (EntityNotFoundException e){
            throw new DocumentNotFoundException(id);
        }
    }

    public List<Document> updateFromCsv(MultipartFile file) {
        List<Document> updatedDocuments = new ArrayList<>();
        try {
            CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
            String[] headers = csvReader.readNext();

            //Headers should be: id,subject,content,locale(optional),author
            if (headers == null || headers.length < 4) {
                throw new InvalidCsvException();
            }

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                UUID id;
                try {
                    id = UUID.fromString(row[0]);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid UUID: " + row[0]);
                }

                Document document = documentRepository.findById(id).orElseThrow(() -> new DocumentNotFoundException(id));

                if (row[1] != null && !row[1].isEmpty()) {
                    document.setSubject(row[1]);
                }
                if (row[2] != null && !row[2].isEmpty()) {
                    document.setContent(row[2]);
                }
                document.setLocale(row.length == 5 && !row[3].isEmpty() ? row[3] : "");
                if (row[4] != null && !row[4].isEmpty()) {
                    final String author = row[3];
                    Translator translator = translatorRepository.findByEmail(row[4]).orElseThrow(() -> new TranslatorNotFoundException(author));
                    document.setAuthor(row[4]);
                    document.setTranslator(translator);
                }

                updatedDocuments.add(document);
            }
        } catch (IOException | CsvValidationException e) {
            throw new InvalidCsvException();
        }
        
        return documentRepository.saveAll(updatedDocuments);
    }

    public void delete(UUID id) {
        if (!documentRepository.existsById(id)) {
            throw new DocumentNotFoundException(id);
        }
        documentRepository.deleteById(id);
    }

}