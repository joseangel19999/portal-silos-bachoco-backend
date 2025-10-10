package com.bachuco.port;

import java.util.List;
import java.util.Optional;

import com.bachuco.model.Material;

public interface MaterialRepositoryPort {

	void save(Material material);
	void update(Material material);
	void delete(Integer id);
	Optional<Material> findById(Integer id);
	Optional<Material> findByNumero(String numero);
	List<Material> findAll();
}
