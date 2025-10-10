package com.bachuco.port;

import java.util.List;
import java.util.Optional;

import com.bachuco.model.Empleado;
import com.bachuco.model.EmpleadoInterno;

public interface EmpleadoRepositoryPort {

	public Empleado save(Empleado empleado);
	public Empleado update(Empleado empleado);
	public Empleado findById(Integer id);
	public Optional<Empleado> findByCorreo(String correo);
	public Optional<String> getCorreo(Integer usuarioId);
	public List<EmpleadoInterno> findAll();
}
