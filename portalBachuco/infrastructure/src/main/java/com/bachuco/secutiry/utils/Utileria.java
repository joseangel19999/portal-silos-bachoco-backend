package com.bachuco.secutiry.utils;

public class Utileria {

	private static String buildFolioNumPosicion(String pedCompra,String posicion) {
		return pedCompra.trim().concat("-").concat(posicion.trim());
	}
}
