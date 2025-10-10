package com.bachuco.port;

import java.util.Optional;

import com.bachuco.model.Otp;

public interface OtpRepository {
	void save(Otp otp);
	Optional<Otp> findByUsuarioIdAndCodigo(String usuarioId, String codigo);
	Optional<Otp> findByUsuario(String usuario);
	void markAsUsed(Otp otp);
	void deleteExpired();
	void deleteOtp(String username);
}
