package com.bachuco.service.usecase;

import java.util.List;
import java.util.Optional;

import com.bachuco.exception.NotFoundException;
import com.bachuco.model.ApiResponse;
import com.bachuco.model.Silo;
import com.bachuco.port.SiloRepositoryPort;

public class SiloUseCase {

	private final SiloRepositoryPort siloRepository;
	
	public SiloUseCase(SiloRepositoryPort siloRepository) {
		this.siloRepository = siloRepository;
	}

	public ApiResponse<Silo> save(Silo silo) {
		ApiResponse<Silo> response= new ApiResponse<>();
		Optional<Silo> opt=this.siloRepository.findByClave(silo.getSilo());
		if(opt.isPresent() && opt.get().getSilo().equalsIgnoreCase(silo.getSilo())) {
			response.setCode("BUSI-S1");
			response.setMessage("Ya existe el silo");
			return response;
		}
		Silo result=this.siloRepository.save(silo).get();
		response.setCode("0");
		response.setData(result);
		return response;
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
