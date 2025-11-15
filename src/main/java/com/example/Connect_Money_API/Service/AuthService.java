package com.example.Connect_Money_API.Service;

import com.example.Connect_Money_API.dto.TokenRequest;
import com.example.Connect_Money_API.dto.TokenResponse;
import com.example.Connect_Money_API.model.Client;
import com.example.Connect_Money_API.repository.ClientRepository;
import com.example.Connect_Money_API.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${security.jwt.expiration_time}")
    private Long tokenExpiration;

    @Value("${security.client-credentials.max-number-of-attempts}")
    private int maxAttempts;

    @Value("${security.client-credentials.lockout-duration-time}")
    private int lockoutDuration;

    @Transactional
    public TokenResponse authenticate(TokenRequest request){
        Client client = clientRepository.findClientByClientId(request.getClientId())
                .orElseThrow(() -> new SecurityException("Invalid data"));

        if(!client.getActive()){
            log.error("Authentication can't be done the user: {} is inactive", request.getClientId());
            throw new SecurityException("Client isn't active");
        }

        //Compare plain password m3a hashed pass
        if(!passwordEncoder.matches(request.getClientSecret(), client.getClientSecret())){
            log.error("Invalid password");
            failedAuthAction(client);
            throw new SecurityException("Invalid password");
        }

        if (client.getLockedUntil() != null &&
                client.getLockedUntil().isAfter(LocalDateTime.now())) {
            log.warn("The account of the user {} is locked due to many attempts", request.getClientId());
            throw new SecurityException("Account is locked for while");
        }

        if (client.getLockedUntil() != null &&
                client.getLockedUntil().isBefore(LocalDateTime.now())) {
            client.setLockedUntil(null);
            client.setFailedAttempts(0);
        }

        String token = jwtService.generateToken(client.getClientId());

        return TokenResponse.builder()
                .accessToken(token)
                .expiresIn(tokenExpiration)
                .tokenType("Bearer")
                .build();
    }

    private void failedAuthAction(Client clt){
        clt.setFailedAttempts(clt.getFailedAttempts() + 1);

        if (clt.getFailedAttempts() >= maxAttempts){
            clt.setLockedUntil(LocalDateTime.now().plusSeconds(lockoutDuration));
            log.warn("Client is locked due to many failed attempts from user {}", clt.getClientId());
        }

        clientRepository.save(clt);
    }

}
