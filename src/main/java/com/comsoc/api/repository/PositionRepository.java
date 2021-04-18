package com.comsoc.api.repository;

import com.comsoc.api.Enum.EPosition;
import com.comsoc.api.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByName(EPosition valueOf);
}
