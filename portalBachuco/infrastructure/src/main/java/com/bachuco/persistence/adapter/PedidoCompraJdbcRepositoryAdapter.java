package com.bachuco.persistence.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import com.bachuco.model.PedidoSapResponseDTO;
import com.bachuco.model.PedidoTrasladoSapResponseDTO;
import com.bachuco.model.procedores.PedidoCompraDTO;
import com.bachuco.persistence.repository.PedidoCompraJdbcRepository;
import com.bachuco.port.PedidoCompraJdbcRepositoryPort;
import com.bachuco.port.PedidoCompraSapPort;
import com.bachuco.port.PedidoTrasladoSapPort;
import com.bachuco.secutiry.utils.BatchUtils;

import jakarta.persistence.EntityManager;

@Component
public class PedidoCompraJdbcRepositoryAdapter implements PedidoCompraJdbcRepositoryPort {

	private final PedidoCompraJdbcRepository pedidoCompraJdbcRepository;
	private final PedidoCompraSapPort pedidoCompraSapWebClientAdapter;
	private final PedidoTrasladoSapPort pedidoTrasladoSapPort;
	private final String urlPedCompra = "http://bopaxdev01.bachoco.net:50000/RESTAdapter/portal-cosecha";

	public PedidoCompraJdbcRepositoryAdapter(PedidoCompraJdbcRepository pedidoCompraJdbcRepository,
			PedidoCompraSapPort pedidoCompraSapWebClientAdapter, PedidoTrasladoSapPort pedidoTrasladoSapPort) {
		this.pedidoCompraJdbcRepository = pedidoCompraJdbcRepository;
		this.pedidoCompraSapWebClientAdapter = pedidoCompraSapWebClientAdapter;
		this.pedidoTrasladoSapPort = pedidoTrasladoSapPort;
	}

	private List<PedidoSapResponseDTO> findAllPedidoCompra(String claveSilo, String claveMaterial, String fechaInicio,
			String fechaFin) {
		return this.pedidoCompraSapWebClientAdapter.findAllPedidoCompra(claveSilo,claveMaterial, toReplace(fechaInicio),
				toReplace(fechaFin), urlPedCompra);
	}

	private List<PedidoTrasladoSapResponseDTO> findAllPedidoCTraslado(String claveSilo, String claveMaterial,
			String fechaInicio, String fechaFin) {
		return this.pedidoTrasladoSapPort.findAllPedTraslado(claveSilo,claveMaterial, toReplace(fechaInicio),
				toReplace(fechaFin), urlPedCompra);
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
			String cantidadDespacho = mapFolios.get(folio);
			if (cantidadDespacho != null) {
				String cantPendDespacho="0";
				if(item.getCantidadPedido()!=null) {
					cantPendDespacho=String.valueOf(Float.parseFloat(item.getCantidadPedido())-Float.parseFloat(cantidadDespacho));
				}
				// Crear nuevo objeto modificado (no mutamos el original)
				item.setCantidadDespacho(cantidadDespacho);
				item.setCantidadPendienteDespacho(cantidadDespacho);
				return item;
			} else {
				return item; // sin cambios
			}
		}).toList(); // en Java 21 toList() devuelve lista inmutable
		return nuevaListCompras;
	}

	@Override
	public List<PedidoCompraDTO> findByFilterSiloAndMaterialAnFecha(String claveSilo, String claveMaterial,
			String fechaInicio, String fechaFin) {
		List<PedidoCompraDTO> response = new ArrayList<>();
		List<String> foliosExists = new ArrayList<>();
		List<PedidoTrasladoSapResponseDTO> pedidosTrasladoSap = findAllPedidoCTraslado(claveSilo, claveMaterial,
				fechaInicio, fechaFin);
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
			List<String> foliosNoExistSap = pedidosSapNoExistBd.stream()
					.map(p -> buildFolioPedCompraAnPosicion(p.getPedCompra(), p.getPosicion())).toList();
			if (pedidosSapNoExistBd.size() > 0) {
				this.pedidoCompraJdbcRepository.savePedidoCompra(pedidosSapNoExistBd);
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
