package com.bachuco.port;

import java.util.List;

import com.bachuco.model.PedidoTrasladoSapResponseDTO;

public interface PedidoTrasladoSapPort {
	public List<PedidoTrasladoSapResponseDTO> findAllPedTraslado(String claveSilo,String claveMaterial,String fechaInicio,String fechaFin,String rutaUrl);
}
