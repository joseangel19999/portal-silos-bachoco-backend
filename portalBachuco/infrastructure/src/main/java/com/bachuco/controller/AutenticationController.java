package com.bachuco.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bachuco.dto.AutenticationResponse;
import com.bachuco.dto.LoginRequest;
import com.bachuco.dto.OtpRequest;
import com.bachuco.persistence.adapter.AuthenticationAdapter;

@RestController
@RequestMapping("/v1/auth")
public class AutenticationController {

	// private final AuthenticationUseCase authenticationUseCase;
	/*
	 * public AutenticationController(AuthenticationUseCase authenticationUseCase) {
	 * this.authenticationUseCase = authenticationUseCase; }
	 */
	private final AuthenticationAdapter authenticationAdapter;

	public AutenticationController(AuthenticationAdapter authenticationAdapter) {
		this.authenticationAdapter = authenticationAdapter;
	}

	@PostMapping("/login")
	public ResponseEntity<AutenticationResponse> login(@RequestBody LoginRequest request) {
		String token = this.authenticationAdapter.authenticate(request.username(), request.password());
		AutenticationResponse response = new AutenticationResponse(token, request.username());
		return new ResponseEntity<AutenticationResponse>(response, HttpStatus.OK);
	}

	@PostMapping("/verity-otp")
	public ResponseEntity<AutenticationResponse> validateOtpAndGenerateToken(@RequestBody OtpRequest otpRequest){
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 String username="";
		 if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
	            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	            username = userDetails.getUsername();
	            // List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
	    }
		String token=this.authenticationAdapter.verifyOtpAnGenerateToken(username, otpRequest.otp());
		AutenticationResponse response= new AutenticationResponse(token,username);
		return new ResponseEntity<AutenticationResponse>(response,HttpStatus.OK);
	}

	@PostMapping("/createPassword")
	public ResponseEntity<?> createPassword(@RequestBody LoginRequest request) {
		return new ResponseEntity<String>("", HttpStatus.OK);
	}
}
