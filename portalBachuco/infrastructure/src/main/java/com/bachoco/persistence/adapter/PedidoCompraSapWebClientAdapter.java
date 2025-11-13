package com.bachoco.persistence.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bachoco.dto.PedidoCompraSapClientWrapper;
import com.bachoco.exception.SapConnectionException;
import com.bachoco.mapper.PedidoSapClientMapper;
import com.bachoco.model.PedidoSapResponseDTO;
import com.bachoco.persistence.config.HttpUrlConnectionClient;
import com.bachoco.persistence.config.SapProperties;
import com.bachoco.port.PedidoCompraSapPort;
import com.bachoco.utils.WebClientUtils;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class PedidoCompraSapWebClientAdapter implements PedidoCompraSapPort {

	private final SapProperties sapProperties;
	@Value("${app.url.endpoit.sap.filter.silo}")
	private int filtersiloSilo;
	private static final Logger logger = LoggerFactory.getLogger(PedidoCompraSapWebClientAdapter.class);

	public PedidoCompraSapWebClientAdapter(SapProperties sapProperties) {
		this.sapProperties = sapProperties;
	}

	@Override
	public List<PedidoSapResponseDTO> findAllPedidoCompra(String claveSilo, String claveMaterial,String fechaInicio, String fechaFin,String rutaUrl) {
		List<PedidoSapResponseDTO> response= new ArrayList<>();
		String endpoint;
		logger.info("===============PEDIDO COMPRA====================== ");
		logger.info("URL::: "+rutaUrl);
		HttpUrlConnectionClient client=new HttpUrlConnectionClient(rutaUrl,
				sapProperties.getUserName(),sapProperties.getPassWord());
		if(filtersiloSilo==1) {
			endpoint=WebClientUtils.buildUrlPedidoCompra(claveSilo, "", "", "", false); 
		}else {
			endpoint=WebClientUtils.buildUrlPedidoCompra(claveSilo, fechaInicio, fechaFin,Integer.valueOf(claveMaterial), true); 
		}
		try {
			String jsonResponse = client.get(endpoint);
			int endIndex = Math.min(jsonResponse.length(), 15);
		    String prefix = jsonResponse.substring(0, endIndex).trim();
		    if (!prefix.startsWith("{") && !prefix.startsWith("[")) {
		    	 logger.warn("Respuesta de SAP vacía (posiblemente sin datos para los filtros).");
			     return response;
		    }
			ObjectMapper objectMapper= new ObjectMapper();
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			PedidoCompraSapClientWrapper wrapper= objectMapper.readValue(jsonResponse, PedidoCompraSapClientWrapper.class);
			if(wrapper!=null && wrapper.getItems().size()>0) {
				 return wrapper.getItems().stream().map(p->PedidoSapClientMapper.toDomain(p)).toList();
			}
			return response;
		}catch (IOException e) {
			e.printStackTrace();
			logger.error("Hubo un error de conexion a sap de pedido compra: "+e.getMessage());
			throw new SapConnectionException("Hubo error en conexion a SAP: "+e.getCause());
		}
	}
}
