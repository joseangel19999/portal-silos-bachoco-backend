package com.bachuco.service.usecase;

import java.util.List;

import com.bachuco.exception.RegistroNoCreadoException;
import com.bachuco.model.procedores.PedidoTrasladoArriboDTO;
import com.bachuco.model.procedores.PedidoTrasladoDTO;
import com.bachuco.port.PedidoTrasladoJdbcRepositoryPort;

public class PedidoTrasladoJdbcUseCase {
	
	private final PedidoTrasladoJdbcRepositoryPort pedidoTrasladoJdbcRepositoryPort;

	public PedidoTrasladoJdbcUseCase(PedidoTrasladoJdbcRepositoryPort pedidoTrasladoJdbcRepositoryPort) {
		this.pedidoTrasladoJdbcRepositoryPort = pedidoTrasladoJdbcRepositoryPort;
	}
	public List<PedidoTrasladoDTO> findByFilters(String claveSilo,String claveMaterial,String fechaInicio,String fechaFin){
		try {
			return this.pedidoTrasladoJdbcRepositoryPort.findByFilterSiloAndMaterialAnFecha(claveSilo, claveMaterial, fechaInicio, fechaFin);
		}catch (Exception e) {
			throw new RegistroNoCreadoException(e.getMessage());
		}
	}
	public List<PedidoTrasladoDTO> findByFiltersCantidadDisponible(Integer siloId,Integer materialId,String fechaInicio,String fechaFin){
		return this.pedidoTrasladoJdbcRepositoryPort.findByFiltersCantidadDisponible(siloId,materialId, fechaInicio, fechaFin);
	}
	public List<PedidoTrasladoArriboDTO> findByFilterProgramArribo(Integer siloId,String planta,Integer materialId){
		return this.pedidoTrasladoJdbcRepositoryPort.findByFilterProgramArribo(siloId,planta,materialId);
	}
	public List<PedidoTrasladoArriboDTO> findByPedTrasladoByConfDespacho(Integer siloId,String planta,Integer materialId){
		return this.pedidoTrasladoJdbcRepositoryPort.findByPedTrasladoByConfDespacho(siloId,planta,materialId);
	}
}
