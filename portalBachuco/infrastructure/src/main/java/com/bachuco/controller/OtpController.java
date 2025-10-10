package com.bachuco.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bachuco.service.usecase.GenerarYEnviarOtpUseCase;

@RestController
@RequestMapping("/v1/otp")
public class OtpController {

	private final GenerarYEnviarOtpUseCase generarYEnviarOtpUseCase;

	public OtpController(GenerarYEnviarOtpUseCase generarYEnviarOtpUseCase) {
		this.generarYEnviarOtpUseCase = generarYEnviarOtpUseCase;
	}

	@GetMapping
	public ResponseEntity<String> getOtp() {
		//this.generarYEnviarOtpUseCase.ejecutar("joseCode001", "jose-hernandez@latbc.com");
		return new ResponseEntity<String>("TU OTP: ", HttpStatus.OK);
	}
	
	

}
