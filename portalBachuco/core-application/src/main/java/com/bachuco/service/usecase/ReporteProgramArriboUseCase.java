package com.bachuco.service.usecase;

import java.util.List;

import com.bachuco.model.ReportePorgramArribo;
import com.bachuco.port.ReporteProgramArriboPort;

public class ReporteProgramArriboUseCase {
	
	private final ReporteProgramArriboPort programArriboPort;

	public ReporteProgramArriboUseCase(ReporteProgramArriboPort programArriboPort) {
		this.programArriboPort = programArriboPort;
	}
	
	public List<ReportePorgramArribo> findAll(Integer siloId,String fechaI,String fechaF){
		return this.programArriboPort.findAllFilters(siloId, fechaI, fechaF);
	}

}
