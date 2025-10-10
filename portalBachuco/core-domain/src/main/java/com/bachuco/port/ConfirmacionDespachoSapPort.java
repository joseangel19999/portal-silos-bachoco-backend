package com.bachuco.port;

public interface ConfirmacionDespachoSapPort {

	public String sendConfirmacionDespacho(String claveSilo,Integer claveMaterial,
			String claveMovimiento,String numBoleta,String pesoNeto,String destino,String ruta);
}
