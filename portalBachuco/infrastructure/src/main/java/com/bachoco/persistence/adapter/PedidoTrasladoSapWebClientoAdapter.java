package com.bachoco.persistence.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bachoco.dto.pedTraslado.dto.SapPedidoTrasladoResponse;
import com.bachoco.exception.SapConnectionException;
import com.bachoco.mapper.PedidoSapClientMapper;
import com.bachoco.mapper.PedidoTrasladoSapClientMapper;
import com.bachoco.model.PedidoTrasladoSapResponseDTO;
import com.bachoco.persistence.config.HttpUrlConnectionClient;
import com.bachoco.persistence.config.SapProperties;
import com.bachoco.port.PedidoTrasladoSapPort;
import com.bachoco.utils.WebClientUtils;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class PedidoTrasladoSapWebClientoAdapter implements PedidoTrasladoSapPort {

	private final SapProperties sapProperties;
	@Value("${app.url.endpoit.sap.filter.silo}")
	private int filtersiloSilo;
	private static final Logger logger = LoggerFactory.getLogger(PedidoTrasladoSapWebClientoAdapter.class);

	public PedidoTrasladoSapWebClientoAdapter(SapProperties sapProperties) {
		this.sapProperties = sapProperties;
	}

	@Override
	public List<PedidoTrasladoSapResponseDTO> findAllPedTraslado(String claveSilo, String claveMaterial,
			String plantaDestino, String fechaInicio, String fechaFin, String rutaUrl) {
		List<PedidoTrasladoSapResponseDTO> response = new ArrayList<>();
		String endpoint;
		logger.info("===============PEDIDO TRASLADO====================== ");
		logger.info("URL::: " + rutaUrl);
		HttpUrlConnectionClient client = new HttpUrlConnectionClient(rutaUrl, sapProperties.getUserName(),
				sapProperties.getPassWord());
		if (filtersiloSilo == 1) {
			endpoint = WebClientUtils.buildUrlPedioTraslado(claveSilo, "", "", "", true);
		} else {
			endpoint = WebClientUtils.buildUrlPedioTraslado(claveSilo, fechaInicio, fechaFin,
					Integer.valueOf(claveMaterial), plantaDestino, true);
		}
		try {
			String jsonResponse = client.get(endpoint);
			int endIndex = Math.min(jsonResponse.length(), 15);
			String prefix = jsonResponse.substring(0, endIndex).trim();
			if (!prefix.startsWith("{") && !prefix.startsWith("[")) {
				logger.warn("Respuesta de SAP vacía (posiblemente sin datos para los filtros).");
				return response;
			}
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			SapPedidoTrasladoResponse responseSap = objectMapper.readValue(jsonResponse,
					SapPedidoTrasladoResponse.class);
			if (responseSap != null && responseSap.getItems() != null && !responseSap.getItems().isEmpty()) {
				return responseSap.getItems().stream().map(p -> PedidoTrasladoSapClientMapper.toDomain(p)).toList();
			} else {
				logger.warn("La respuesta de SAP no contiene items o Items es null");
				return response;
			}
		} catch (InvalidFormatException e) {
			logger.error("Error en la conexion de SAP pedido traslado: " + e.getMessage());
			throw new SapConnectionException("Hubo error en conexion a SAP: " + e.getCause());
		} catch (IOException e) {
			logger.error("Error en la conexion de SAP pedido traslado: " + e.getMessage());
			throw new SapConnectionException("Hubo error en conexion a SAP: " + e.getCause());
		}
	}

}
