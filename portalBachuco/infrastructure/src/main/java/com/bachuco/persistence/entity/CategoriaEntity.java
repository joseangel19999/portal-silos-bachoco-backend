package com.bachuco.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="categoria")
@Table(name="TC_CATEGORIA")
public class CategoriaEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="CATEGORIA_ID")
	private Integer id;
	@Column(name="CATEGORIA_NOMBRE")
	private String nombre;
	@Column(name="CATEGORIA_DESCRIPCION")
	private String descripcion;

}
