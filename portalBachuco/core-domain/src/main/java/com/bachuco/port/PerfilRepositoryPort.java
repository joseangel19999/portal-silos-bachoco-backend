package com.bachuco.port;

import java.util.List;
import java.util.Optional;

import com.bachuco.model.Perfil;

public interface PerfilRepositoryPort {

	public Perfil save(Perfil perfil);
	public Perfil update(Perfil perfil);
	public void delete(Integer id);
	public Optional<Perfil> findById(Integer id);
	public Optional<Perfil> findByClave(String clave);
	public List<Perfil> findAll();
}
