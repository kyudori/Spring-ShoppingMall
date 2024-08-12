package com.example.shoppingmall.domain.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String userid;

    @NotNull
    @Email
    @Column(unique = true)
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String nickname;

    @NotNull
    private String fullName;

    @NotNull
    private LocalDate birthDate;

    @NotNull
    private String phoneNumber;

    private boolean emailVerified = false;

    private String emailVerificationToken;

    private LocalDateTime emailVerificationTokenExpiry;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
}
