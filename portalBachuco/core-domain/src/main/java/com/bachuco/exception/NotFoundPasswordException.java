package com.bachuco.exception;

public class NotFoundPasswordException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotFoundPasswordException(String mensaje) {
		super(mensaje);
	}
}
