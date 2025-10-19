package com.bachuco.port;

import java.util.List;

import com.bachuco.model.PedidoSapResponseDTO;

public interface PedidoCompraSapPort {
	public List<PedidoSapResponseDTO> findAllPedidoCompra(String claveSilo,String claveMaterial,String fechaInicio,String fechaFin,String rutaUrl);
}
