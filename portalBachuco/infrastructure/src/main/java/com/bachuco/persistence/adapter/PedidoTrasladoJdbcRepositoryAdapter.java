package com.bachuco.persistence.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.bachuco.dto.FolioResponseDTO;
import com.bachuco.model.PedidoSapResponseDTO;
import com.bachuco.model.procedores.PedidoCompraDTO;
import com.bachuco.model.procedores.PedidoTrasladoArriboDTO;
import com.bachuco.model.procedores.PedidoTrasladoDTO;
import com.bachuco.persistence.repository.PedidoTrasladoJdbcRepository;
import com.bachuco.port.PedidoTrasladoJdbcRepositoryPort;

@Repository
public class PedidoTrasladoJdbcRepositoryAdapter implements PedidoTrasladoJdbcRepositoryPort {

	private final PedidoTrasladoJdbcRepository pedidoTrasladoJdbcRepository;
	private final PedidoCompraSapWebClientAdapter pedidoCompraSapWebClientAdapter;
	private final String urlPedCompra="http://bopaxdev01.bachoco.net:50000/RESTAdapter/portal-cosecha";

	public PedidoTrasladoJdbcRepositoryAdapter(PedidoTrasladoJdbcRepository pedidoTrasladoJdbcRepository,
			PedidoCompraSapWebClientAdapter pedidoCompraSapWebClientAdapter) {
		this.pedidoTrasladoJdbcRepository = pedidoTrasladoJdbcRepository;
		this.pedidoCompraSapWebClientAdapter = pedidoCompraSapWebClientAdapter;
	}

	@Override
	public List<PedidoTrasladoDTO> findByFilterSiloAndMaterialAnFecha(String claveSilo,String claveMaterial,
			String fechaInicio, String fechaFin) {
		//obtenerPedidosFiltradosPoSiloYMaterial
		List<PedidoTrasladoDTO> response=new ArrayList<>();
		try {
			List<PedidoSapResponseDTO> pedidosSap=this.pedidoCompraSapWebClientAdapter.findAllPedidoCompra(claveSilo,toReplace(fechaInicio),toReplace(fechaFin),urlPedCompra);
			if(pedidosSap.size()>0) {
				List<FolioResponseDTO> numeroPedidos=this.pedidoTrasladoJdbcRepository.findAllNumeroCompraByFilterProgram(claveSilo, claveMaterial);
				//List<PedidoTrasladoDTO> registrados=this.pedidoTrasladoJdbcRepository.obtenerPedidosFiltrados(claveSilo,claveMaterial,fechaInicio,fechaFin);
				Set<String> foliosPedResponse = new HashSet<>(numeroPedidos.stream().map(p->p.getNumeroPedido().concat("-").concat(p.getPosicion())).toList());
				//Set<String> foliosPedtraslado = new HashSet<>(pedidosSap.stream().map(p->p.getPedCompra().concat("-").concat(p.getPosicion())).toList());
				/*List<PedidoTrasladoDTO> pedCompraResultado=registrados.stream().
						filter(r->!foliosPedtraslado.contains(folioPedTraslado(r.getNumPedidoTraslado(),r.getPosicion()))).toList();*/
				
				List<PedidoSapResponseDTO> pedCompraResultado=pedidosSap.stream().
						filter(r->!foliosPedResponse.contains(folioPedTraslado(r.getPedCompra(),r.getPosicion()))).toList();
				
				List<PedidoSapResponseDTO> resultPedidosRelacionados=pedCompraResultado.stream().
						filter(r->r.getPedidoRelacionado()!=null).toList();
				if(resultPedidosRelacionados.size()>0){
					
					List<PedidoSapResponseDTO> altas= new ArrayList<>();
					pedidosSap.get(0).setPedidoRelacionado("4500226663");
					altas.add(pedidosSap.get(0));
					try {
						this.pedidoTrasladoJdbcRepository.savePedidoTraslado(resultPedidosRelacionados);
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			response= pedidoTrasladoJdbcRepository.obtenerPedidosFiltrados(claveSilo, claveMaterial,fechaInicio,fechaInicio);
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<PedidoTrasladoArriboDTO> findByFilterProgramArribo(Integer siloId, Integer materialId, String planta) {
		return this.pedidoTrasladoJdbcRepository.findByFilterProgramArribo(siloId, materialId);
	}
	
	public String folioPedTraslado(String numPed,String posicion) {
		return numPed+"-"+posicion;
	}
	
	private String toReplace(String value) {
		return value.replace("-", "");
	}
}
