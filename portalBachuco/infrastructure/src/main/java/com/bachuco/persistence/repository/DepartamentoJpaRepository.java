package com.bachuco.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bachuco.persistence.entity.DepartamentoEntity;

public interface DepartamentoJpaRepository extends JpaRepository<DepartamentoEntity, Integer>{
	
	public Optional<DepartamentoEntity> findById(Integer id);

}
