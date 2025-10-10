package com.bachuco.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bachuco.persistence.entity.EmpleadoEntity;

public interface EmpleadoJpaRepository extends JpaRepository<EmpleadoEntity, Integer>{
	
	public Optional<EmpleadoEntity> findById(Integer id);
	public Optional<EmpleadoEntity> findByCorreo(String correo);
	Optional<EmpleadoEntity> findByUsuarioId(Integer idUsuario);
	//@Query(name="SELECT e.correo FROM empleado e JOIN e.usuario u WHERE u.id = :idUsuario ")
	//public Optional<String> getCorreo(@Param("idUsuario") Integer id);
	/*
	 * 	@Query(name="select e.correo from tc_empleado e where e.TC_USUARIO_ID=:idUsuario ",nativeQuery = true)
	public Optional<String> getCorreo(@Param("idUsuario") Integer id);*/
}
