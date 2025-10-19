package com.bachuco.port;

import java.util.List;

import com.bachuco.model.Departamento;

public interface DepartamentoRepositoryPort {

	public Departamento findById(Integer id);
	public List<Departamento> findAll();
}
