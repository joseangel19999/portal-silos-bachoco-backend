package com.bachuco.service.usecase;

import java.time.LocalDateTime;
import java.util.Optional;

import com.bachuco.model.EmailMensaje;
import com.bachuco.model.Otp;
import com.bachuco.port.EmailSender;
import com.bachuco.port.EmpleadoRepositoryPort;
import com.bachuco.port.OtpRepository;
import com.bachuco.service.IGenerarYEnviarOtp;
import com.bachuco.service.IOtpGenerator;

public class GenerarYEnviarOtpUseCase implements IGenerarYEnviarOtp {

	private final OtpRepository otpRepository;
	private final EmailSender emailSender;
	private final IOtpGenerator optGenerator;
	private final EmpleadoRepositoryPort empleadoRepositoryPort;

	public GenerarYEnviarOtpUseCase(OtpRepository otpRepository, EmailSender emailSender, IOtpGenerator otpGenerator,
			EmpleadoRepositoryPort empleadoRepositoryPort) {
		this.otpRepository = otpRepository;
		this.emailSender = emailSender;
		this.optGenerator = otpGenerator;
		this.empleadoRepositoryPort = empleadoRepositoryPort;
	}

	@Override
	public void ejecutar(Integer usuarioId, String username, String destino) {
		Optional<String> correo = this.empleadoRepositoryPort.getCorreo(usuarioId);
		if (correo.isPresent()) {
			String codigo = optGenerator.generarCodigo();
			Otp otp = new Otp(username, codigo, LocalDateTime.now().plusMinutes(3), false);
			otpRepository.save(otp);
			EmailMensaje email = new EmailMensaje(correo.get(), "Codigo OTP", "El código de acceso es: " + codigo);
			emailSender.enviar(email);
		}
	}

}
