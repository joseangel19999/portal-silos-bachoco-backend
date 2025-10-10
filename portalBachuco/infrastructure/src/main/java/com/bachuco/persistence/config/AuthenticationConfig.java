package com.bachuco.persistence.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bachuco.port.PasswordEncoderPort;
import com.bachuco.port.TokenProviderPort;
import com.bachuco.port.UsuarioRepositoryPort;
import com.bachuco.service.usecase.AuthenticationUseCase;

@Configuration
public class AuthenticationConfig {

	/*@Bean
	public AuthenticationUseCase autenticarUsuarioUseCase(UsuarioRepositoryPort usuarioRepo,
			PasswordEncoderPort passwordEncoder, TokenProviderPort tokenProvider) {
		return new AuthenticationUseCase(usuarioRepo, passwordEncoder, tokenProvider);
	}*/

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Este bean es necesario si necesitas inyectar AuthenticationManager en otros
	// servicios (ej. AuthenticationService).
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
