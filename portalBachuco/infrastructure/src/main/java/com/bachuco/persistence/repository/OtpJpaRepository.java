package com.bachuco.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bachuco.persistence.entity.OtpEntity;

public interface OtpJpaRepository extends JpaRepository<OtpEntity, Long>{
	
	Optional<OtpEntity> findByUsuario(String usuario);

}
