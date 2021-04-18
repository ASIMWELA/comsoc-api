package com.comsoc.api.repository;

import com.comsoc.api.Enum.ERole;
import com.comsoc.api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole roleUser);
}
