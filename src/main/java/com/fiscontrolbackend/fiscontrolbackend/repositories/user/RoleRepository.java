package com.fiscontrolbackend.fiscontrolbackend.repositories.user;

import com.fiscontrolbackend.fiscontrolbackend.models.user.RoleEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<RoleEntity, Long> {
}
