package com.bachuco.persistence.adapter;

import org.springframework.stereotype.Repository;

import com.bachuco.model.Departamento;
import com.bachuco.persistence.entity.DepartamentoEntity;
import com.bachuco.persistence.repository.DepartamentoJpaRepository;
import com.bachuco.port.DepartamentoRepositoryPort;

@Repository
public class DepartamentoJpaRepositoryAdapter implements DepartamentoRepositoryPort{

	private final DepartamentoJpaRepository departamentoRepository;
	
	public DepartamentoJpaRepositoryAdapter(DepartamentoJpaRepository departamentoRepository) {
		this.departamentoRepository = departamentoRepository;
	}


	@Override
	public Departamento findById(Integer id) {
		return toDomain(this.departamentoRepository.findById(id).get());
	}
	
	public Departamento toDomain(DepartamentoEntity entity) {
		return new Departamento(entity.getId(),entity.getNombre());
	}

}
