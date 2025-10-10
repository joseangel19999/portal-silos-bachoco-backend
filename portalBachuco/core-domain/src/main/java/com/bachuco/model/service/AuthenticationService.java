package com.bachuco.model.service;

import java.util.Optional;

import com.bachuco.model.Usuario;

public interface AuthenticationService {

	Optional<Usuario> authenticate(String username,String password); 
}
