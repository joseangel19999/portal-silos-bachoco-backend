package com.bachuco.port;


public interface AuthenticationPort {

	String authenticate(String username,String password);
	String verifyOtpAnGenerateToken(String username,String otp);
}
