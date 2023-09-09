package com.sonthai.schedulermanagement.entity;

import com.sonthai.schedulermanagement.constant.RegistrationEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    private String username;

    @Size(max = 50)
    private String name;

    @Size(max = 120)
    private String password;

    @NotNull
    @Column(name = "registration_id",columnDefinition = "varchar(25) NOT NULL DEFAULT 'BASIC'")
    @Enumerated(EnumType.STRING)
    private RegistrationEnum registrationId = RegistrationEnum.BASIC;

    @ElementCollection
    private Set<String> roles = new HashSet<>();
}
