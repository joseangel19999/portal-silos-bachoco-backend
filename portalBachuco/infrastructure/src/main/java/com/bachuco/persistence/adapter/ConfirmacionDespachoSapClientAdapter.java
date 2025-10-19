package com.bachuco.persistence.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.bachuco.port.ConfirmacionDespachoSapPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConfirmacionDespachoSapClientAdapter implements ConfirmacionDespachoSapPort {

	private final String username="CONS_LATBC01";
	private final String password="Snorlax25";
	private final String pathConfDespacho="http://bopaxdev01.bachoco.net:50000/RESTAdapter/portal-cosecha/confirmacion-despacho";
	
	public ConfirmacionDespachoSapClientAdapter() {
	}

	@Override
	public String sendConfirmacionDespacho(String claveSilo, String claveMaterial,String claveNumPedTraslado ,String claveMovimiento,
			String numBoleta, String pesoNeto, String destino,String ruta) {
	
		HttpURLConnection conn=null;
        try {
        	URL url = new URL(pathConfDespacho);
        	conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);
            addBasicAuth(conn);
            String requestJson = jsonRequest(claveSilo,claveMaterial,claveNumPedTraslado,claveMovimiento,
        			 numBoleta,pesoNeto,destino);
            // Enviar cuerpo JSON
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestJson.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            return readResponse(conn);
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    return null;
		} finally {
            conn.disconnect();
        }
	}
	
	private String jsonRequest(String claveSilo, String claveMaterial,String claveNumPedTraslado, String claveMovimiento,
			String numBoleta, String pesoNeto, String destino) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("I_SILO",claveSilo);
		requestMap.put("I_REFNODOC", "PORTAL TUKY");
		requestMap.put("I_HEADERTXT", "PRUEBA-COSECHAS TUKY");
		requestMap.put("I_MATERIAL",claveMaterial);
		requestMap.put("I_PONUMBER",claveNumPedTraslado);
		requestMap.put("I_CANTIDAD", pesoNeto);
		requestMap.put("I_MOVIMIENTO",claveMovimiento);
		return mapper.writeValueAsString(requestMap);
	}
	
	 private void addBasicAuth(HttpURLConnection conn) {
	        String encodedAuth = Base64.getEncoder()
	                .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));
	        conn.setRequestProperty("Authorization", "Basic " + encodedAuth);
	    }
	    // ======================
	    // MÉTODO AUXILIAR: Lectura respuesta
	    // ======================
	    private String readResponse(HttpURLConnection conn) throws IOException {
	        int status = conn.getResponseCode();
	        InputStream inputStream = (status >= 200 && status < 300)
	                ? conn.getInputStream()
	                : conn.getErrorStream();

	        StringBuilder response = new StringBuilder();
	        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
	            String line;
	            while ((line = br.readLine()) != null) {
	                response.append(line.trim());
	            }
	        }
	        if (status < 200 || status >= 300) {
	            throw new IOException("Error HTTP " + status + ": " + response);
	        }
	        return response.toString();
	    }

}
