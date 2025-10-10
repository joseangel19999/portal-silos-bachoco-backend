package com.bachuco.persistence.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import com.bachuco.model.PedidoSapResponseDTO;
import com.bachuco.model.procedores.PedidoCompraDTO;
import com.bachuco.persistence.repository.PedidoCompraJdbcRepository;
import com.bachuco.port.PedidoCompraJdbcRepositoryPort;
import com.bachuco.secutiry.utils.BatchUtils;

import jakarta.persistence.EntityManager;

@Component
public class PedidoCompraJdbcRepositoryAdapter implements PedidoCompraJdbcRepositoryPort {

	private final PedidoCompraJdbcRepository pedidoCompraJdbcRepository;
	private final PedidoCompraSapWebClientAdapter pedidoCompraSapWebClientAdapter;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	private EntityManager em;
	private final String urlPedCompra="http://bopaxdev01.bachoco.net:50000/RESTAdapter/portal-cosecha";

	public PedidoCompraJdbcRepositoryAdapter(PedidoCompraJdbcRepository pedidoCompraJdbcRepository,
			PedidoCompraSapWebClientAdapter pedidoCompraSapWebClientAdapter, JdbcTemplate jdbcTemplate,
			NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.pedidoCompraJdbcRepository = pedidoCompraJdbcRepository;
		this.pedidoCompraSapWebClientAdapter = pedidoCompraSapWebClientAdapter;
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<PedidoCompraDTO> findByFilterSiloAndMaterialAnFecha(String claveSilo, Integer materialId, String fechaInicio,
			String fechaFin) {
		List<PedidoCompraDTO> response=new ArrayList<>();
		List<PedidoSapResponseDTO> pedidosSap=this.pedidoCompraSapWebClientAdapter.findAllPedidoCompra(claveSilo,toReplace(fechaInicio),toReplace(fechaFin),urlPedCompra);
		List<PedidoCompraDTO> resultadoPedidoCompra=new ArrayList<>();
		if(pedidosSap.size()>0) {
			List<String> folioPedCompraPosicion=pedidosSap.stream()
					.map(p->buildFolioPedCompraAnPosicion(p.getPedCompra(),p.getPosicion()))
					.toList();
			//se obtiene por lotes
			List<List<String>> folioLotes=pedidoCompraByNumPed(folioPedCompraPosicion);
			//se busca registros de los folios
			//resultadoPedidoCompra=findAllPedidoCompras(folioLotes);
			resultadoPedidoCompra.addAll(findAllPedidoCompras(folioLotes));
			if(resultadoPedidoCompra.size()>0) {
				Set<String> folioPedidoCompra = new HashSet<>(folioPedCompraPosicion);
				List<PedidoCompraDTO> foliosNoExisten=resultadoPedidoCompra.stream()
						.filter(f->!folioPedidoCompra.contains(f.getFolioPedCompraPosicion())).toList();
				
				List<String> foliosBdNoReg=resultadoPedidoCompra.stream()
						.map(p->p.getFolioPedCompraPosicion())
						.toList();
				Set<String> folios = new HashSet<>(foliosBdNoReg);
				
				List<PedidoSapResponseDTO> pedCompraPorRegistrar=pedidosSap.stream().
						filter(p->!folios.contains(buildFolioPedCompraAnPosicion(p.getPedCompra(),p.getPosicion()))).toList();
		
				if(pedCompraPorRegistrar.size()>0) {
					//this.pedidoCompraJdbcRepository.savePedidoCompra(pedCompraPorRegistrar);
					List<String> searchFolios=pedCompraPorRegistrar.stream()
							.map(p->buildFolioPedCompraAnPosicion(p.getPedCompra(),p.getPosicion()))
							.toList();
					List<List<String>> loteSaveSearch=pedidoCompraByNumPed(searchFolios);
					resultadoPedidoCompra.addAll(findAllPedidoCompras(loteSaveSearch));
				}
			}else {
				List<List<String>> folioSearch=pedidoCompraByNumPed(folioPedCompraPosicion);
				resultadoPedidoCompra.addAll(findAllPedidoCompras(folioSearch));
			}
		}
		return resultadoPedidoCompra;
	}
	
	@Override
	public List<PedidoCompraDTO> findAllComprasSapByFilters(String claveSilo, String claveMaterial, String fechaInicio,
			String fechaFin) {
		//st<PedidoSapResponseDTO> pedidos=this.pedidoCompraSapWebClientAdapter.findAll(claveSilo, fechaInicio, fechaFin);
		List<PedidoSapResponseDTO> pedidos=new ArrayList<>();
		if(pedidos.size()>0) {
			List<String> folioPedCompraPosicion=pedidos.stream()
					.map(p->buildFolioPedCompraAnPosicion(p.getPedCompra(),p.getPosicion())).toList();
			List<List<String>> resultFolios=pedidoCompraByNumPed(folioPedCompraPosicion);
			return null;
		}
		return null;
	}

	private List<PedidoCompraDTO> findAllPedidoCompras(List<List<String>> lote) {
		List<PedidoCompraDTO> result= new ArrayList<>();
		for (List<String> batch : lote) {
			result.addAll(this.pedidoCompraJdbcRepository.findAllByFolioNumCompra(batch));
		}
		return result;
	}
	
	private List<List<String>> pedidoCompraByNumPed(List<String> folioNumPedCompraPosicion){
		List<List<String>> lotes=BatchUtils.partition(folioNumPedCompraPosicion,100);
		return lotes;
	}
	private String toReplace(String value) {
		return value.replace("-", "");
	}
	
	private String buildFolioPedCompraAnPosicion(String pedCompra,String posicion) {
		return pedCompra.concat("-").concat(posicion);
	}
}
