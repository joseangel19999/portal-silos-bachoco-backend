package com.bachuco.port;

import java.util.List;

import com.bachuco.model.Puesto;

public interface PuestoRepositoryPort {
	
	public Puesto findById(Integer id);
	public List<Puesto> findAll();

}
