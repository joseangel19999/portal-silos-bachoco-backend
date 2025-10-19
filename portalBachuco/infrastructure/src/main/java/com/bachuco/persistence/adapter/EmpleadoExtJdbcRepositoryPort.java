package com.bachuco.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import com.bachuco.model.EmpleadoExternoRequest;
import com.bachuco.model.EmpleadoExternoResponse;
import com.bachuco.persistence.repository.EmpleadoExternoJdbcRepository;
import com.bachuco.port.EmpleadoExternoRepositoryPort;

@Component
public class EmpleadoExtJdbcRepositoryPort implements EmpleadoExternoRepositoryPort {

	private final EmpleadoExternoJdbcRepository empleadoExternoRepository;

	public EmpleadoExtJdbcRepositoryPort(EmpleadoExternoJdbcRepository empleadoExternoRepository) {
		this.empleadoExternoRepository = empleadoExternoRepository;
	}

	@Override
	public Optional<EmpleadoExternoResponse> save(EmpleadoExternoRequest req) {
		EmpleadoExternoResponse response=empleadoExternoRepository.save(req);
		if(response!=null) {
			return Optional.ofNullable(response);
		}
		return Optional.empty();
	}

	@Override
	public void update(Integer id,EmpleadoExternoRequest req) {
		this.empleadoExternoRepository.update(id, req);
	}

	@Override
	public List<EmpleadoExternoResponse> findAll() {
		try {
			return this.empleadoExternoRepository.findAll();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Optional<EmpleadoExternoResponse> findByCorreo(String correo) {
		EmpleadoExternoResponse response=this.empleadoExternoRepository.findByCorreo(correo);
		if(response==null) {
			Optional.empty();
		}
		return Optional.ofNullable(response);
	}

	@Override
	public void delete(Integer id) {
		this.empleadoExternoRepository.delete(id);
	}
	
}
