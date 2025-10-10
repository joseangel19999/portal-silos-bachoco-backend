package com.bachuco.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.bachuco.model.Categoria;
import com.bachuco.persistence.entity.CategoriaEntity;
import com.bachuco.persistence.repository.CategoriaJpaRepository;
import com.bachuco.port.CategoriaRepositoryPort;

@Repository
public class CategoriaJpaAdapter implements CategoriaRepositoryPort {
	
	private final CategoriaJpaRepository categoriaJpaRepository;
	

	public CategoriaJpaAdapter(CategoriaJpaRepository categoriaJpaRepository) {
		this.categoriaJpaRepository = categoriaJpaRepository;
	}

	@Override
	public Optional<Categoria> findById(Integer materialId) {
		Optional<CategoriaEntity> categoriaEntiry= this.categoriaJpaRepository.findById(materialId);
		if(!categoriaEntiry.isEmpty()) {
			Categoria categoria= new Categoria(categoriaEntiry.get().getId(),categoriaEntiry.get().getNombre(),categoriaEntiry.get().getDescripcion());
			return Optional.ofNullable(categoria);
		}
		return Optional.empty();
	}

}
