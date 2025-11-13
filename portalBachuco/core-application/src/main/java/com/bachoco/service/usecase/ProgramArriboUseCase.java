package com.bachoco.service.usecase;

import java.util.List;

import com.bachoco.model.ProgramArriboRequest;
import com.bachoco.port.ProgramArriboRepositoryPort;

public class ProgramArriboUseCase {

	private final ProgramArriboRepositoryPort programArriboRepositoryPort;

	public ProgramArriboUseCase(ProgramArriboRepositoryPort programArriboRepositoryPort) {
		this.programArriboRepositoryPort = programArriboRepositoryPort;
	}
	
	public Double stockSilo(String claveSilo,String material) {
		return this.programArriboRepositoryPort.stockSilo(claveSilo,material);
	}
	
	public String saveProgramArribo(List<ProgramArriboRequest> req) {
		return this.programArriboRepositoryPort.saveProgramArrivo(req);
	}
}
