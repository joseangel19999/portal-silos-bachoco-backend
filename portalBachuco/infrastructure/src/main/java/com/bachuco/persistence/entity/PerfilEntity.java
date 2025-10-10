package com.bachuco.persistence.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TC_PERFIL")
public class PerfilEntity {

	@Id
	@Column(name = "PERFIL_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "PERFIL_CLAVE")
	private String clave;
	@Column(name = "PERFIL_DESCRIPCION")
	private String descripcion;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tc_perfilpermiso", joinColumns = @JoinColumn(name = "PERMISO_ID"), inverseJoinColumns = @JoinColumn(name = "PERFIL_ID"))
	private Set<PermisoEntity> permisos = new HashSet<>();

}
