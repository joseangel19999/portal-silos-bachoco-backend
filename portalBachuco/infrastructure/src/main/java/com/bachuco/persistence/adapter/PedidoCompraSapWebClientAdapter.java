package com.bachuco.persistence.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.bachuco.dto.PedidoCompraSapClientWrapper;
import com.bachuco.mapper.PedidoSapClientMapper;
import com.bachuco.model.PedidoSapResponseDTO;
import com.bachuco.persistence.config.HttpUrlConnectionClient;
import com.bachuco.port.PedidoCompraSapPort;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PedidoCompraSapWebClientAdapter implements PedidoCompraSapPort {

	private final RestClient restClient;
	private final String username="CONS_LATBC01";
	private final String password="Snorlax25";
	
	public PedidoCompraSapWebClientAdapter(RestClient restClient) {
		this.restClient = restClient;
	}

	@Override
	public List<PedidoSapResponseDTO> findAllPedidoCompra(String claveSilo, String fechaInicio, String fechaFin,String rutaUrl) {
		List<PedidoSapResponseDTO> response= new ArrayList<>();
		HttpUrlConnectionClient client=new HttpUrlConnectionClient(rutaUrl,
				username,password);
		 String endpoint = String.format("/consulta-pedido-compra?Silo=%s&FechaIni=%s&FechaFin=%s", claveSilo,"20240912","20250922");
		 try {
			String jsonResponse = client.get(endpoint);
			ObjectMapper objectMapper= new ObjectMapper();
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			PedidoCompraSapClientWrapper wrapper= objectMapper.readValue(jsonResponse, PedidoCompraSapClientWrapper.class);
			 return wrapper.getItems().stream().map(p->PedidoSapClientMapper.toDomain(p)).toList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	@Override
	public List<PedidoSapResponseDTO> findAllPedTraslado(String claveSilo, String fechaInicio, String fechaFin,
			String rutaUrl) {
		List<PedidoSapResponseDTO> response= new ArrayList<>();
		HttpUrlConnectionClient client=new HttpUrlConnectionClient(rutaUrl,
				username,password);
		 String endpoint = String.format("/consulta-pedido-compra?Silo=%s&FechaIni=%s&FechaFin=%s&Traslado=%s", claveSilo,"20240912","20250922","X");
		 try {
			String jsonResponse = client.get(endpoint);
			ObjectMapper objectMapper= new ObjectMapper();
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			PedidoCompraSapClientWrapper wrapper= objectMapper.readValue(jsonResponse, PedidoCompraSapClientWrapper.class);
			 return wrapper.getItems().stream().map(p->PedidoSapClientMapper.toDomain(p)).toList();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}
}
