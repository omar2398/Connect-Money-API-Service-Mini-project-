package com.example.Connect_Money_API.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "client", indexes = {
        @Index(name = "index_client_id", columnList = "clientId", unique = true)
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String clientId;
    private String clientSecret;
    private Boolean active = true;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
