package com.example.Connect_Money_API.dto;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TokenRequest {
    @NotBlank
    @Pattern(regexp = "Client_Credentials", message = "grant_type must be: 'client_credentials'")
    @JsonProperty("grant_type")
    private String grantType;

    @NotBlank
    @JsonProperty("client_id")
    private String ClientId;

    @NotBlank
    @JsonProperty("client_secret")
    private String ClientSecret;
}
