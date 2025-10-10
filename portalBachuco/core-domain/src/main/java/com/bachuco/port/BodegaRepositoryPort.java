package com.bachuco.port;

import java.util.List;
import java.util.Optional;

import com.bachuco.model.Bodega;

public interface BodegaRepositoryPort {
	Bodega save(Bodega bodega);
	Bodega update(Bodega bodega);
	void delete(Integer id);
	Bodega findByCodigo(String codigo);
	Optional<Bodega> findById(Integer id);
	List<Bodega> findAll();
}
