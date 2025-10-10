package com.bachuco.port;

import java.util.List;
import java.util.Optional;

import com.bachuco.model.PedidoCompra;

public interface PedidoCompraRepositoryPort {

	public List<PedidoCompra> findByFilterSiloAndMaterialAnFecha(Integer siloId,Integer materialId,String fechaInicio,String fechaFin);
	public Optional<PedidoCompra> save(PedidoCompra pedidoCompra);
	public Optional<String> saveAll(List<PedidoCompra> pedidoCompras);
}
