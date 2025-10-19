package com.bachuco.persistence.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.bachuco.exception.RegistroNoCreadoException;
import com.bachuco.model.PedidoTrasladoSapResponseDTO;
import com.bachuco.model.procedores.PedidoTrasladoArriboDTO;
import com.bachuco.model.procedores.PedidoTrasladoDTO;
import com.bachuco.persistence.repository.PedidoTrasladoJdbcRepository;
import com.bachuco.port.PedidoTrasladoJdbcRepositoryPort;
import com.bachuco.secutiry.utils.BatchUtils;

@Repository
public class PedidoTrasladoJdbcRepositoryAdapter implements PedidoTrasladoJdbcRepositoryPort {

	private final PedidoTrasladoJdbcRepository pedidoTrasladoJdbcRepository;
	private final PedidoTrasladoSapWebClientoAdapter pedidoTrasladoSapWebClientoAdapter;
	private final String urlPedCompra="http://bopaxdev01.bachoco.net:50000/RESTAdapter/portal-cosecha";

	public PedidoTrasladoJdbcRepositoryAdapter(PedidoTrasladoJdbcRepository pedidoTrasladoJdbcRepository,
			PedidoTrasladoSapWebClientoAdapter pedidoTrasladoSapWebClientoAdapter) {
		this.pedidoTrasladoJdbcRepository = pedidoTrasladoJdbcRepository;
		this.pedidoTrasladoSapWebClientoAdapter = pedidoTrasladoSapWebClientoAdapter;
	}

	@Override
	public List<PedidoTrasladoDTO> findByFilterSiloAndMaterialAnFecha(String claveSilo,String claveMaterial,
			String fechaInicio, String fechaFin) {
		List<PedidoTrasladoDTO> response=new ArrayList<>();
		List<String> foliosExists=new ArrayList<>();
		try {
			List<PedidoTrasladoSapResponseDTO> pedidosSap=this.pedidoTrasladoSapWebClientoAdapter.findAllPedTraslado(claveSilo,claveMaterial,toReplace(fechaInicio),toReplace(fechaFin),urlPedCompra);
			if(pedidosSap.size()>0) {
				List<String> folios=pedidosSap.stream().map(s->s.getNumeroPedTraslado().trim().concat("-").concat(s.getPosicion().trim())).toList();
				List<List<String>> batches = BatchUtils.partition(folios, 100);
				for (List<String> batch : batches) {
					foliosExists.addAll(this.pedidoTrasladoJdbcRepository.findAllFoliosPedTrasladoExist(batch));
				}
				Set<String> foliosExist = new HashSet<>(foliosExists);
				List<PedidoTrasladoSapResponseDTO> pedidosSapNoExistBd=pedidosSap.stream().filter(p->!foliosExist.contains(folio(p.getNumeroPedTraslado(),p.getPosicion()))).toList();
				List<String> foliosNoExistSap=pedidosSapNoExistBd.stream().map(p->buildFolioPedCompraAnPosicion(p.getNumeroPedTraslado(),p.getPosicion())).toList();
				if(pedidosSapNoExistBd.size()>0){
					this.pedidoTrasladoJdbcRepository.savePedidoTraslado(pedidosSapNoExistBd);
					foliosExists.addAll(foliosNoExistSap);
					List<List<String>> batchesFolios = BatchUtils.partition(foliosExists, 100);
					for (List<String> batch : batchesFolios) {
						response.addAll(this.pedidoTrasladoJdbcRepository.findAllByFolioNumCompra(batch));
					}
					return response;
				}else {
					for (List<String> batch : batches) {
						response.addAll(this.pedidoTrasladoJdbcRepository.findAllByFolioNumCompra(batch));
					}
				}
			}
			//response= pedidoTrasladoJdbcRepository.obtenerPedidosFiltrados(claveSilo, claveMaterial,fechaInicio,fechaInicio);
		}catch (Exception e) {
			e.printStackTrace();
			throw  new RegistroNoCreadoException(e.getMessage());
		}
		return response;
	}
	
	private String folio(String numPedTraslado,String posicion) {
		String folio=numPedTraslado.trim().concat("-").concat(posicion.trim());
		return folio;
	}

	@Override
	public List<PedidoTrasladoArriboDTO> findByFilterProgramArribo(Integer siloId, String planta,Integer materialId) {
		return this.pedidoTrasladoJdbcRepository.obtenerPedidosTrasladoParaArribo(siloId,planta, materialId);
	}
	
	@Override
	public List<PedidoTrasladoArriboDTO> findByPedTrasladoByConfDespacho(Integer siloId, String planta,
			Integer materialId) {
		return this.pedidoTrasladoJdbcRepository.findByPedidosTrasladoParaConfDespacho(siloId, materialId);
	}
	@Override
	public List<PedidoTrasladoDTO> findByFiltersCantidadDisponible(Integer siloId,Integer materialId,
			String fechaInicio, String fechaFin) {
		return pedidoTrasladoJdbcRepository.obtenerPedidosFiltradosCantidadDisponible(siloId,materialId,fechaInicio,fechaFin);
	}
	public String folioPedTraslado(String numPed,String posicion) {
		return numPed+"-"+posicion;
	}
	private String toReplace(String value) {
		return value.replace("-", "");
	}
	private String buildFolioPedCompraAnPosicion(String pedCompra,String posicion) {
		return pedCompra.trim().concat("-").concat(posicion.trim());
	}
}
