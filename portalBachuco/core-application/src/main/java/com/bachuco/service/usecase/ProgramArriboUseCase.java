package com.bachuco.service.usecase;

import java.util.List;

import com.bachuco.model.ProgramArriboRequest;
import com.bachuco.port.ProgramArriboRepositoryPort;

public class ProgramArriboUseCase {

	private final ProgramArriboRepositoryPort programArriboRepositoryPort;

	public ProgramArriboUseCase(ProgramArriboRepositoryPort programArriboRepositoryPort) {
		this.programArriboRepositoryPort = programArriboRepositoryPort;
	}
	
	public Double stockSilo(String claveSilo) {
		return this.programArriboRepositoryPort.stockSilo(claveSilo);
	}
	
	public String saveProgramArribo(List<ProgramArriboRequest> req) {
		return this.programArriboRepositoryPort.saveProgramArrivo(req);
	}
}
