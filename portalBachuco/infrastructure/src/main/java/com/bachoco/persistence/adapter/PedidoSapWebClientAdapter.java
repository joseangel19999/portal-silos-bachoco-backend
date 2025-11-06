package com.bachoco.persistence.adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.bachoco.model.PedidoSapResponseDTO;
import com.bachoco.port.PedidoSapRepositoryPort;

@Component
public class PedidoSapWebClientAdapter implements PedidoSapRepositoryPort {

	private final PedidoCompraSapWebClientAdapter pedidoCompraSapWebClientAdapter;
	private final JdbcTemplate jdbcTemplate;
	private final RestClient restClient;
	//@Value("${app.url.endpoit.sap.username}")
	private String username;
	//@Value("${app.url.endpoit.sap.password}")
	private String password;

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
