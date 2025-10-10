package com.bachuco.port;

import com.bachuco.model.Usuario;

public interface TokenProviderPort {
	String generateToken(Usuario usuario);
    boolean validateToken(String token);
    String getUsernameFromToken(String token);
}
