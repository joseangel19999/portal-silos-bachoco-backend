package com.bachuco.service.usecase;

import java.util.List;
import java.util.Optional;

import com.bachuco.exception.PersistenceException;
import com.bachuco.exception.RegistroNoCreadoException;
import com.bachuco.exception.UsuarioDuplicadoException;
import com.bachuco.model.Empleado;
import com.bachuco.model.EmpleadoExternoRequest;
import com.bachuco.model.EmpleadoInterno;
import com.bachuco.model.EmpleadoInternoRequest;
import com.bachuco.model.EmpleadoInternoResponse;
import com.bachuco.model.EmpleadoResponse;
import com.bachuco.port.EmpleadoInternoRepositoryPort;
import com.bachuco.port.EmpleadoRepositoryPort;
import com.bachuco.port.PerfilRepositoryPort;
import com.bachuco.port.UsuarioRepositoryPort;

public class EmpleadoUseCase {

	private final UsuarioRepositoryPort usuarioRepositoryPort;
	private final EmpleadoRepositoryPort empleadoRepositoryPort;
	private final EmpleadoInternoRepositoryPort empleadoInternoRepositoryPort;
	private final PerfilRepositoryPort perfilRepositoryPort;

	public EmpleadoUseCase(UsuarioRepositoryPort usuarioRepositoryPort, EmpleadoRepositoryPort empleadoRepositoryPort,
			EmpleadoInternoRepositoryPort empleadoInternoRepositoryPort,PerfilRepositoryPort perfilRepositoryPort) {
		this.usuarioRepositoryPort = usuarioRepositoryPort;
		this.empleadoRepositoryPort = empleadoRepositoryPort;
		this.empleadoInternoRepositoryPort = empleadoInternoRepositoryPort;
		this.perfilRepositoryPort=perfilRepositoryPort;
	}

	public Empleado crearEmpleadoConUsuario(Empleado empleado) {
		Empleado emp = new Empleado();
		var resultEmpelado = this.empleadoRepositoryPort.findByCorreo(empleado.getCorreo());
		if (resultEmpelado.isPresent() && resultEmpelado.get().getId() != null) {
			throw new UsuarioDuplicadoException("El correo ya existe");
		}
		empleado.setUsuario(empleado.getUsuario());
		emp = this.empleadoRepositoryPort.save(empleado);
		return emp;
	}

	public void update(Integer id,EmpleadoInternoRequest req) {
		this.empleadoInternoRepositoryPort.update(id, req);
	}
	public EmpleadoInterno asignarDepartamentoConPuestoInterno(Integer empleadoId, Integer departamentoId,
			Integer puestoId) {
		// Departamento
		// depto=this.departamentoRepositoryPort.findById(internoCommand.departamentoId());
		// Puesto puesto =
		// this.puestoRepositoryPort.findById(internoCommand.departamentoId());
		// Empleado emp=
		// this.empleadoRepositoryPort.findById(internoCommand.empleadoId());
		return this.empleadoInternoRepositoryPort.save(empleadoId, departamentoId, puestoId).get();
	}

	public void asinadPerfilAUsuario(Integer usuarioId,Integer perfilId) {
		try{
			this.usuarioRepositoryPort.addUsuatioToPerfil(usuarioId, perfilId);
		}catch (Exception e) {
			throw new PersistenceException("No se pudo asignar el perfil al usuario");
		}
	}
	
	public List<EmpleadoInterno> findAll(){
		return this.empleadoRepositoryPort.findAll();
	}
	
	public List<EmpleadoInternoResponse> findAllEmpleadoResponse(){
		return this.empleadoInternoRepositoryPort.findAll();
	}
	
	public void saveEmpleadoInterno(EmpleadoInternoRequest req) {
		var resultEmpelado = this.empleadoRepositoryPort.findByCorreo(req.getCorreo());
		if (resultEmpelado.isPresent() && resultEmpelado.get().getId() != null) {
			throw new UsuarioDuplicadoException("El correo ya existe");
		}
		try {
			this.empleadoInternoRepositoryPort.save(req);
		}catch (Exception e) {
			e.printStackTrace();
			throw new RegistroNoCreadoException("Hubo error al crear el registro");
		}

	}
	
	public Optional<EmpleadoResponse> findByUsuarioOrCorreo(String value){
		return this.empleadoRepositoryPort.findByClaveUsuarioOrCorreo(value);
	}
	
	public void delete(Integer id) {
		this.empleadoInternoRepositoryPort.delete(id);
	}
}
