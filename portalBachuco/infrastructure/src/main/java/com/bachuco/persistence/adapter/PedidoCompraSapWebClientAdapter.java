package com.bachuco.persistence.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.bachuco.dto.PedidoCompraSapClientWrapper;
import com.bachuco.dto.pedTraslado.dto.SapPedidoTrasladoResponse;
import com.bachuco.mapper.PedidoSapClientMapper;
import com.bachuco.mapper.PedidoTrasladoSapClientMapper;
import com.bachuco.model.PedidoSapResponseDTO;
import com.bachuco.model.PedidoTrasladoSapResponseDTO;
import com.bachuco.persistence.config.HttpUrlConnectionClient;
import com.bachuco.port.PedidoCompraSapPort;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class PedidoCompraSapWebClientAdapter implements PedidoCompraSapPort {

	private final String username="CONS_LATBC01";
	private final String password="Snorlax25";
	
	public PedidoCompraSapWebClientAdapter() {
	}

	@Override
	public List<PedidoSapResponseDTO> findAllPedidoCompra(String claveSilo, String claveMaterial,String fechaInicio, String fechaFin,String rutaUrl) {
		List<PedidoSapResponseDTO> response= new ArrayList<>();
		HttpUrlConnectionClient client=new HttpUrlConnectionClient(rutaUrl,
				username,password);
		 String endpoint = String.format("/consulta-pedido-compra?Silo=%s&FechaIni=%s&FechaFin=%s&Material=%s",claveSilo,fechaInicio,fechaFin,Integer.parseInt(claveMaterial));
		 try {
			String jsonResponse = client.get(endpoint);
			ObjectMapper objectMapper= new ObjectMapper();
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			PedidoCompraSapClientWrapper wrapper= objectMapper.readValue(jsonResponse, PedidoCompraSapClientWrapper.class);
			if(wrapper!=null && wrapper.getItems().size()>0) {
				 return wrapper.getItems().stream().map(p->PedidoSapClientMapper.toDomain(p)).toList();
			}
			return response;
		}
		 catch (InvalidFormatException e) {
			 e.printStackTrace();
			return response;
		}catch (IOException e) {
			e.printStackTrace();
			return response;
		}
	}

	/*@Override
	public List<PedidoTrasladoSapResponseDTO> findAllPedTraslado(String claveSilo,String claveMaterial, String fechaInicio, String fechaFin,
			String rutaUrl) {
		List<PedidoTrasladoSapResponseDTO> response= new ArrayList<>();
		HttpUrlConnectionClient client=new HttpUrlConnectionClient(rutaUrl,
				username,password);
		//consulta-pedido-compra?Silo=CP12&FechaIni=20250901&FechaFin=20250930&Material=100011&Traslado=X
		 String endpoint = String.format("/consulta-pedido-compra?Silo=%s&FechaIni=%s&FechaFin=%s&Traslado=%s&MATERIAL=%s", claveSilo,fechaInicio,fechaFin,Integer.parseInt(claveMaterial),"X");
		 try {
			String jsonResponse = client.get("http://bopaxdev01.bachoco.net:50000/RESTAdapter/portal-cosecha/consulta-pedido-compra?Silo=CP12&FechaIni=20250901&FechaFin=20250930&Material=100011&Traslado=X");
			ObjectMapper objectMapper= new ObjectMapper();
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			SapPedidoTrasladoResponse responseSap= objectMapper.readValue(jsonResponse, SapPedidoTrasladoResponse.class);
			return responseSap.getItems().stream().map(p->PedidoTrasladoSapClientMapper.toDomain(p)).toList();
		} catch (InvalidFormatException e) {
			return response;
		}catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}*/
}
