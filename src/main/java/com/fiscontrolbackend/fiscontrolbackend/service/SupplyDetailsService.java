package com.fiscontrolbackend.fiscontrolbackend.service;

import com.fiscontrolbackend.fiscontrolbackend.models.SupplyEntity;

import java.util.List;
import java.util.Optional;

public interface SupplyDetailsService {
    List<SupplyEntity> getAllSupplies();
    Optional<SupplyEntity> getSupplyById(Long id);
    SupplyEntity createSupply(SupplyEntity supply);
    SupplyEntity updateSupply(Long id, SupplyEntity supply);
    void deleteSupply(Long id);
}
