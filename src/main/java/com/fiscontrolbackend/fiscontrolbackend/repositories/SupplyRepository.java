package com.fiscontrolbackend.fiscontrolbackend.repositories;

import com.fiscontrolbackend.fiscontrolbackend.models.ESupplyType;
import com.fiscontrolbackend.fiscontrolbackend.models.SupplyEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplyRepository extends CrudRepository<SupplyEntity, Long> {

    Optional<SupplyEntity> findBySuppliesName(String suppliesName);

    @Query("select s from SupplyEntity s where s.suppliesName = ?1")
    Optional<SupplyEntity> getName(String suppliesName);

    List<SupplyEntity> findByType(ESupplyType type); // Filtrar insumos por tipo
    List<SupplyEntity> findByStage(String stage);    // Filtrar insumos por etapa

}