package com.bachuco.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.bachuco.model.Departamento;
import com.bachuco.model.Empleado;
import com.bachuco.model.EmpleadoInterno;
import com.bachuco.model.EmpleadoInternoRequest;
import com.bachuco.model.EmpleadoInternoResponse;
import com.bachuco.model.Puesto;
import com.bachuco.model.Usuario;
import com.bachuco.persistence.entity.DepartamentoEntity;
import com.bachuco.persistence.entity.EmpleadoInternoEntity;
import com.bachuco.persistence.entity.PuestoEntity;
import com.bachuco.persistence.repository.DepartamentoJpaRepository;
import com.bachuco.persistence.repository.EmpleadoInternoJdbcRepository;
import com.bachuco.persistence.repository.EmpleadoInternoJpaRepository;
import com.bachuco.persistence.repository.EmpleadoJpaRepository;
import com.bachuco.persistence.repository.PuestoJpaRepository;
import com.bachuco.port.EmpleadoInternoRepositoryPort;

@Repository
public class EmpleadoInternoJpaRepositoryAdapter implements EmpleadoInternoRepositoryPort {
	
	private final DepartamentoJpaRepository departamentoRepository;
	private final PuestoJpaRepository puestoJpaRepository;
	private final EmpleadoJpaRepository empleadoJpaRepository;
	private final EmpleadoInternoJpaRepository empleadoInternoJpaRepository;
	private final EmpleadoInternoJdbcRepository empInternoJdbcRepository;

	public EmpleadoInternoJpaRepositoryAdapter(DepartamentoJpaRepository departamentoRepository,
			PuestoJpaRepository puestoJpaRepository, EmpleadoJpaRepository empleadoJpaRepository,
			EmpleadoInternoJpaRepository empleadoInternoJpaRepository,
			EmpleadoInternoJdbcRepository empInternoJdbcRepository) {
		this.departamentoRepository = departamentoRepository;
		this.puestoJpaRepository = puestoJpaRepository;
		this.empleadoJpaRepository = empleadoJpaRepository;
		this.empleadoInternoJpaRepository = empleadoInternoJpaRepository;
		this.empInternoJdbcRepository = empInternoJdbcRepository;
	}

	@Override
	public Optional<EmpleadoInterno> save(Integer empleadoId, Integer departamentoId, Integer puestoId) {
		try {
			var empleado=this.empleadoJpaRepository.findById(empleadoId);
			DepartamentoEntity depto=this.departamentoRepository.findById(departamentoId).get();
			PuestoEntity puesto=this.puestoJpaRepository.findById(puestoId).get();
			EmpleadoInternoEntity empInterno=new EmpleadoInternoEntity();
			empInterno.setEmpleado(empleado.get());
			empInterno.setDepartamento(depto);
			empInterno.setPuesto(puesto);
			EmpleadoInternoEntity newEmpInterno=this.empleadoInternoJpaRepository.save(empInterno);
			return Optional.ofNullable(toDomain(newEmpInterno));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
		
	}
	
	private EmpleadoInterno toDomain(EmpleadoInternoEntity entity) {
		Usuario user=new Usuario();
		user.setId(entity.getEmpleado().getUsuario().getId());
		user.setUsuario(entity.getEmpleado().getUsuario().getUsuario());
		user.setActivo(entity.getEmpleado().getUsuario().getEstatus());
		
		Empleado emp= new Empleado();
		emp.setId(entity.getEmpleado().getId());
		emp.setNombre(entity.getEmpleado().getNombre());
		emp.setUsuario(user);
		emp.setCorreo(entity.getEmpleado().getCorreo());
		
		Departamento depto= new Departamento(entity.getDepartamento().getId(),entity.getDepartamento().getNombre());
		Puesto puesto= new Puesto(entity.getPuesto().getId(),entity.getPuesto().getDescripcion());
		EmpleadoInterno interno=new EmpleadoInterno();
		interno.setEmpleado(emp);
		interno.setDepartamento(depto);
		interno.setPuesto(puesto);
		return interno;
	}
	
	@Override
	public List<EmpleadoInternoResponse> findAll(){
		return this.empInternoJdbcRepository.findAll();
	}

	@Override
	public void save(EmpleadoInternoRequest request) throws Exception {
		this.empInternoJdbcRepository.save(request);
	}
	
	@Override
	public void delete(Integer id) {
		this.empInternoJdbcRepository.delete(id);
	}

	@Override
	public void update(Integer id,EmpleadoInternoRequest request) {
		this.empInternoJdbcRepository.update(id,request);
	}
}
