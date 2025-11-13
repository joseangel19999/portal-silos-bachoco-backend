package com.bachoco.persistence.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bachoco.model.PedidoSapResponseDTO;
import com.bachoco.model.PedidoTrasladoSapResponseDTO;
import com.bachoco.model.procedores.PedidoCompraDTO;
import com.bachoco.persistence.config.SapProperties;
import com.bachoco.persistence.repository.PedidoCompraJdbcRepository;
import com.bachoco.port.PedidoCompraJdbcRepositoryPort;
import com.bachoco.port.PedidoCompraSapPort;
import com.bachoco.port.PedidoTrasladoSapPort;
import com.bachoco.secutiry.utils.BatchUtils;

@Component
public class PedidoCompraJdbcRepositoryAdapter implements PedidoCompraJdbcRepositoryPort {

	private final PedidoCompraJdbcRepository pedidoCompraJdbcRepository;
	private final PedidoCompraSapPort pedidoCompraSapWebClientAdapter;
	private final PedidoTrasladoSapPort pedidoTrasladoSapPort;
	private final SapProperties sapProperties;

	public PedidoCompraJdbcRepositoryAdapter(PedidoCompraJdbcRepository pedidoCompraJdbcRepository,
			PedidoCompraSapPort pedidoCompraSapWebClientAdapter, PedidoTrasladoSapPort pedidoTrasladoSapPort,
			SapProperties sapProperties) {
		this.pedidoCompraJdbcRepository = pedidoCompraJdbcRepository;
		this.pedidoCompraSapWebClientAdapter = pedidoCompraSapWebClientAdapter;
		this.pedidoTrasladoSapPort = pedidoTrasladoSapPort;
		this.sapProperties = sapProperties;
	}

	private List<PedidoSapResponseDTO> findAllPedidoCompra(String claveSilo, String claveMaterial, String fechaInicio,
			String fechaFin) {
		return this.pedidoCompraSapWebClientAdapter.findAllPedidoCompra(claveSilo,claveMaterial, toReplace(fechaInicio),
				toReplace(fechaFin), sapProperties.getUrl());
	}

	private List<PedidoTrasladoSapResponseDTO> findAllPedidoCTraslado(String claveSilo, String claveMaterial,
			String plantaDestino,String fechaInicio, String fechaFin) {
		return this.pedidoTrasladoSapPort.findAllPedTraslado(claveSilo,claveMaterial,plantaDestino, toReplace(fechaInicio),
				toReplace(fechaFin), sapProperties.getUrl());
	}

	private List<PedidoSapResponseDTO> assignedCantidadDespacho(List<PedidoSapResponseDTO> compra,
			List<PedidoTrasladoSapResponseDTO> traslado) {
		Map<String, String> mapFolios = new HashMap<>();
		for (PedidoTrasladoSapResponseDTO p : traslado) {
			mapFolios.put(
					buildFolioPedCompraAnPosicion(String.valueOf(p.getPedidoDeComprasAsociado()), p.getPosicion()),
					p.getCantidaddespacho());
		}
		List<PedidoSapResponseDTO> nuevaListCompras = compra.stream().map(item -> {
			String folio = buildFolioPedCompraAnPosicion(item.getPedCompra(), item.getPosicion());
			String cantidadDespachoPeTraslado = mapFolios.get(folio);
			if (cantidadDespachoPeTraslado != null) {
				String cantPendDespacho="0";
				if(item.getCantidadPedido()!=null) {
					cantPendDespacho=String.valueOf(Float.parseFloat(item.getCantidadPedido())-Float.parseFloat(cantidadDespachoPeTraslado));
				}
				// Crear nuevo objeto modificado (no mutamos el original)
				item.setCantidadPendienteDespacho(cantPendDespacho);
				item.setCantidadDespacho(cantidadDespachoPeTraslado);
				return item;
			} else {
				/*if(item.getCantidadPedido()!=null) {
					item.setCantidadPendienteDespacho(item.getCantidadPedido());
				}
				item.setCantidadDespacho(cantidadDespachoPeTraslado);
				item.setCantidadPendienteDespacho(item.getCantidadPedido());*/
				return item; // sin cambios
			}
		}).toList(); // en Java 21 toList() devuelve lista inmutable
		return nuevaListCompras;
	}

