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

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.bachuco.port.ConfirmacionDespachoSapPort;

@Component
public class ConfirmacionDespachoSapClientAdapter implements ConfirmacionDespachoSapPort {

	private final RestClient restClient;
	private final String username="CONS_LATBC01";
	private final String password="Snorlax25";
	private final String ruta="http://bopaxdev01.bachoco.net:50000//RESTAdapter/portal-cosecha/confirmacion-despacho";
	
	public ConfirmacionDespachoSapClientAdapter(RestClient restClient) {
		this.restClient = restClient;
	}

	@Override
	public String sendConfirmacionDespacho(String claveSilo, Integer claveMaterial, String claveMovimiento,
			String numBoleta, String pesoNeto, String destino,String ruta) {
	
		HttpURLConnection conn=null;
        try {
        	URL url = new URL(ruta);
        	conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);
            addBasicAuth(conn);
            
            String requestJson = """
                    {
						"MT_ConfirmacionDespacho_Req": {
							"I_SILO": "CP12",
							"I_REFNODOC": "11",
							"I_HEADERTXT": "AA",
							"I_MATERIAL": "1212",
							"I_PONUMBER": "423434",
							"I_CANTIDAD": "11.0"
						}
					}
                    """;
            // Enviar cuerpo JSON
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestJson.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            return readResponse(conn);

        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            conn.disconnect();
        }
        return null;
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
