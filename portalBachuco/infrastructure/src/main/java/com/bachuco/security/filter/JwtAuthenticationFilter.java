package com.bachuco.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.bachuco.security.CustomUserDetailsService;
import com.bachuco.security.JwtSecurityService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private String AUTHORIZATION_="Authorization";
	private String BEARER="Bearer ";
	private String pathAuth="/v1/auth/login";
	
	 private final JwtSecurityService jwtSecurityService;
	 private final CustomUserDetailsService userDetailsService;
	 
	public JwtAuthenticationFilter(JwtSecurityService jwtSecurityService, CustomUserDetailsService userDetailsService) {
		this.jwtSecurityService = jwtSecurityService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String pathUrl=request.getServletPath();
		if (pathUrl.contains(pathAuth)) {//no va a validar el token en el login
			filterChain.doFilter(request, response);
			return;
		}
		String authHeader = request.getHeader(AUTHORIZATION_);
		String jwt;
		String claveUser;
		List<Claims> perfiles= new ArrayList<>();
		if (authHeader == null || !authHeader.startsWith(BEARER)) {
			filterChain.doFilter(request, response);
			return;
		}
		jwt=authHeader.substring(BEARER.length());
		try {
			claveUser = jwtSecurityService.getUsernameFromToken(jwt);
			Claims claim=jwtSecurityService.extractAllClaims(jwt);
			if (claveUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(claveUser);
				if (jwtSecurityService.isTokenValid(jwt, userDetails)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null, userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}else {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                response.getWriter().write("El JWT ha expirado.");
				}
			}
		}catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("El JWT ha expirado.");
		}
		filterChain.doFilter(request, response);
	}

}
