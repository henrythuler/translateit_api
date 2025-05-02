package com.bureau.translateit.controllers;

import com.bureau.translateit.models.Translator;
import com.bureau.translateit.models.dtos.TranslatorDto;
import com.bureau.translateit.services.TranslatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(summary = "Create new translator",
            description = "Create a new translator according to JSON format passed info.",
            tags = {"Translators"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Translator.class)
                    )),
                    @ApiResponse(description = "Bad Request", responseCode = "400",  content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Conflict", responseCode = "409",  content =
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
    public ResponseEntity<Translator> createTranslator(@RequestBody @Valid TranslatorDto translatorDTO) {
        Translator translator = translatorService.create(translatorDTO);
        return ResponseEntity.status(201).body(translator);
    }

    @Operation(summary = "Create new translators from CSV file",
            description = "Create new translators according to provided CSV file.",
            tags = {"Translators"},
            responses = {
                    @ApiResponse(description = "Created", responseCode = "201", content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Translator.class)
                    )),
                    @ApiResponse(description = "Bad Request", responseCode = "400",  content =
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)
                    )),
                    @ApiResponse(description = "Conflict", responseCode = "409",  content =
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
    public ResponseEntity<List<Translator>> uploadTranslatorsCsv(@RequestPart("file") MultipartFile file) {
        List<Translator> translators = translatorService.createFromCsv(file);
        return ResponseEntity.ok(translators);
    }

    @Operation(summary = "Get all translators or get a translator by email",
            description = "Get all existing translators or a translator according to the passed email.",
            tags = {"Translators"},
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
    public ResponseEntity<Page<Translator>> getTranslators(
            @RequestParam(required = false) String email,
            @ParameterObject Pageable pageable
    ) {
        Page<Translator> translators = translatorService.getAll(email, pageable);
        return ResponseEntity.ok(translators);
    }

    @Operation(summary = "Get translator by id",
            description = "Get a translator according to an id.",
            tags = {"Translators"},
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
    public ResponseEntity<Translator> getTranslatorById(@PathVariable UUID id) {
        Translator translator = translatorService.getById(id);
        return ResponseEntity.ok(translator);
    }

    @Operation(summary = "Update translator by id",
            description = "Update a translator that its id matches to the passed one.",
            tags = {"Translators"},
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
                    @ApiResponse(description = "Conflict", responseCode = "409", content =
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
    public ResponseEntity<Translator> updateTranslator(@PathVariable UUID id, @RequestBody @Valid TranslatorDto translatorDTO) {
        Translator updatedTranslator = translatorService.update(id, translatorDTO);
        return ResponseEntity.ok(updatedTranslator);
    }

    @Operation(summary = "Update translators from CSV file",
            description = "Update translators that its ids matches to the passed ones in a CSV file.",
            tags = {"Translators"},
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
                    @ApiResponse(description = "Conflict", responseCode = "409", content =
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
    public ResponseEntity<List<Translator>> updateTranslatorsCsv(@RequestPart("file") MultipartFile file) {
        List<Translator> updatedTranslators = translatorService.updateFromCsv(file);
        return ResponseEntity.ok(updatedTranslators);
    }

    @Operation(summary = "Delete translator by id",
            description = "Delete a translator that its id matches to the passed one.",
            tags = {"Translators"},
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
    public ResponseEntity<Void> deleteTranslator(@PathVariable UUID id) {
        translatorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}