	@Override
	public List<PedidoCompraDTO> findByFilterSiloAndMaterialAnFecha(String claveSilo, String claveMaterial,
			String plantaDestino,String fechaInicio, String fechaFin) {
		List<PedidoCompraDTO> response = new ArrayList<>();
		List<String> foliosExists = new ArrayList<>();
		List<PedidoTrasladoSapResponseDTO> pedidosTrasladoSap = findAllPedidoCTraslado(claveSilo, claveMaterial,
				plantaDestino,fechaInicio, fechaFin);
		List<PedidoSapResponseDTO> pedidosSap = findAllPedidoCompra(claveSilo, claveMaterial, fechaInicio, fechaFin);
		List<PedidoSapResponseDTO> updateCompras = assignedCantidadDespacho(pedidosSap, pedidosTrasladoSap);
		
		if (updateCompras.size() > 0) {
			List<Map<String, Object>> foliosCantidadesModificar= new ArrayList<>();
			List<String> folioPedCompraPosicion = updateCompras.stream()
					.map(p -> buildFolioPedCompraAnPosicion(p.getPedCompra(), p.getPosicion())).toList();
			List<List<String>> batches = BatchUtils.partition(folioPedCompraPosicion, 100);
			for (List<String> batch : batches) {
				foliosExists.addAll(this.pedidoCompraJdbcRepository.findAllFoliosExist(batch));
				foliosCantidadesModificar.addAll(this.pedidoCompraJdbcRepository.findAllFoliosExistCantidades(batch));
			}
			//this.updateCantidades(updateCompras,convertToFolioMap(foliosCantidadesModificar));
			//se crear una lista set para evitar duplicados
			Set<String> foliosExist = new HashSet<>(foliosExists);
			List<PedidoSapResponseDTO> pedidosSapNoExistBd = updateCompras.stream().filter(
					p -> !foliosExist.contains(buildFolioPedCompraAnPosicion(p.getPedCompra(), p.getPosicion())))
					.toList();
			List<String> foliosNoExistSap = pedidosSapNoExistBd.stream()
					.map(p -> buildFolioPedCompraAnPosicion(p.getPedCompra(), p.getPosicion())).toList();
			if(foliosCantidadesModificar.size()>0) {
				this.pedidoCompraJdbcRepository.updateCantidades(updateCompras,convertToFolioMap(foliosCantidadesModificar));
			}
			if (pedidosSapNoExistBd.size() > 0) {
				this.pedidoCompraJdbcRepository.savePedidoCompra(pedidosSapNoExistBd,claveSilo);
				foliosExists.addAll(foliosNoExistSap);
				List<List<String>> batchesFolios = BatchUtils.partition(foliosExists, 100);
				for (List<String> batch : batchesFolios) {
					response.addAll(this.pedidoCompraJdbcRepository.findAllByFolioNumCompra(batch));
				}
				return response;
			} else {
				for (List<String> batch : batches) {
					response.addAll(this.pedidoCompraJdbcRepository.findAllByFolioNumCompra(batch));
				}
			}
		}
		return response;
	}
	
	public Map<String, Map<String, Object>> convertToFolioMap(List<Map<String, Object>> resultados) {
	    return resultados.stream()
	        .collect(Collectors.toMap(
	            row -> (String) row.get("folio"),
	            row -> row
	        ));
	}
	
	@Override
	public void executePedidoCompraByFilter(String claveSilo, String claveMaterial,String plantaDestino, String fechaInicio,
			String fechaFin) {
		List<String> foliosExists = new ArrayList<>();
		List<PedidoTrasladoSapResponseDTO> pedidosTrasladoSap = findAllPedidoCTraslado(claveSilo, claveMaterial,
				plantaDestino,fechaInicio, fechaFin);
		List<PedidoSapResponseDTO> pedidosSap = findAllPedidoCompra(claveSilo, claveMaterial, fechaInicio, fechaFin);
		List<PedidoSapResponseDTO> updateCompras = assignedCantidadDespacho(pedidosSap, pedidosTrasladoSap);
		if (updateCompras.size() > 0) {
			List<String> folioPedCompraPosicion = updateCompras.stream()
					.map(p -> buildFolioPedCompraAnPosicion(p.getPedCompra(), p.getPosicion())).toList();
			List<List<String>> batches = BatchUtils.partition(folioPedCompraPosicion, 100);
			for (List<String> batch : batches) {
				foliosExists.addAll(this.pedidoCompraJdbcRepository.findAllFoliosExist(batch));
			}
			Set<String> foliosExist = new HashSet<>(foliosExists);
			List<PedidoSapResponseDTO> pedidosSapNoExistBd = updateCompras.stream().filter(
					p -> !foliosExist.contains(buildFolioPedCompraAnPosicion(p.getPedCompra(), p.getPosicion())))
					.toList();
			if (pedidosSapNoExistBd.size() > 0) {
				this.pedidoCompraJdbcRepository.savePedidoCompra(pedidosSapNoExistBd,claveSilo);
			}
		}
	}

	@Override
	public List<PedidoCompraDTO> findAllComprasSapByFilters(String claveSilo, String claveMaterial, String fechaInicio,
			String fechaFin) {
		List<PedidoSapResponseDTO> pedidos = new ArrayList<>();
		if (pedidos.size() > 0) {
			List<String> folioPedCompraPosicion = pedidos.stream()
					.map(p -> buildFolioPedCompraAnPosicion(p.getPedCompra(), p.getPosicion())).toList();
			List<List<String>> resultFolios = pedidoCompraByNumPed(folioPedCompraPosicion);
			return null;
		}
		return null;
	}

	private List<PedidoCompraDTO> findAllPedidoCompras(List<List<String>> lote) {
		List<PedidoCompraDTO> result = new ArrayList<>();
		for (List<String> batch : lote) {
			result.addAll(this.pedidoCompraJdbcRepository.findAllByFolioNumCompra(batch));
		}
		return result;
	}

	private List<List<String>> pedidoCompraByNumPed(List<String> folioNumPedCompraPosicion) {
		List<List<String>> lotes = BatchUtils.partition(folioNumPedCompraPosicion, 100);
		return lotes;
	}

	private String toReplace(String value) {
		return value.replace("-", "");
	}

	private String buildFolioPedCompraAnPosicion(String pedCompra, String posicion) {
		return pedCompra.trim().concat("-").concat(posicion.trim());
	}
}
