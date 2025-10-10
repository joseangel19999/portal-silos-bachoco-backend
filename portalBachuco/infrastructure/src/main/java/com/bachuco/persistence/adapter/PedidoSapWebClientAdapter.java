package com.bachuco.persistence.adapter;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.bachuco.model.PedidoSapResponseDTO;
import com.bachuco.port.PedidoSapRepositoryPort;

@Component
public class PedidoSapWebClientAdapter implements PedidoSapRepositoryPort {

	private final PedidoCompraSapWebClientAdapter pedidoCompraSapWebClientAdapter;
	private final JdbcTemplate jdbcTemplate;
	private final RestClient restClient;
	
	private final String endpointPedidoCompra="http://bopaxdev01.bachoco.net:50000/RESTAdapter/portal-cosecha";
	private final String endpointPedidoTraslado="http://bopaxdev01.bachoco.net:50000/RESTAdapter/portal-cosecha";
	private final String username="CONS_LATBC01";
	private final String password="Snorlax25";
	

	public PedidoSapWebClientAdapter(PedidoCompraSapWebClientAdapter pedidoCompraSapWebClientAdapter,
			JdbcTemplate jdbcTemplate, RestClient restClient) {
		this.pedidoCompraSapWebClientAdapter = pedidoCompraSapWebClientAdapter;
		this.jdbcTemplate = jdbcTemplate;
		this.restClient = restClient;
	}

	@Override
	public List<PedidoSapResponseDTO> findAllPedidoCompra(String claveSilo, String claveMaterial, String fechaInicio,
			String fechaFin) {
		//List<PedidoSapResponseDTO> pedidosSap=this.pedidoCompraSapWebClientAdapter.findAll(claveSilo, fechaInicio.replace("-", ""), fechaFin.replace("-", ""));
		return null;
	}

	@Override
	public List<PedidoSapResponseDTO> findAllPedidoTraslado(String claveSilo, String claveMaterial, String fechaInicio,
			String fechaFin) {
		// TODO Auto-generated method stub
		return null;
	}



}
