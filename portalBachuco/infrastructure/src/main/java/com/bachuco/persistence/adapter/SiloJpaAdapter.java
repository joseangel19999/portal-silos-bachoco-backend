package com.bachuco.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.bachuco.mapper.SiloMapper;
import com.bachuco.model.Silo;
import com.bachuco.persistence.entity.SiloEntity;
import com.bachuco.persistence.repository.SiloJpaRepository;
import com.bachuco.port.SiloRepositoryPort;

@Component
public class SiloJpaAdapter implements SiloRepositoryPort{
	
	private final SiloJpaRepository siloJpaRepository;
	
	public SiloJpaAdapter(SiloJpaRepository siloJpaRepository) {
		this.siloJpaRepository = siloJpaRepository;
	}

	@Override
	public 	Optional<Silo> save(Silo model) {
		SiloEntity entity=this.siloJpaRepository.save(SiloMapper.toEntitySave(model));
		return Optional.ofNullable(SiloMapper.toDomain(entity));
	}

	@Override
	public Optional<Silo> update(Silo model) {
		try {
			Optional<SiloEntity> siloEntity= this.siloJpaRepository.findById(model.getId());
			if(siloEntity.isPresent()) {
				SiloEntity siloUpdate=siloEntity.get();
				siloUpdate.setSilo(model.getSilo());
				siloUpdate.setNombre(model.getNombre());
				siloUpdate.setSociedad(model.getSociedad());
				SiloEntity updated= this.siloJpaRepository.save(siloUpdate);
				return Optional.ofNullable(SiloMapper.toDomain(updated)); 
			}
			return Optional.empty();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public void delete(Integer id) {
		try {
			Optional<SiloEntity> siloEntity= this.siloJpaRepository.findById(id);
			this.siloJpaRepository.delete(siloEntity.get());
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public List<Silo> findAll() {
		return SiloMapper.toListDomain(this.siloJpaRepository.findAll());
	}

	
	@Override
	public Optional<Silo> findById(Integer id) {
		try {
			Optional<SiloEntity> siloEntity= this.siloJpaRepository.findById(id);
			if(siloEntity.isPresent()) {
				return Optional.ofNullable(SiloMapper.toDomain(siloEntity.get()));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Optional<Float> stock(Integer siloId) {
		try {
			Optional<Float> siloEntity= this.siloJpaRepository.findStockById(siloId);
			if(siloEntity.isPresent()) {
				return Optional.ofNullable(siloEntity.get());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	@Override
	public Optional<Silo> findByClave(String clave) {
		Optional<SiloEntity> entity=this.siloJpaRepository.findBySilo(clave);
		if(entity.isPresent()) {
			return Optional.ofNullable(SiloMapper.toDomain(entity.get()));
		}
		return Optional.empty();
	}

}
