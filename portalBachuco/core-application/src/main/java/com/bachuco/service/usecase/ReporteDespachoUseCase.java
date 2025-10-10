package com.bachuco.service.usecase;

import java.util.List;

import com.bachuco.model.ReporteDespacho;
import com.bachuco.port.ReporteDespachosPort;

public class ReporteDespachoUseCase {
	
	private final ReporteDespachosPort despachosPort;

	public ReporteDespachoUseCase(ReporteDespachosPort despachosPort) {
		this.despachosPort = despachosPort;
	}
	
	public List<ReporteDespacho> findAll(Integer siloId,String fechaI,String fechaF){
		return this.despachosPort.findAllReportDespachos(siloId, fechaI, fechaF);
	}

}
