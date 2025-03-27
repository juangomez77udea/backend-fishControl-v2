package com.fiscontrolbackend.fiscontrolbackend.service;

import com.fiscontrolbackend.fiscontrolbackend.models.main.ESupplyType;
import com.fiscontrolbackend.fiscontrolbackend.models.main.SupplyEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SupplyDetailsService {

    List<SupplyEntity> getAllSupplies();
    Optional<SupplyEntity> getSupplyById(Long id);
    SupplyEntity createSupply(SupplyEntity supply);
    SupplyEntity updateSupply(Long id, SupplyEntity supply);
    void deleteSupply(Long id);

    // métodos para consultas específicas
    List<SupplyEntity> getSuppliesByType(ESupplyType type); // Obtener insumos por tipo
    List<SupplyEntity> getSuppliesByStage(String stage);    // Obtener insumos por etapa
    Map<ESupplyType, Integer> getInventory();              // Obtener inventario por tipo
}