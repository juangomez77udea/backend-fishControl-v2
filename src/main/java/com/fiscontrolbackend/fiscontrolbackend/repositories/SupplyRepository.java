package com.fiscontrolbackend.fiscontrolbackend.repositories;

import com.fiscontrolbackend.fiscontrolbackend.models.SupplyEntity;
import com.fiscontrolbackend.fiscontrolbackend.models.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplyRepository extends CrudRepository<SupplyEntity, Long> {

    Optional<SupplyEntity> findBySuppliesName(String suppliesName);

    @Query("select s from SupplyEntity s where s.suppliesName = ?1")
    Optional<SupplyEntity> getName(String suppliesName);

}
