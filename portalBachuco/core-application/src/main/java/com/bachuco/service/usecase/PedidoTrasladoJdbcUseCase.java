package com.bachuco.service.usecase;

import java.util.List;

import com.bachuco.model.procedores.PedidoTrasladoArriboDTO;
import com.bachuco.model.procedores.PedidoTrasladoDTO;
import com.bachuco.port.PedidoTrasladoJdbcRepositoryPort;

public class PedidoTrasladoJdbcUseCase {
	
	private final PedidoTrasladoJdbcRepositoryPort pedidoTrasladoJdbcRepositoryPort;

	public PedidoTrasladoJdbcUseCase(PedidoTrasladoJdbcRepositoryPort pedidoTrasladoJdbcRepositoryPort) {
		this.pedidoTrasladoJdbcRepositoryPort = pedidoTrasladoJdbcRepositoryPort;
	}
	
	public List<PedidoTrasladoDTO> findByFilters(String claveSilo,String claveMaterial,String fechaInicio,String fechaFin){
		return this.pedidoTrasladoJdbcRepositoryPort.findByFilterSiloAndMaterialAnFecha(claveSilo, claveMaterial, fechaInicio, fechaFin);
	}
	
	public List<PedidoTrasladoArriboDTO> findByFilterProgramArribo(Integer siloId,Integer materialId,String planta){
		return this.pedidoTrasladoJdbcRepositoryPort.findByFilterProgramArribo(siloId, materialId,planta);
	}
}
