package com.bachuco.service.usecase;

import java.util.List;
import java.util.Optional;

import com.bachuco.model.Planta;
import com.bachuco.port.PlantaRepositoryPort;

public class PlantaUseCase {

	private final PlantaRepositoryPort plantaRepositoryPort;

	public PlantaUseCase(PlantaRepositoryPort plantaRepositoryPort) {
		this.plantaRepositoryPort = plantaRepositoryPort;
	}
	
	public Planta save(Planta planta) {
		return this.plantaRepositoryPort.save(planta).get();
	}
	
	public Planta update(Planta planta) {
		return this.plantaRepositoryPort.update(planta).get();
	}
	
	public void delete(Integer id) {
		this.plantaRepositoryPort.delete(id);
	}
	
	public Optional<Planta> findById(Integer id){
		return this.plantaRepositoryPort.findBydId(id);
	}
	
	public List<Planta> findAll(){
		return this.plantaRepositoryPort.findAll();
	}
}
