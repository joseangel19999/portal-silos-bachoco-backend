package com.bachuco.persistence.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bachuco.persistence.entity.PlantaEntity;

public interface PlantaJpaRepository extends JpaRepository<PlantaEntity,Integer> {
	
	public Optional<PlantaEntity> findById(Integer id);

}
