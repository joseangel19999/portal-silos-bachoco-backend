package com.bachuco.service.usecase;

import java.util.List;
import java.util.Optional;

import com.bachuco.exception.UsuarioDuplicadoException;
import com.bachuco.model.EmpleadoExternoRequest;
import com.bachuco.model.EmpleadoExternoResponse;
import com.bachuco.port.EmpleadoExternoRepositoryPort;

public class EmpleadoExternoUseCase {
	
	private final EmpleadoExternoRepositoryPort empExternoRepositoryPort;

	public EmpleadoExternoUseCase(EmpleadoExternoRepositoryPort empExternoRepositoryPort) {
		this.empExternoRepositoryPort = empExternoRepositoryPort;
	}
	
	public EmpleadoExternoResponse save(EmpleadoExternoRequest req) {
		Optional<EmpleadoExternoResponse> result=this.empExternoRepositoryPort.findByCorreo(req.getCorreo());
		if(result.isPresent()) {
			throw new UsuarioDuplicadoException("Se suplica el correo");
		}
		return this.empExternoRepositoryPort.save(req).get();
	}

	public List<EmpleadoExternoResponse> findAll(){
		return this.empExternoRepositoryPort.findAll();
	}
	
	public void delete(Integer id) {
		this.empExternoRepositoryPort.delete(id);
	}
	
	public void update(Integer id,EmpleadoExternoRequest req) {
		this.empExternoRepositoryPort.update(id, req);
	}
}
