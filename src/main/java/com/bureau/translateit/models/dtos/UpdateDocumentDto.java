package com.bureau.translateit.models.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateDocumentDto {
    private UUID id;
    private String subject;
    private String content;
    private String locale;
    private String author;
}
