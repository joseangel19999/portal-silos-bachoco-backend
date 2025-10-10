package com.bachuco.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bachuco.model.procedores.PedidoTrasladoArriboDTO;
import com.bachuco.model.procedores.PedidoTrasladoDTO;
import com.bachuco.service.usecase.PedidoTrasladoJdbcUseCase;

@RestController
@RequestMapping("/v1/pedido-traslado")
public class PedidoTrasladoController {

	private final PedidoTrasladoJdbcUseCase pedidoTrasladoJdbcUseCase;

	public PedidoTrasladoController(PedidoTrasladoJdbcUseCase pedidoTrasladoJdbcUseCase) {
		this.pedidoTrasladoJdbcUseCase = pedidoTrasladoJdbcUseCase;
	}
	
	@GetMapping("/filters")
	public ResponseEntity<List<PedidoTrasladoDTO>> findAllByFilters(@RequestParam String claveSilo,@RequestParam String claveMaterial,
			@RequestParam String fechaInicio,@RequestParam String fechaFin){
		List<PedidoTrasladoDTO> response=this.pedidoTrasladoJdbcUseCase.findByFilters(claveSilo, claveMaterial, fechaInicio, fechaFin);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	
	@GetMapping("/filters/program")
	public ResponseEntity<List<PedidoTrasladoArriboDTO>> findAllByFilterProgramArribo(@RequestParam Integer siloId,@RequestParam Integer materialId,
			@RequestParam String planta){
		List<PedidoTrasladoArriboDTO> response=this.pedidoTrasladoJdbcUseCase.findByFilterProgramArribo(siloId, materialId, "");
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
}
