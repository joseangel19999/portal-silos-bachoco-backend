package com.bachuco.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bachuco.dto.UsuarioUpdatePasswordRequest;
import com.bachuco.service.usecase.UsuarioUseCase;

@RestController
@RequestMapping("/v1/usuario")
public class UsuarioController {

	public final UsuarioUseCase usuarioUseCase;

	public UsuarioController(UsuarioUseCase usuarioUseCase) {
		this.usuarioUseCase = usuarioUseCase;
	}
	
	@PostMapping("/update-password")
	public ResponseEntity<?> updatePassword(@RequestBody UsuarioUpdatePasswordRequest request){
		this.usuarioUseCase.updatePassword(request.username(),request.password());
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
	
}
