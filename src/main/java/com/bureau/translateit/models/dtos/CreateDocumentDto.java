package com.bureau.translateit.models.dtos;

import lombok.Data;

@Data
public class CreateDocumentDto {

    private String subject;
    private String content;
    private String locale;
    private String author;

}
