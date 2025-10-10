package com.bachuco.port;

public interface PasswordEncoderPort {

	public String encode(String password);
	public boolean matches(String password,String passwordEncoded);
}
