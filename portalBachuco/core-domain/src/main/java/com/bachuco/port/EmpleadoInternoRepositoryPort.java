package com.bachuco.port;

import java.util.Optional;

import com.bachuco.model.EmpleadoInterno;

public interface EmpleadoInternoRepositoryPort {
	
	//public EmpleadoInterno save(EmpleadoInterno empleadoInterno);
	public Optional<EmpleadoInterno> save(Integer empleadoId,Integer departamentoId,Integer puestoId);
	public Optional<EmpleadoInterno> update(EmpleadoInterno empleadoInterno);

}
