package com.bachoco.dto;

import java.util.List;

public class PedidoCompraSapClientWrapper {

	private List<PedidoSapResponseClientDTO> Items;

	 public List<PedidoSapResponseClientDTO> getItems() {
		 return Items;
	 }

	 public void setItems(List<PedidoSapResponseClientDTO> items) {
		 Items = items;
	 }
}
