package com.bachuco.model.procedores;

public class PedidoTrasladoArriboDTO {
	
	private Integer pedidoTrasladoId;
	private String numeroPedPosicion;
	private String numeroPedido;
	private Float cantidad;
	
	public PedidoTrasladoArriboDTO() {
	}

	public PedidoTrasladoArriboDTO(Integer pedidoTrasladoId, String numeroPedPosicion, String numeroPedido,
			Float cantidad) {
		this.pedidoTrasladoId = pedidoTrasladoId;
		this.numeroPedPosicion = numeroPedPosicion;
		this.numeroPedido = numeroPedido;
		this.cantidad = cantidad;
	}

	public Integer getPedidoTrasladoId() {
		return pedidoTrasladoId;
	}

	public void setPedidoTrasladoId(Integer pedidoTrasladoId) {
		this.pedidoTrasladoId = pedidoTrasladoId;
	}

	public String getNumeroPedPosicion() {
		return numeroPedPosicion;
	}

	public void setNumeroPedPosicion(String numeroPedPosicion) {
		this.numeroPedPosicion = numeroPedPosicion;
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
