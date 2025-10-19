package com.bachuco.persistence.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bachuco.persistence.entity.EmpleadoEntity;

public interface EmpleadoJpaRepository extends JpaRepository<EmpleadoEntity, Integer>{
	
	public Optional<EmpleadoEntity> findById(Integer id);
	public Optional<EmpleadoEntity> findByCorreo(String correo);
	Optional<EmpleadoEntity> findByUsuarioId(Integer idUsuario);
	@Query("SELECT e FROM empleado e JOIN e.usuario u WHERE u.usuario = :value OR e.correo = :value ")
	Optional<EmpleadoEntity> searchByUsuaurioOrCorreo(@Param("value") String value);
}
