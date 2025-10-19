package com.bachuco.port;

import java.util.List;
import com.bachuco.model.procedores.PedidoCompraDTO;

public interface PedidoCompraJdbcRepositoryPort {

	public List<PedidoCompraDTO> findByFilterSiloAndMaterialAnFecha(String claveSilo,String claveMaterial,String fechaInicio,String fechaFin);
	public List<PedidoCompraDTO> findAllComprasSapByFilters(String claveSilo,String claveMaterial,String fechaInicio,String fechaFin);
}
