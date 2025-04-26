package com.bureau.translateit.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DocumentDto {

    @NotBlank(message = "Subject should not be blank.")
    @Size(max = 255, message = "Subject shouldn't have more than 255 characters.")
    private String subject;
    @NotBlank(message = "Content should not be blank.")
    @Size(max = 1000, message = "Content shouldn't have more than 1000 characters.")
    private String content;
    @Size(max = 5, message = "Locale shouldn't have more than 5 characters.")
    private String locale = "";
    @NotBlank(message = "Author should not be blank")
    @Size(max = 255, message = "Email shouldn't have more than 255 characters.")
    @Email(message = "Author should be a valid email.", regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")
    private String author;

}
