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
    @Email(message = "Email should be a valid email.")
    private String email;

    @NotBlank(message = "Source Language should not be blank.")
    @Size(max = 5, message = "Source Language shouldn't have more than 5 characters.")
    private String sourceLanguage;

    @NotBlank(message = "Target Language should not be blank.")
    @Size(max = 5, message = "Target Language shouldn't have more than 5 characters.")
    private String targetLanguage;
}
