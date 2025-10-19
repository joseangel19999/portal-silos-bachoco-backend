package com.bachuco.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bachuco.mapper.BodegaMapper;
import com.bachuco.model.Bodega;
import com.bachuco.persistence.entity.BodegaEntity;
import com.bachuco.persistence.entity.SiloEntity;
import com.bachuco.persistence.repository.BodegaRepository;
import com.bachuco.port.BodegaRepositoryPort;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class BodegaJpaRepositoryAdapter implements BodegaRepositoryPort{

	private final BodegaRepository bodegaRepository;
	
	@PersistenceContext
    private EntityManager entityManager;
	
	public BodegaJpaRepositoryAdapter(BodegaRepository bodegaRepository) {
		this.bodegaRepository = bodegaRepository;
	}

	@Override
	public Bodega save(Bodega bodega) {
		BodegaEntity entity= BodegaMapper.toEntity(bodega);
		SiloEntity siloProxy=entityManager.getReference(SiloEntity.class, bodega.getSilo().getId());
		entity.setSilo(siloProxy);
		BodegaEntity newBodega= this.bodegaRepository.save(entity);
		return BodegaMapper.toDomain(newBodega);
	}

	
	@Override
	public Bodega update(Bodega bodega) {
		Optional<BodegaEntity> entity=this.bodegaRepository.findById(bodega.getId());
		BodegaEntity updateEntity=entity.get();
		SiloEntity siloProxy=entityManager.getReference(SiloEntity.class, bodega.getSilo().getId());
		updateEntity.setClave(bodega.getNombre());
		updateEntity.setSilo(siloProxy);
		BodegaEntity newBodega= this.bodegaRepository.save(updateEntity);
		return BodegaMapper.toDomain(newBodega);
	}

	@Override
	public void delete(Integer id) {
		Optional<BodegaEntity> entity=this.bodegaRepository.findById(id);
		if(entity.isPresent()) {
			this.bodegaRepository.delete(entity.get());
		}
	}

	@Override
	public Bodega findByCodigo(String codigo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Bodega> findAll() {
		return BodegaMapper.toDomain(this.bodegaRepository.findAll());
	}

	@Override
	public Optional<Bodega> findById(Integer id) {
		Optional<BodegaEntity> entity=this.bodegaRepository.findById(id);
		if(entity.isPresent()) {
			return Optional.ofNullable(BodegaMapper.toDomain(entity.get()));
		}
		return Optional.empty();
	}

	@Override
	public List<Bodega> findBySilo(Integer siloId) {
		return BodegaMapper.toDomain(this.bodegaRepository.findBySiloId(siloId));
	}

}
