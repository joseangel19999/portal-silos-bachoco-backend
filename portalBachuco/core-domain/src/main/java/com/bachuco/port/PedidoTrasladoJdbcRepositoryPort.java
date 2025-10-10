package com.bachuco.port;

import java.util.List;

import com.bachuco.model.procedores.PedidoTrasladoArriboDTO;
import com.bachuco.model.procedores.PedidoTrasladoDTO;

public interface PedidoTrasladoJdbcRepositoryPort {

	public List<PedidoTrasladoDTO> findByFilterSiloAndMaterialAnFecha(String claveSilo,String claveMaterial,String fechaInicio,String fechaFin);
	public List<PedidoTrasladoArriboDTO> findByFilterProgramArribo(Integer siloId,Integer materialId,String planta);
}
