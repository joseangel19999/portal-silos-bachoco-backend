package com.bachuco.model.procedores;

public class PedidoTrasladoArriboDTO {
	
	private String numeroPedido;
	private Float cantidad;
	public PedidoTrasladoArriboDTO(String numeroPedido, Float cantidad) {
		super();
		this.numeroPedido = numeroPedido;
		this.cantidad = cantidad;
	}
	public String getNumeroPedido() {
		return numeroPedido;
	}
	public void setNumeroPedido(String numeroPedido) {
		this.numeroPedido = numeroPedido;
	}
	public Float getCantidad() {
		return cantidad;
	}
	public void setCantidad(Float cantidad) {
		this.cantidad = cantidad;
	}
	
	

}
