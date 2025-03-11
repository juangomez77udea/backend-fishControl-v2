package com.fiscontrolbackend.fiscontrolbackend.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSupplyDTO {

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