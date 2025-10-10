package com.bachuco.persistence.adapter;

import org.springframework.stereotype.Repository;

import com.bachuco.model.Puesto;
import com.bachuco.persistence.entity.PuestoEntity;
import com.bachuco.persistence.repository.PuestoJpaRepository;
import com.bachuco.port.PuestoRepositoryPort;

@Repository
public class PuestoJpaRepositoryAdapter implements PuestoRepositoryPort {

	private final PuestoJpaRepository puestoRepository;
	
	public PuestoJpaRepositoryAdapter(PuestoJpaRepository puestoRepository) {
		this.puestoRepository = puestoRepository;
	}

	@Override
	public Puesto findById(Integer id) {
		return toDomain(this.puestoRepository.findById(id).get());
	}
	
	private Puesto toDomain(PuestoEntity entity) {
		return new Puesto(entity.getId(),entity.getDescripcion());
	}

}
