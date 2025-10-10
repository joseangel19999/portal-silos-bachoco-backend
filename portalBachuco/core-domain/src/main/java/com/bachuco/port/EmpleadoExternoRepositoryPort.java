package com.bachuco.port;

import java.util.List;
import java.util.Optional;

import com.bachuco.model.EmpleadoExternoRequest;
import com.bachuco.model.EmpleadoExternoResponse;

public interface EmpleadoExternoRepositoryPort {

	public Optional<EmpleadoExternoResponse> save(EmpleadoExternoRequest req);
	public Optional<EmpleadoExternoResponse> findByCorreo(String correo);
	public List<EmpleadoExternoResponse> findAll();
	public void update(Integer id,EmpleadoExternoRequest req);
	void delete(Integer id);
}
