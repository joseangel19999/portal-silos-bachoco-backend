package com.bachuco.service.usecase;

import java.util.List;

import com.bachuco.model.Departamento;
import com.bachuco.port.DepartamentoRepositoryPort;

public class DepartamentoUseCase {
	private final DepartamentoRepositoryPort departamentoRepositoryPort;

	public DepartamentoUseCase(DepartamentoRepositoryPort departamentoRepositoryPort) {
		this.departamentoRepositoryPort = departamentoRepositoryPort;
	}

	public List<Departamento> findAll(){
		return this.departamentoRepositoryPort.findAll();
	}

}
