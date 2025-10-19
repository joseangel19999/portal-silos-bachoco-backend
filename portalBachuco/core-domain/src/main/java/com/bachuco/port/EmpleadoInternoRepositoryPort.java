package com.bachuco.port;

import java.util.List;
import java.util.Optional;

import com.bachuco.model.EmpleadoInterno;
import com.bachuco.model.EmpleadoInternoRequest;
import com.bachuco.model.EmpleadoInternoResponse;

public interface EmpleadoInternoRepositoryPort {
	
	//public EmpleadoInterno save(EmpleadoInterno empleadoInterno);
	public Optional<EmpleadoInterno> save(Integer empleadoId,Integer departamentoId,Integer puestoId);
	public void update(Integer id,EmpleadoInternoRequest request);
	public List<EmpleadoInternoResponse> findAll();
	public void save(EmpleadoInternoRequest request) throws Exception;
	public void delete(Integer id);

}
