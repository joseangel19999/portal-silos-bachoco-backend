package com.bachuco.model;

public class EmpleadoResponse {
	private Integer empleadoId;
	private Integer departamentoId;
	private Integer puestoid;
	private String nombrePuesto;
	private String nombre;
	private Integer siloId;
	
	public EmpleadoResponse() {
	}

	public EmpleadoResponse(Integer empleadoId, Integer departamentoId, Integer puestoid, Integer siloId) {
		this.empleadoId = empleadoId;
		this.departamentoId = departamentoId;
		this.puestoid = puestoid;
		this.siloId = siloId;
	}

	public Integer getEmpleadoId() {
		return empleadoId;
	}

	public void setEmpleadoId(Integer empleadoId) {
		this.empleadoId = empleadoId;
	}

	public Integer getDepartamentoId() {
		return departamentoId;
	}

	public void setDepartamentoId(Integer departamentoId) {
		this.departamentoId = departamentoId;
	}

	public Integer getPuestoid() {
		return puestoid;
	}

	public void setPuestoid(Integer puestoid) {
		this.puestoid = puestoid;
	}

	public Integer getSiloId() {
		return siloId;
	}

	public void setSiloId(Integer siloId) {
		this.siloId = siloId;
	}

	public String getNombrePuesto() {
		return nombrePuesto;
	}

	public void setNombrePuesto(String nombrePuesto) {
		this.nombrePuesto = nombrePuesto;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
