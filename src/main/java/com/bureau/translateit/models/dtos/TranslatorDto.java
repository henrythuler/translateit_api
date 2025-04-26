package com.bureau.translateit.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TranslatorDto {

    @NotBlank(message = "Name should not be blank.")
    @Size(max = 100, message = "Name shouldn't have more than 100 characters.")
    private String name;

    @NotBlank(message = "Email should not be blank.")
    @Size(max = 255, message = "Email shouldn't have more than 255 characters.")
    @Email(message = "Author should be a valid email.", regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$")
    private String email;

    @NotBlank(message = "Source Language should not be blank.")
    @Size(max = 5, message = "Source Language shouldn't have more than 5 characters.")
    private String sourceLanguage;

    @NotBlank(message = "Target Language should not be blank.")
    @Size(max = 5, message = "Target Language shouldn't have more than 255 characters.")
    private String targetLanguage;
}
