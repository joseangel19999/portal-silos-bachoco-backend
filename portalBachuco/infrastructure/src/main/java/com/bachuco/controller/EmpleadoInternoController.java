package com.bachuco.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bachuco.dto.EmpleadoInternoRequestDto;
import com.bachuco.model.Empleado;
import com.bachuco.model.EmpleadoInterno;
import com.bachuco.model.Usuario;
import com.bachuco.service.usecase.EmpleadoUseCase;
import com.bachuco.service.usecase.UsuarioUseCase;

@RestController
@RequestMapping("/v1/empleadoInterno")
public class EmpleadoInternoController {

	private final EmpleadoUseCase empleadoUseCase;
	private final UsuarioUseCase usuarioUseCase;

	public EmpleadoInternoController(EmpleadoUseCase empleadoUseCase, UsuarioUseCase usuarioUseCase) {
		this.empleadoUseCase = empleadoUseCase;
		this.usuarioUseCase = usuarioUseCase;
	}

	@PostMapping
	public ResponseEntity<Empleado> save(@RequestBody EmpleadoInternoRequestDto request){
		Usuario usuario= new Usuario();
		usuario.setUsuario(request.getUsuario());
		Empleado empleado= new Empleado();
		empleado.setCorreo(request.getCorreo());
		empleado.setNombre(request.getNombre());
		Usuario newusuario=this.usuarioUseCase.save(usuario);
		empleado.setUsuario(newusuario);
		this.empleadoUseCase.asinadPerfilAUsuario(newusuario.getId(),request.getPerfilId());
		Empleado newEmpleado=this.empleadoUseCase.crearEmpleadoConUsuario(empleado);
		this.empleadoUseCase.asignarDepartamentoConPuestoInterno(newEmpleado.getId(),request.getDepartamentoId(),request.getPuestoId());
		return new ResponseEntity<Empleado>(empleado,HttpStatus.OK);
		
	}
	

	@GetMapping
	public ResponseEntity<List<EmpleadoInterno>> findAll(){
		List<EmpleadoInterno> empleados= this.empleadoUseCase.findAll();
		return new ResponseEntity<List<EmpleadoInterno>>(empleados,HttpStatus.OK);
	}
	
}
