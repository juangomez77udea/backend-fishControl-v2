package com.fiscontrolbackend.fiscontrolbackend.resolver;

import com.fiscontrolbackend.fiscontrolbackend.models.SupplyEntity;
import com.fiscontrolbackend.fiscontrolbackend.request.CreateSupplyDTO;
import com.fiscontrolbackend.fiscontrolbackend.service.SupplyDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/supplies")
public class SupplyController {

    @Autowired
    private SupplyDetailsService supplyDetailsService;

    // CREATE - Crear un nuevo insumo
    @PostMapping
    public ResponseEntity<SupplyEntity> createSupply(@RequestBody CreateSupplyDTO supplyDTO) {
        // Validar que la fecha no sea nula
        if (supplyDTO.getSuppliesDate() == null) {
            return ResponseEntity.badRequest().build();
        }

        SupplyEntity supply = new SupplyEntity();
        supply.setSuppliesName(supplyDTO.getSuppliesName());
        supply.setPresentation(supplyDTO.getPresentation());
        supply.setSuppliesQuantity(supplyDTO.getSuppliesQuantity());
        supply.setSuppliesPrice(supplyDTO.getSuppliesPrice());
        supply.setSuppliesDate(supplyDTO.getSuppliesDate());
        supply.setType(supplyDTO.getType());
        supply.setStage(supplyDTO.getStage());

        SupplyEntity createdSupply = supplyDetailsService.createSupply(supply);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSupply);
    }

    // READ - Obtener todos los insumos
    @GetMapping
    public ResponseEntity<List<SupplyEntity>> getAllSupplies() {
        List<SupplyEntity> supplies = supplyDetailsService.getAllSupplies();
        return ResponseEntity.ok(supplies);
    }

    // READ - Obtener un insumo por ID
    @GetMapping("/{id}")
    public ResponseEntity<SupplyEntity> getSupplyById(@PathVariable Long id) {
        Optional<SupplyEntity> supply = supplyDetailsService.getSupplyById(id);
        return supply.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE - Actualizar un insumo existente
    @PutMapping("/{id}")
    public ResponseEntity<SupplyEntity> updateSupply(@PathVariable Long id, @RequestBody SupplyEntity supply) {
        SupplyEntity updatedSupply = supplyDetailsService.updateSupply(id, supply);
        return updatedSupply != null ?
                ResponseEntity.ok(updatedSupply) :
                ResponseEntity.notFound().build();
    }

    // DELETE - Eliminar un insumo
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupply(@PathVariable Long id) {
        // Verificar si el insumo existe antes de eliminarlo
        Optional<SupplyEntity> existingSupply = supplyDetailsService.getSupplyById(id);
        if (existingSupply.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        supplyDetailsService.deleteSupply(id);
        return ResponseEntity.noContent().build();
    }
}