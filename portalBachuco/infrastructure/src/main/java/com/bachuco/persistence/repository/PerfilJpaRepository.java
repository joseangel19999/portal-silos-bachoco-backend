package com.bachuco.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bachuco.persistence.entity.PerfilEntity;

public interface PerfilJpaRepository extends JpaRepository<PerfilEntity,Integer> {

	public Optional<PerfilEntity> findById(Integer id);
	public Optional<PerfilEntity> findByClave(String clave);
}
