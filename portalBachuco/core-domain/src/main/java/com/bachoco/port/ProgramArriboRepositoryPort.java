package com.bachoco.port;

import java.util.List;

import com.bachoco.model.ProgramArriboRequest;

public interface ProgramArriboRepositoryPort {

	public Double stockSilo(String claveSilo);
	public String saveProgramArrivo(List<ProgramArriboRequest> arribos);
}
