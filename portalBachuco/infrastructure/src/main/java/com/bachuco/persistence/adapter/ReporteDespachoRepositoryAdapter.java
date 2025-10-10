package com.bachuco.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.bachuco.model.ReporteDespacho;
import com.bachuco.persistence.repository.ReporteDespachoJdbcRepository;
import com.bachuco.port.ReporteDespachosPort;

@Repository
public class ReporteDespachoRepositoryAdapter implements ReporteDespachosPort {
 
	private final ReporteDespachoJdbcRepository reporteDespachoJdbcRepository;
	
	public ReporteDespachoRepositoryAdapter(ReporteDespachoJdbcRepository reporteDespachoJdbcRepository) {
		this.reporteDespachoJdbcRepository = reporteDespachoJdbcRepository;
	}

	@Override
	public List<ReporteDespacho> findAllReportDespachos(Integer siloId, String fechaI, String fechaF) {
		return reporteDespachoJdbcRepository.obtenerPedidosFiltrados(siloId, fechaI, fechaF);
	}

}
