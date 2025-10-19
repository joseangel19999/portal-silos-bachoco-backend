package com.bachuco.port;

import java.util.List;

import com.bachuco.model.ProgramArriboRequest;

public interface ProgramArriboRepositoryPort {

	public Double stockSilo(String claveSilo);
	public String saveProgramArrivo(List<ProgramArriboRequest> arribos);
}
