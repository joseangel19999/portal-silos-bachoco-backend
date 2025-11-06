package com.bachoco.persistence.repository;



import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bachoco.persistence.entity.PlantaEntity;

public interface PlantaJpaRepository extends JpaRepository<PlantaEntity,Integer> {
	public List<PlantaEntity> findAll();
	public Optional<PlantaEntity> findById(Integer id);
	public Optional<PlantaEntity> findByPlanta(String clavePlanta);

}
