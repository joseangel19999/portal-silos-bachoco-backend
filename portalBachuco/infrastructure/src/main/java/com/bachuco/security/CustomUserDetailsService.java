package com.bachuco.security;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.bachuco.model.Usuario;
import com.bachuco.port.UsuarioRepositoryPort;

@Component
public class CustomUserDetailsService implements UserDetailsService{

	private final UsuarioRepositoryPort usuarioRepositoryPort;
	
	public CustomUserDetailsService(UsuarioRepositoryPort usuarioRepositoryPort) {
		this.usuarioRepositoryPort = usuarioRepositoryPort;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepositoryPort.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		 
		/*Collection<SimpleGrantedAuthority> authorities= new ArrayList<>();
        return User.builder()
                .username(usuario.getUsuario())
                .password(usuario.getPassword())
                //.authorities(usuario.getRoles().toArray(new String[0]))
                .authorities(authorities)
                .build();*/
		// 2. Transforma el modelo de dominio 'User' a un objeto UserDetails de Spring Security.
		var roles=usuario.getPerfiles().stream()
                .map(role -> role.getClave())
                .collect(Collectors.joining(","));
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getUsuario())
                .password(usuario.getPassword()) // La contraseña ya está encriptada
                .roles(roles)
                .build();
	}

}
