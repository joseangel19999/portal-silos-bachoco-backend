package com.bachuco.port;

import com.bachuco.model.Departamento;

public interface DepartamentoRepositoryPort {

	public Departamento findById(Integer id);
}
