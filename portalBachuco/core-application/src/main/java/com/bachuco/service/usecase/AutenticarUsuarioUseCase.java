package com.bachuco.service.usecase;

import java.util.Optional;

import com.bachuco.model.Usuario;
import com.bachuco.model.service.AuthenticationService;
import com.bachuco.service.IAuthenticate;

public class AutenticarUsuarioUseCase implements IAuthenticate{

	private final AuthenticationService authenticationService;

	public AutenticarUsuarioUseCase(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
	
	@Override
	public Optional<Usuario> ejecutar(String username, String password) {
		return this.authenticationService.authenticate(username, password);
	}
}
