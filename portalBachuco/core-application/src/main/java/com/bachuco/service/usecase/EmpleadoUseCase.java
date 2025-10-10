package com.bachuco.service.usecase;

import java.util.List;

import com.bachuco.exception.PersistenceException;
import com.bachuco.exception.UsuarioDuplicadoException;
import com.bachuco.model.Empleado;
import com.bachuco.model.EmpleadoInterno;
import com.bachuco.model.Usuario;
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
}
