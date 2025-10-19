package com.bachuco.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bachuco.model.Departamento;
import com.bachuco.model.Puesto;
import com.bachuco.service.usecase.DepartamentoUseCase;
import com.bachuco.service.usecase.PuestoUseCase;

@RestController
@RequestMapping("/v1/catalog")
public class CatalogController {
	
	private final DepartamentoUseCase deptoUseCase;
	private final PuestoUseCase puestoUseCase;
	
	public CatalogController(DepartamentoUseCase deptoUseCase, PuestoUseCase puestoUseCase) {
		this.deptoUseCase = deptoUseCase;
		this.puestoUseCase = puestoUseCase;
	}

	@GetMapping("/departamento")
	public ResponseEntity<List<Departamento>> findAllDepto(){
		return new ResponseEntity<>(this.deptoUseCase.findAll(),HttpStatus.OK);
	}
	
	@GetMapping("/puesto")
	public ResponseEntity<List<Puesto>> findAllPuesto(){
		return new ResponseEntity<>(this.puestoUseCase.findAll(),HttpStatus.OK);
	}

}
