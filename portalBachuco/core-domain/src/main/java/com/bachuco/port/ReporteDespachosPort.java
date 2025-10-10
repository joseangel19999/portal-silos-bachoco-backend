package com.bachuco.port;

import java.util.List;

import com.bachuco.model.ReporteDespacho;

public interface ReporteDespachosPort {

	public List<ReporteDespacho> findAllReportDespachos(Integer siloId,String fechaI,String fechaF);
}
