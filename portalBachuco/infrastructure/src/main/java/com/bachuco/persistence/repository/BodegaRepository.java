package com.bachuco.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bachuco.persistence.entity.BodegaEntity;
import com.bachuco.persistence.entity.SiloEntity;

import jakarta.transaction.Transactional;

public interface BodegaRepository extends JpaRepository<BodegaEntity, Integer> {

	@Modifying
    @Transactional
    @Query("UPDATE bodega b SET b.clave = :nombre,b.silo=:nuevoSilo WHERE b.id = :id")
    public Integer actualizarNombrePorId(@Param("nombre") String nombre,@Param("nuevoSilo") SiloEntity nuevoSilo, @Param("id") Integer id);
	
	public Optional<BodegaEntity> findById(Integer id);
}
