package com.fiscontrolbackend.fiscontrolbackend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class SupplyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String suppliesName;

    @NotBlank
    private String presentation;

    @NotNull
    private Integer suppliesQuantity;

    @NotNull
    private Double suppliesPrice;

    @NotNull
    private LocalDate suppliesDate;

}
