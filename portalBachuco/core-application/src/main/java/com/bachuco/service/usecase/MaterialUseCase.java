package com.bachuco.service.usecase;

import java.util.List;
import java.util.Optional;

import com.bachuco.command.CrearMaterialCommand;
import com.bachuco.model.Categoria;
import com.bachuco.model.Material;
import com.bachuco.port.CategoriaRepositoryPort;
import com.bachuco.port.MaterialRepositoryPort;

public class MaterialUseCase {
	
	private final CategoriaRepositoryPort categoriaRepositoryPort;
	private final MaterialRepositoryPort materialRepositoryPort;

	public MaterialUseCase(MaterialRepositoryPort materialRepositoryPort,
			CategoriaRepositoryPort categoriaRepositoryPort) {
		this.materialRepositoryPort = materialRepositoryPort;
		this.categoriaRepositoryPort=categoriaRepositoryPort;
	}
	
	public void save(CrearMaterialCommand materialCommand) {
		Optional<Categoria> categoria=categoriaRepositoryPort.findById(materialCommand.getCategoriaId());
		Material material= new Material();
		material.setNumero(materialCommand.getNumero());
		material.setDescripcion(materialCommand.getDescripcion());
		material.setCategoria(categoria.get());
		this.materialRepositoryPort.save(material);
	}
	
	public void update(Material material) {
		Optional<Categoria> categoria = categoriaRepositoryPort.findById(material.getCategoria().getId());
		if (categoria.isPresent()) {
			Optional<Material> resultado = this.materialRepositoryPort.findById(material.getMaterialId());
			if (resultado.isPresent()) {
				Material update = resultado.get();
				update.setNumero(material.getNumero());
				update.setDescripcion(material.getDescripcion());
				update.setCategoria(categoria.get());
				this.materialRepositoryPort.update(material);
			}
		}
	}
	
	public Optional<Material> findById(Integer id){
		return this.materialRepositoryPort.findById(id);
	}
	
	public List<Material> findAll(){
		return this.materialRepositoryPort.findAll();
	}
	
	public void delete(Integer id) {
		this.materialRepositoryPort.delete(id);
	}

}
