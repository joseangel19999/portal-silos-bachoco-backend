package com.bachoco.dto.pedTraslado.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SapPedidoTrasladoResponse {

	@JsonProperty("Items")
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private List<SapItem> items;

	@JsonProperty("STOCKSILO")
	private String stockSilo;

	public String getStockSilo() {
		return stockSilo;
	}

	public void setStockSilo(String stockSilo) {
		this.stockSilo = stockSilo;
	}

	public List<SapItem> getItems() {
		return items;
	}

	public void setItems(List<SapItem> items) {
		this.items = items;
	}
}
