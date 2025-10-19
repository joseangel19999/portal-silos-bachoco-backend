package com.bachuco.service.usecase;

import java.util.List;

import com.bachuco.model.Puesto;
import com.bachuco.port.PuestoRepositoryPort;

public class PuestoUseCase {

	private final PuestoRepositoryPort puestoRepositoryPort;

	public PuestoUseCase(PuestoRepositoryPort puestoRepositoryPort) {
		this.puestoRepositoryPort = puestoRepositoryPort;
	}
	
	public List<Puesto> findAll(){
		return this.puestoRepositoryPort.findAll();
	}
	
}
