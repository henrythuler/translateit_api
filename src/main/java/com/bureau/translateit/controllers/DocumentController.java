package com.bureau.translateit.controllers;

import com.bureau.translateit.models.Document;
import com.bureau.translateit.models.Translator;
import com.bureau.translateit.models.dtos.DocumentDto;
import com.bureau.translateit.services.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "Create new document",
            description = "Create a new document according to JSON format passed info.",
            tags = {"Documents"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Document.class)
                    )),
                    @ApiResponse(description = "Bad Request", responseCode = "400",  content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Not Found", responseCode = "404",  content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    ))
    })
    @PostMapping(
        value = "/create",
        consumes = {"application/json"},
        produces = {"application/json"}
    )
    public ResponseEntity<Document> createDocument(@RequestBody @Valid DocumentDto documentDto) {
        Document document = documentService.create(documentDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(document.getId()).toUri();
        return ResponseEntity.created(location).body(document);
    }

    @Operation(summary = "Create new documents from CSV file",
            description = "Create new documents according to provided CSV file.",
            tags = {"Documents"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Translator.class)
                    )),
                    @ApiResponse(description = "Bad Request", responseCode = "400",  content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Not Found", responseCode = "404",  content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    ))
    })
    @PostMapping(
        value = "/upload/create",
        consumes = {"multipart/form-data"},
        produces = {"application/json"}
    )
    public ResponseEntity<List<Document>> uploadDocumentsCsv(@RequestPart("file") MultipartFile file) {
        List<Document> documents = documentService.createFromCsv(file);
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "Get all documents or get documents by author/locale",
            description = "Get all existing documents or the ones according to the passed author/locale.",
            tags = {"Documents"},
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Translator.class)
                    )),
                    @ApiResponse(description = "Not Found", responseCode = "404", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    ))
    })
    @GetMapping(produces = {"application/json"})
    public ResponseEntity<Page<Document>> getDocuments(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String locale,
            Pageable pageable
    ) {
        Page<Document> documents = documentService.getAll(author, locale, pageable);
        return ResponseEntity.ok(documents);
    }

    @Operation(summary = "Get document by id",
            description = "Get a document according to an id.",
            tags = {"Documents"},
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Translator.class)
                    )),
                    @ApiResponse(description = "Not Found", responseCode = "404", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    ))
    })
    @GetMapping(
        value = "/{id}",
        produces = {"application/json"}
    )
    public ResponseEntity<Document> getDocumentById(@PathVariable UUID id) {
        Document document = documentService.getById(id);
        return ResponseEntity.ok(document);
    }

    @Operation(summary = "Update document by id",
            description = "Update a document that its id matches to the passed one.",
            tags = {"Documents"},
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Translator.class)
                    )),
                    @ApiResponse(description = "Bad Request", responseCode = "400",  content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Not Found", responseCode = "404", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    ))
            })
    @PutMapping(
        value = "/update/{id}",
        consumes = {"application/json"},
        produces = {"application/json"}
    )
    public ResponseEntity<Document> updateDocument(@PathVariable UUID id, @RequestBody @Valid DocumentDto updateDocumentDto) {
        Document updatedDocument = documentService.update(id, updateDocumentDto);
        return ResponseEntity.ok(updatedDocument);
    }

    @Operation(summary = "Update documents from CSV file",
            description = "Update documents that its ids matches to the passed ones in a CSV file.",
            tags = {"Documents"},
            responses = {
                    @ApiResponse(description = "Ok", responseCode = "200", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Translator.class)
                    )),
                    @ApiResponse(description = "Bad Request", responseCode = "400",  content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Not Found", responseCode = "404", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    ))
            })
    @PutMapping(
        value = "/upload/update",
        consumes = {"multipart/form-data"},
        produces = {"application/json"}
    )
    public ResponseEntity<List<Document>> updateDocumentsCsv(@RequestPart("file") MultipartFile file) {
        List<Document> updatedDocuments = documentService.updateFromCsv(file);
        return ResponseEntity.ok(updatedDocuments);
    }

    @Operation(summary = "Delete document by id",
            description = "Delete a document that its id matches to the passed one.",
            tags = {"Documents"},
            responses = {
                    @ApiResponse(description = "No content", responseCode = "204", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Translator.class)
                    )),
                    @ApiResponse(description = "Not Found", responseCode = "404", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Internal Server Error", responseCode = "500", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    ))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable UUID id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
