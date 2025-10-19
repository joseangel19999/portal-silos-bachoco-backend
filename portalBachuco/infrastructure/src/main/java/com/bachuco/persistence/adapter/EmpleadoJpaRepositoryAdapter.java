package com.bachuco.persistence.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.bachuco.model.Departamento;
import com.bachuco.model.Empleado;
import com.bachuco.model.EmpleadoInterno;
import com.bachuco.model.EmpleadoInternoRequest;
import com.bachuco.model.EmpleadoResponse;
import com.bachuco.model.Perfil;
import com.bachuco.model.Usuario;
import com.bachuco.persistence.entity.DepartamentoEntity;
import com.bachuco.persistence.entity.EmpleadoEntity;
import com.bachuco.persistence.entity.EmpleadoInternoEntity;
import com.bachuco.persistence.entity.PerfilEntity;
import com.bachuco.persistence.entity.UsuarioEntity;
import com.bachuco.persistence.repository.CatalogJdbcRepository;
import com.bachuco.persistence.repository.EmpleadoInternoJpaRepository;
import com.bachuco.persistence.repository.EmpleadoJpaRepository;
import com.bachuco.persistence.repository.UsuarioJpaRepository;
import com.bachuco.port.EmpleadoRepositoryPort;

@Repository
public class EmpleadoJpaRepositoryAdapter implements EmpleadoRepositoryPort{
	
	private final EmpleadoJpaRepository empleadoJpaRepository;
	private final EmpleadoInternoJpaRepository empleadoInternoJpaRepository;
	private final UsuarioJpaRepository usuarioRepository;
	private final CatalogJdbcRepository catalogJdbcRepository;



	public EmpleadoJpaRepositoryAdapter(EmpleadoJpaRepository empleadoJpaRepository,
			EmpleadoInternoJpaRepository empleadoInternoJpaRepository, UsuarioJpaRepository usuarioRepository,
			CatalogJdbcRepository catalogJdbcRepository) {
		this.empleadoJpaRepository = empleadoJpaRepository;
		this.empleadoInternoJpaRepository = empleadoInternoJpaRepository;
		this.usuarioRepository = usuarioRepository;
		this.catalogJdbcRepository = catalogJdbcRepository;
	}

	@Override
	public Empleado save(Empleado empleado) {
		Optional<UsuarioEntity> usuarioEntity= this.usuarioRepository.findById(empleado.getUsuario().getId());
		EmpleadoEntity empEntity= toEntity(empleado);
		empEntity.setUsuario(usuarioEntity.get());
		return toDomain(this.empleadoJpaRepository.save(empEntity));
	}

	@Override
	public Optional<String> getCorreo(Integer usuarioId) {
		Optional<EmpleadoEntity> emp=this.empleadoJpaRepository.findByUsuarioId(usuarioId);
		if(emp.isPresent()) {
			return Optional.ofNullable(emp.get().getCorreo());
		}
		return Optional.empty();
	}
	
	@Override
	public Empleado findById(Integer id) {
		return null;
	}

	@Override
	public Optional<Empleado> findByCorreo(String correo) {
		try {
			return Optional.ofNullable(toDomain(this.empleadoJpaRepository.findByCorreo(correo).get()));
		}catch(NoSuchElementException ex) {
			return Optional.empty();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	private EmpleadoEntity toEntity(Empleado empleado) {
		return EmpleadoEntity.builder()
				.nombre(empleado.getNombre())
				.apellidoPaterno(empleado.getApellidoPaterno())
				.apellidoMaterno(empleado.getAppelidoMaterno())
				.correo(empleado.getCorreo()).build();
	}
	
	private Empleado toDomain(EmpleadoEntity entity) {
		Empleado emp=new Empleado();
		emp.setId(entity.getId());
		emp.setNombre(entity.getNombre());
		emp.setApellidoPaterno(entity.getApellidoPaterno());
		emp.setAppelidoMaterno(entity.getApellidoMaterno());
		emp.setCorreo(entity.getCorreo());
		return emp;
	}

	@Override
	public List<EmpleadoInterno> findAll() {
		List<EmpleadoInternoEntity> empleados=this.empleadoInternoJpaRepository.findAll();
		return empleados.stream().map(p->toEmpleadoInternoDomain(p)).toList();
	}
	
	private Perfil toPerfilDomain(PerfilEntity e){
		Perfil p= new Perfil();
		p.setClave(e.getClave());
		p.setDescripcion(e.getDescripcion());
		p.setId(e.getId());
		return p;
	}
	
	private Usuario toUsuarioDomain(UsuarioEntity e) {
		Set<Perfil> perfiles= new HashSet<>();
		Usuario user= new Usuario();
		user.setUsuario(e.getUsuario());
		user.setId(e.getId());
		user.setUsuarioTipo(e.getTipoUsuario());
		e.getPerfiles().stream().forEach(per->perfiles.add(toPerfilDomain(per)));
		user.setPerfiles(perfiles);
		return user;
	}
	
	private Empleado toEmpleadoDomain(EmpleadoEntity e) {
		Empleado emp= new Empleado();
		emp.setNombre(e.getNombre());
		emp.setCorreo(e.getCorreo());
		emp.setUsuario(toUsuarioDomain(e.getUsuario()));
		return emp;
	}
	
	private EmpleadoInterno toEmpleadoInternoDomain(EmpleadoInternoEntity e) {
		EmpleadoInterno emp= new EmpleadoInterno();
		emp.setDepartamento(toDepartamentoDomain(e.getDepartamento()));
		emp.setEmpleado(toEmpleadoDomain(e.getEmpleado()));
		emp.setId(e.getId());
		return emp;
	}

	private Departamento toDepartamentoDomain(DepartamentoEntity e) {
		Departamento dep= new Departamento();
		dep.setId(e.getId());
		dep.setNombre(e.getNombre());
		return dep;
	}

	@Override
	public Optional<EmpleadoResponse> findByClaveUsuarioOrCorreo(String value) {
		return this.catalogJdbcRepository.findByUsuarioOrCorreo(value);
	}

	@Override
	public void update(Integer id, EmpleadoInternoRequest req) {
	}
}
