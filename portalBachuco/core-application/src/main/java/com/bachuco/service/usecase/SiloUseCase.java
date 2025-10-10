package com.bachuco.service.usecase;

import java.util.List;
import java.util.Optional;

import com.bachuco.exception.NotFoundException;
import com.bachuco.model.Silo;
import com.bachuco.port.SiloRepositoryPort;

public class SiloUseCase {

	private final SiloRepositoryPort siloRepository;
	
	public SiloUseCase(SiloRepositoryPort siloRepository) {
		this.siloRepository = siloRepository;
	}

	public Silo save(Silo silo) {
		return this.siloRepository.save(silo).get();
	}
	
	public Silo findById(Integer id){
		return this.siloRepository.findById(id).get();
	}
	
	public Silo update(Silo silo,Integer id) {
		silo.setId(id);
		return this.siloRepository.update(silo).get();
	}
	public List<Silo> findAll(){
		return siloRepository.findAll();
	}
	
	public void delete(Integer id) {
		this.siloRepository.delete(id);
	};
	
	public Float findStockById(Integer siloId) {
		Optional<Float> stockOpt=this.siloRepository.stock(siloId);
		if(stockOpt.isPresent()) {
			return stockOpt.get();
		}
		throw new NotFoundException("No hay stock");
	}
}
