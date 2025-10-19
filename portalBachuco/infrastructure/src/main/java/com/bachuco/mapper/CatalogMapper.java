package com.bachuco.mapper;

import com.bachuco.model.Departamento;
import com.bachuco.model.Puesto;
import com.bachuco.persistence.entity.DepartamentoEntity;
import com.bachuco.persistence.entity.PuestoEntity;

public class CatalogMapper {

	public static Departamento toDomain(DepartamentoEntity entity) {
		Departamento depto= new Departamento();
		depto.setId(entity.getId());
		depto.setNombre(entity.getNombre());
		return depto;
	}
	
	public static Puesto toDomain(PuestoEntity entity) {
		Puesto puesto= new Puesto();
		puesto.setId(entity.getId());
		puesto.setDescripcion(entity.getDescripcion());
		return puesto;
	}
}
