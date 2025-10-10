package com.bachuco.persistence.adapter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.bachuco.exception.CredencialesInvalidasException;
import com.bachuco.exception.NotFoundPasswordException;
import com.bachuco.exception.OtpInvalidException;
import com.bachuco.persistence.entity.UsuarioEntity;
import com.bachuco.persistence.repository.UsuarioJpaRepository;
import com.bachuco.port.AuthenticationPort;
import com.bachuco.port.OtpRepository;
import com.bachuco.security.CustomUserDetailsService;
import com.bachuco.security.JwtSecurityService;
import com.bachuco.service.usecase.GenerarYEnviarOtpUseCase;
@Component
public class AuthenticationAdapter implements AuthenticationPort {
	
	private final AuthenticationManager authenticationManager;
	private final JwtSecurityService jwtSecurityService;
	private final UsuarioJpaRepository usuarioJpaRepository;
	private final GenerarYEnviarOtpUseCase generarYEnviarOtpUseCase;
	private final OtpRepository otpRepository;
	private final CustomUserDetailsService customUserDetailsService;
	private String ROL_PENDING_2FA="2FA_PENDING";
	
	
	public AuthenticationAdapter(AuthenticationManager authenticationManager, JwtSecurityService jwtSecurityService,
			UsuarioJpaRepository usuarioJpaRepository,GenerarYEnviarOtpUseCase generarYEnviarOtpUseCase,OtpRepository otpRepository,
			CustomUserDetailsService customUserDetailsService) {
		this.authenticationManager = authenticationManager;
		this.jwtSecurityService = jwtSecurityService;
		this.usuarioJpaRepository = usuarioJpaRepository;
		this.generarYEnviarOtpUseCase=generarYEnviarOtpUseCase;
		this.otpRepository=otpRepository;
		this.customUserDetailsService=customUserDetailsService;
	}

	@Override
	public String authenticate(String username, String password) {
		var usuario = usuarioJpaRepository.findByUsuario(username);
		if (usuario.isEmpty()) {
			throw new CredencialesInvalidasException();
		}
		UsuarioEntity user=usuario.get();
		if (user.getPassword()==null || user.getPassword().trim().length()==0) {
			throw new NotFoundPasswordException("No existe el password");
		}
		this.generarYEnviarOtpUseCase.ejecutar(user.getId(),user.getUsuario(), username);
		String token=this.jwtSecurityService.generateToken2FA(username,ROL_PENDING_2FA);
		return token;
	}

	@Override
	public String verifyOtpAnGenerateToken(String username, String otp) {
		var resultOtp = otpRepository.findByUsuario(username);
		if(resultOtp.isPresent() && resultOtp.get().getCodigo().equals(otp)) {
			this.otpRepository.deleteOtp(username);
			 UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
			 Authentication authentication = new UsernamePasswordAuthenticationToken(
		                userDetails, null, userDetails.getAuthorities()
		            );
			 SecurityContextHolder.getContext().setAuthentication(authentication);
			return this.jwtSecurityService.generateToken(authentication);
		}else {
			throw new OtpInvalidException("Clave Otp Invalido");
		}
		
	}

}
