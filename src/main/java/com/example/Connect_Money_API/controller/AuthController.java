package com.example.Connect_Money_API.controller;

import com.example.Connect_Money_API.Service.AuthService;
import com.example.Connect_Money_API.dto.TokenRequest;
import com.example.Connect_Money_API.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/protocol/openid-connect")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE) // tjis to get the request as form data not json as this is the standard of the Oauth2.
    public ResponseEntity<TokenResponse> getToken(@Valid @ModelAttribute TokenRequest tokenRequest){//model attr to convert the form data to request object.
        log.info("Token request received form the client: {}", tokenRequest.getClientId());
        TokenResponse tokenResponse = authService.authenticate(tokenRequest);
        return ResponseEntity.ok(tokenResponse);
    }
}
