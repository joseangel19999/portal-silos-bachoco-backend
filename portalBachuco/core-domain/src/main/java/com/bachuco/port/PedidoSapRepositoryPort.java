package com.bachuco.port;

import java.util.List;

import com.bachuco.model.PedidoSapResponseDTO;

public interface PedidoSapRepositoryPort {

	public List<PedidoSapResponseDTO> findAllPedidoCompra(String claveSilo,String claveMaterial,String fechaInicio,String fechaFin);
	public List<PedidoSapResponseDTO> findAllPedidoTraslado(String claveSilo,String claveMaterial,String fechaInicio,String fechaFin);
}
