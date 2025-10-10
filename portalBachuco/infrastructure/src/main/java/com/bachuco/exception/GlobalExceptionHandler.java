package com.bachuco.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bachuco.dto.ApiError;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UsuarioDuplicadoException.class)
	public ResponseEntity<ApiError> handleUsuarioNoEncontrado(UsuarioDuplicadoException ex,
			HttpServletRequest request) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "error-code:1001",  ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}

	@ExceptionHandler(CredencialesInvalidasException.class)
	public ResponseEntity<ApiError> handleCredencialesNovalidas(CredencialesInvalidasException ex,
			HttpServletRequest request) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "Error: ", ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	
	
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(NotFoundException ex,
			HttpServletRequest request) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "-", ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(NotFoundPasswordException.class)
	public ResponseEntity<ApiError> handleValorNoEncontrado(NotFoundPasswordException ex,
			HttpServletRequest request) {
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "no-existe-password", ex.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiError> handleAuthException(AuthenticationException ex,
			HttpServletRequest request) {
		String messageError="";
		String errorCode="AUTH_ERROR";
		if(ex instanceof BadCredentialsException) {
			messageError="Credenciales Invalidas";
		}
		if(ex instanceof UsernameNotFoundException) {
			messageError="Credenciales Invalidas";
		}
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "error-code:"+errorCode, messageError,
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(IOException.class)
	public ResponseEntity<ApiError> handleIoException(IOException io,
			HttpServletRequest request){
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "error-code:DOC-001", io.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleException(Exception io,
			HttpServletRequest request){
		ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "error-code:EX-001", io.getMessage(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
	}
}
