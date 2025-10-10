package com.bachuco.port;

import java.util.Optional;

import com.bachuco.model.Categoria;

public interface CategoriaRepositoryPort {
	
	public Optional<Categoria> findById(Integer materialId);

}
