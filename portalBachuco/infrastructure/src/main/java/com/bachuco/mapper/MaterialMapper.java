package com.bachuco.mapper;
import java.util.List;

import com.bachuco.dto.MaterialResponseDTO;
import com.bachuco.model.Material;
import com.bachuco.persistence.entity.MaterialEntity;

public class MaterialMapper {

	public static Material toDomain(MaterialEntity entity) {
		Material material= new Material();
		material.setMaterialId(entity.getMaterialId());
		if(entity.getNumero()!=null) {
			material.setNumero(entity.getNumero());
		}
		if(entity.getDescripcion()!=null) {
			material.setDescripcion(entity.getDescripcion());
		}
		return material;
	}
	
	public static MaterialEntity toEntity(Material material) {
		return MaterialEntity.builder()
				.numero(material.getNumero())
				.descripcion(material.getDescripcion()).build();
	}
	
	public static List<Material> toDomain(List<MaterialEntity> entity) {
		return entity.stream().map(e->MaterialMapper.toDomain(e)).toList();
	}
	
	public static List<MaterialEntity> toEntity(List<Material> materiales) {
		return materiales.stream().map(e->MaterialMapper.toEntity(e)).toList();
	} 
	
	public static MaterialResponseDTO toResponse(Material material) {
		return new MaterialResponseDTO(material.getMaterialId(),material.getNumero(),material.getDescripcion(),0,"");
	}
	
	public static List<MaterialResponseDTO> toResponse(List<Material> materiales){
		return materiales.stream().map(m->MaterialMapper.toResponse(m)).toList();
	}
}
