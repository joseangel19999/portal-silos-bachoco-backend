package com.bachuco.service;

import java.util.Optional;

import com.bachuco.model.Usuario;

public interface IAuthenticate {
	
	public Optional<Usuario> ejecutar(String username,String password);

}
