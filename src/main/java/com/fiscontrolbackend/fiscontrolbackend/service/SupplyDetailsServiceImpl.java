package com.fiscontrolbackend.fiscontrolbackend.service;

import com.fiscontrolbackend.fiscontrolbackend.models.SupplyEntity;
import com.fiscontrolbackend.fiscontrolbackend.repositories.SupplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        return null; // O podrías lanzar una excepción si el suministro no existe
    }

    @Override
    public void deleteSupply(Long id) {
        supplyRepository.deleteById(id);
    }

}
