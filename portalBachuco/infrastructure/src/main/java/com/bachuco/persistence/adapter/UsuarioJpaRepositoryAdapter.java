package com.bachuco.persistence.adapter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bachuco.model.Perfil;
import com.bachuco.model.Usuario;
import com.bachuco.persistence.entity.PerfilEntity;
import com.bachuco.persistence.entity.UsuarioEntity;
import com.bachuco.persistence.repository.PerfilJpaRepository;
import com.bachuco.persistence.repository.UsuarioJpaRepository;
import com.bachuco.port.UsuarioRepositoryPort;

@Repository
public class UsuarioJpaRepositoryAdapter implements UsuarioRepositoryPort {

	private final UsuarioJpaRepository usuarioJpaRepository;
	private final PasswordEncoder passwordEncoder;
	private final PerfilJpaRepository perfilJpaRepository;


	public UsuarioJpaRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository, PasswordEncoder passwordEncoder,
			PerfilJpaRepository perfilJpaRepository) {
		this.usuarioJpaRepository = usuarioJpaRepository;
		this.passwordEncoder = passwordEncoder;
		this.perfilJpaRepository = perfilJpaRepository;
	}

	@Override
	public Usuario save(Usuario usuario) {
		return toDomain(this.usuarioJpaRepository.save(toEntity(usuario)));
	}

	@Override
	public Usuario update(Usuario usuario) {
		Usuario usuarioResponse=new Usuario();
		try {
			var entity=this.usuarioJpaRepository.findByUsuario(usuario.getUsuario());
			if(entity.isPresent() && entity.get().getId()!=null) {
				UsuarioEntity userEntity=entity.get();
				userEntity.setUsuario(usuario.getUsuario());
				usuarioResponse=toDomain(userEntity);
			}
		}catch (NoSuchElementException e1) {
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}	
		return usuarioResponse;
	}

	@Override
	public List<Usuario> findAllByUsuario(String usuario) {
			List<UsuarioEntity> entity=this.usuarioJpaRepository.findAll();
			if(entity!=null) {
				return entity.stream().map(i->toDomain(i)).collect(Collectors.toList());
			}
			return Collections.emptyList();
	
	}

	@Override
	public Optional<Usuario> findByUsuario(String usuario) {
		try {
			var entity=this.usuarioJpaRepository.findByUsuario(usuario);
			if(entity.isPresent()) {
				return Optional.ofNullable(toDomain(entity.get()));
			}
		}catch (NoSuchElementException e1) {
			return Optional.empty();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}	
		return Optional.empty();
	}
	
	@Override
	public void updatePassword(String username, String password) {
		try {
			var entity=this.usuarioJpaRepository.findByUsuario(username);
			if(entity.isPresent() && (entity.get().getPassword()==null || entity.get().getPassword().trim().length()==0)) {
				UsuarioEntity userEntity=entity.get();
				userEntity.setPassword(this.passwordEncoder.encode(password));
				this.usuarioJpaRepository.save(userEntity);
			}
		}catch (NoSuchElementException e1) {
			
		}catch(Exception ex) {
			ex.printStackTrace();
		}	
	}

	@Override
	public Optional<Usuario> findByUsername(String username) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	
	@Transactional
	@Override
	public void addUsuatioToPerfil(Integer usuarioId, Integer perfilId) {
		Optional<PerfilEntity> perfil=this.perfilJpaRepository.findById(perfilId);
		if(perfil.isPresent()) {
			Optional<UsuarioEntity> usuario=this.usuarioJpaRepository.findById(usuarioId);
			if(usuario.isPresent()) {
				Set<PerfilEntity> listaPerfiles= new HashSet<>();
				listaPerfiles.add(perfil.get());
				UsuarioEntity entity=usuario.get();
				entity.setPerfiles(listaPerfiles);
				this.usuarioJpaRepository.save(entity);
			}
		}
	}
	
	private UsuarioEntity toEntity(Usuario usuario) {
		return new UsuarioEntity()
				.builder()
				.tipoUsuario(usuario.getUsuario())
				.usuario(usuario.getUsuario())
				.estatus(0)
				.tipoUsuario("1")
				.build();
	}

	private Usuario toDomain(UsuarioEntity entity) {
		Usuario usuario= new Usuario();
		Set<Perfil> listPerfiles= new HashSet<>();
		usuario.setId(entity.getId());
		if(entity.getUsuario()!=null) {
			usuario.setUsuario(entity.getUsuario());
		}
		usuario.setUsuarioTipo(entity.getTipoUsuario());
		usuario.setActivo(entity.getEstatus());
		if(entity.getPerfiles()!=null && entity.getPerfiles().size()>0) {
			entity.getPerfiles().stream().forEach(i->listPerfiles.add(toDomainPerfil(i)));
			usuario.setPerfiles(listPerfiles);
		}
		if(entity.getPassword()!=null) {
			usuario.setPassword(entity.getPassword());
		}
		return usuario;
	}
	
	private Perfil toDomainPerfil(PerfilEntity perfilEntity) {
		return new Perfil(perfilEntity.getId(),perfilEntity.getClave(),perfilEntity.getDescripcion(),new HashSet<>());
	}
	
}
