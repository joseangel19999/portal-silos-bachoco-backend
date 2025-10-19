package com.bachuco.dto.pedTraslado.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SapPedidoTrasladoResponse {

	@JsonProperty("Items")
	private List<SapItem> items;

	public List<SapItem> getItems() {
		return items;
	}

	public void setItems(List<SapItem> items) {
		this.items = items;
	}
}
