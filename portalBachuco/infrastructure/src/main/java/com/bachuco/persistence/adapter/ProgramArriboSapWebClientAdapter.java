package com.bachuco.persistence.adapter;

import java.io.IOException;
import org.springframework.stereotype.Component;

import com.bachuco.dto.PedidoCompraSapClientWrapper;
import com.bachuco.dto.sap.programArribo.StockSiloResponse;
import com.bachuco.persistence.config.HttpUrlConnectionClient;
import com.bachuco.port.ProgramArriboSapRepositoryPort;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@Component
public class ProgramArriboSapWebClientAdapter implements ProgramArriboSapRepositoryPort {

	private final String username="CONS_LATBC01";
	private final String password="Snorlax25";

	@Override
	public Double stockSilo(String claveSilo, String rutaUrl) {
		// TODO Auto-generated method stub
		HttpUrlConnectionClient client=new HttpUrlConnectionClient(rutaUrl,
				username,password);
		 String endpoint = String.format("/consulta-pedido-compra?Silo=%s&Param1=%s",claveSilo,"X");
		 try {
				String jsonResponse = client.get(endpoint);
				ObjectMapper objectMapper= new ObjectMapper();
				objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
				StockSiloResponse response= objectMapper.readValue(jsonResponse, StockSiloResponse.class);
				if(response.getStockSilo()!=null) {
					return Double.parseDouble(response.getStockSilo());
				}
				return null;
			}
			 catch (InvalidFormatException e) {
				return null;
			}catch (IOException e) {
				e.printStackTrace();
				return null;
			}
	}

}
