package com.bachuco.app_boot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/pedido")
public class PedidoController {
	
	@GetMapping
	public ResponseEntity<String> saludo(){
		return new ResponseEntity<String>("Hola: ",HttpStatus.OK);
	}

}
