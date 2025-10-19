package com.bachuco.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.bachuco.mapper.MaterialMapper;
import com.bachuco.model.Material;
import com.bachuco.persistence.entity.MaterialEntity;
import com.bachuco.persistence.repository.MateriaJpalRepository;
import com.bachuco.port.MaterialRepositoryPort;

@Repository
public class MaterialJpaAdapter implements MaterialRepositoryPort {
	
	private final MateriaJpalRepository materialRepository;
	
	public MaterialJpaAdapter(MateriaJpalRepository materialRepository) {
		this.materialRepository=materialRepository;
	}

	@Override
	public void save(Material material) {
		this.materialRepository.save(MaterialMapper.toEntity(material));
	}

	@Override
	public void update(Material material) {
		Optional<MaterialEntity> opt=this.materialRepository.findByMaterialId(material.getMaterialId());
		if(opt.isPresent()) {
			MaterialEntity update=opt.get();
			update.setDescripcion(material.getDescripcion());
			update.setNumero(material.getNumero());
			this.materialRepository.save(update);
		}
	}

	@Override
	public void delete(Integer id) {
		Optional<MaterialEntity> opt=this.materialRepository.findByMaterialId(id);
		if(opt.isPresent()) {
			this.materialRepository.delete(opt.get());
		}
	}

	@Override
	public Optional<Material> findById(Integer id) {
		Optional<MaterialEntity> opt=this.materialRepository.findByMaterialId(id);
		if(opt.isPresent()) {
			MaterialEntity entity= opt.get();
			return Optional.ofNullable(MaterialMapper.toDomain(entity));
		}
		return Optional.empty();
	}

	@Override
	public List<Material> findAll() {
		return MaterialMapper.toDomain(this.materialRepository.findAll());
	}

	@Override
	public Optional<Material> findByNumero(String numero) {
		Optional<MaterialEntity> opt=this.materialRepository.findByNumero(numero);
		if(opt.isPresent()) {
			MaterialEntity entity= opt.get();
			return Optional.ofNullable(MaterialMapper.toDomain(entity));
		}
		return Optional.empty();
	}

}
