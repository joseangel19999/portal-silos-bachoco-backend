package com.bachuco.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bachuco.mapper.CatalogMapper;
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

	@Override
	public List<Puesto> findAll() {
		return this.puestoRepository.findAll().stream().map(p->CatalogMapper.toDomain(p)).toList();
	}
	
	private Puesto toDomain(PuestoEntity entity) {
		return new Puesto(entity.getId(),entity.getDescripcion());
	}

}
