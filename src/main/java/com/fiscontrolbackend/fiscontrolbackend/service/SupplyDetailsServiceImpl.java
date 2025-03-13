package com.fiscontrolbackend.fiscontrolbackend.service;

import com.fiscontrolbackend.fiscontrolbackend.models.ESupplyType;
import com.fiscontrolbackend.fiscontrolbackend.models.SupplyEntity;
import com.fiscontrolbackend.fiscontrolbackend.repositories.SupplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplyDetailsServiceImpl implements SupplyDetailsService {

    @Autowired
    private SupplyRepository supplyRepository;

    @Override
    public List<SupplyEntity> getAllSupplies() {
        return (List<SupplyEntity>) supplyRepository.findAll();
    }

    @Override
    public Optional<SupplyEntity> getSupplyById(Long id) {
        return supplyRepository.findById(id);
    }

    @Override
    public SupplyEntity createSupply(SupplyEntity supply) {
        return supplyRepository.save(supply);
    }

    @Override
    public SupplyEntity updateSupply(Long id, SupplyEntity supply) {
        if (supplyRepository.existsById(id)) {
            supply.setId(id);
            return supplyRepository.save(supply);
        }
        return null;
    }

    @Override
    public void deleteSupply(Long id) {
        supplyRepository.deleteById(id);
    }

    @Override
    public List<SupplyEntity> getSuppliesByType(ESupplyType type) {
        return supplyRepository.findByType(type); // Necesitamos agregar este método en el repository
    }

    @Override
    public List<SupplyEntity> getSuppliesByStage(String stage) {
        return supplyRepository.findByStage(stage); // Necesitamos agregar este método en el repository
    }

    @Override
    public Map<ESupplyType, Integer> getInventory() {
        List<SupplyEntity> supplies = (List<SupplyEntity>) supplyRepository.findAll();

        // Agrupar insumos por tipo y sumar sus cantidades
        return supplies.stream()
                .collect(Collectors.groupingBy(
                        SupplyEntity::getType, // Agrupar por tipo
                        Collectors.summingInt(SupplyEntity::getSuppliesQuantity) // Sumar las cantidades
                ));
    }
}