package com.bachuco.service.usecase;

import com.bachuco.exception.UsuarioDuplicadoException;
import com.bachuco.model.Usuario;
import com.bachuco.port.UsuarioRepositoryPort;

public class UsuarioUseCase {

	private final UsuarioRepositoryPort usuarioRepositoryPort;

	public UsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
		this.usuarioRepositoryPort = usuarioRepositoryPort;
	}
	
	public Usuario save(Usuario usuario) {
		var result = this.usuarioRepositoryPort.findByUsuario(usuario.getUsuario());
		if (result.isPresent() && result.get().getId() != null) {
			throw new UsuarioDuplicadoException("El usuario ya existe");
		}
		return this.usuarioRepositoryPort.save(usuario);
	}
	public void updatePassword(String username,String password) {
		this.usuarioRepositoryPort.updatePassword(username, password);
	}
}
