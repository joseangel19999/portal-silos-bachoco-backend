package com.bachuco.model;

public class ConfirmacionDespachoRequest {

	private Integer bodegaId;
	private String claveSilo;
	private String claveMaterial;
	private String fechaEmbarque;
	private String numBoleta;
	private Float pesoBruto;
	private Float pesoTara;
	private String humedad;
	private String chofer;
	private String placaJaula;
	private String lineaTransportista;
	private Integer destinoId;
	private Integer numPedidoTraslado;
	private String tipoMovimiento;
	
	public ConfirmacionDespachoRequest() {
	}

	public Integer getBodegaId() {
		return bodegaId;
	}

	public void setBodegaId(Integer bodegaId) {
		this.bodegaId = bodegaId;
	}

	public String getClaveSilo() {
		return claveSilo;
	}

	public void setClaveSilo(String claveSilo) {
		this.claveSilo = claveSilo;
	}

	public String getClaveMaterial() {
		return claveMaterial;
	}

	public void setClaveMaterial(String claveMaterial) {
		this.claveMaterial = claveMaterial;
	}

	public String getFechaEmbarque() {
		return fechaEmbarque;
	}

	public void setFechaEmbarque(String fechaEmbarque) {
		this.fechaEmbarque = fechaEmbarque;
	}

	public String getNumBoleta() {
		return numBoleta;
	}

	public void setNumBoleta(String numBoleta) {
		this.numBoleta = numBoleta;
	}

	public Float getPesoBruto() {
		return pesoBruto;
	}

	public void setPesoBruto(Float pesoBruto) {
		this.pesoBruto = pesoBruto;
	}

	public Float getPesoTara() {
		return pesoTara;
	}

	public void setPesoTara(Float pesoTara) {
		this.pesoTara = pesoTara;
	}

	public String getHumedad() {
		return humedad;
	}

	public void setHumedad(String humedad) {
		this.humedad = humedad;
	}

	public String getChofer() {
		return chofer;
	}

	public void setChofer(String chofer) {
		this.chofer = chofer;
	}

	public String getPlacaJaula() {
		return placaJaula;
	}

	public void setPlacaJaula(String placaJaula) {
		this.placaJaula = placaJaula;
	}

	public String getLineaTransportista() {
		return lineaTransportista;
	}

	public void setLineaTransportista(String lineaTransportista) {
		this.lineaTransportista = lineaTransportista;
	}

	public Integer getDestinoId() {
		return destinoId;
	}

	public void setDestinoId(Integer destinoId) {
		this.destinoId = destinoId;
	}

	public Integer getNumPedidoTraslado() {
		return numPedidoTraslado;
	}

	public void setNumPedidoTraslado(Integer numPedidoTraslado) {
		this.numPedidoTraslado = numPedidoTraslado;
	}

	public String getTipoMovimiento() {
		return tipoMovimiento;
	}

	public void setTipoMovimiento(String tipoMovimiento) {
		this.tipoMovimiento = tipoMovimiento;
	}

}
