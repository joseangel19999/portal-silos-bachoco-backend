package com.bachuco.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bachuco.persistence.entity.PuestoEntity;

public interface PuestoJpaRepository extends JpaRepository<PuestoEntity,Integer> {

	public Optional<PuestoEntity> findById(Integer id);
}
