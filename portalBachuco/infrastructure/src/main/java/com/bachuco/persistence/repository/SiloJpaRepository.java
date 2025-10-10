package com.bachuco.persistence.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bachuco.persistence.entity.SiloEntity;

public interface SiloJpaRepository extends JpaRepository<SiloEntity,Integer> {

	public Optional<SiloEntity> findById(Integer id);
	public SiloEntity findByNombre(String nombre);
	
	 @Query("SELECT s.stock FROM silo s WHERE s.id = :id")
	 Optional<Float> findStockById(@Param("id") Integer id);
}
