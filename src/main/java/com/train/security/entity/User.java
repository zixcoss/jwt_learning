package com.train.security.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "users")
public class User extends BaseEntity {
    @Column(name = "email",nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
}
