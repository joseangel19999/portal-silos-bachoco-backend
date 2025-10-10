package com.bachuco.port;

import java.util.List;
import java.util.Optional;

import com.bachuco.model.Usuario;

public interface UsuarioRepositoryPort {

	public Usuario save(Usuario usuario);
	public Usuario update(Usuario usuario);
	public List<Usuario> findAllByUsuario(String usuario);
	public Optional<Usuario> findByUsuario(String usuario);
	void updatePassword(String username, String password);
	Optional<Usuario> findByUsername(String username);
	void addUsuatioToPerfil(Integer usuarioId,Integer perfilId);
}
