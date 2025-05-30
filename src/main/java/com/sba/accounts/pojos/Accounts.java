package com.sba.accounts.pojos;


import com.sba.enums.Roles;
import com.sba.utils.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
@SuperBuilder
public class Accounts extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected String id;

    @Column(unique = true, length = 30)
    private String username;

    @Column(length = 50)
    private String password;

    @Column(unique = true, length = 11)
    @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 digits")
    @Digits(integer = 11, fraction = 0)
    private String phoneNumber;

    @Column(nullable = false, unique = true, length = 100)
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    private String email;

    @Enumerated(EnumType.STRING)
    private Roles role;

    private boolean enable;

    private String verificationCode;
}