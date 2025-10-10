package com.bachuco.persistence.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bachuco.dto.ReporteProgramArriboDTO;
import com.bachuco.mapper.rowMapper.ReporteDespachoRowMapper;
import com.bachuco.model.ReporteDespacho;

@Repository
public class ReporteDespachoJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private final ReporteDespachoRowMapper reporteDespachoRowMapper;
	
	public ReporteDespachoJdbcRepository(JdbcTemplate jdbcTemplate, ReporteDespachoRowMapper reporteDespachoRowMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.reporteDespachoRowMapper = reporteDespachoRowMapper;
	}
	
	
	public List<ReporteDespacho> obtenerPedidosFiltrados(Integer siloId, String fechaInicio,String fechaFin) {
		String sql = "{call ObtenerReporteDespachosFiltrados(?, ?, ?)}";
		if(fechaInicio.equals("-1")) {
			fechaInicio=null;
		}
		if(fechaFin.equals("-1")) {
			fechaFin=null;
		}
		return jdbcTemplate.query(sql, reporteDespachoRowMapper, siloId, fechaInicio, fechaFin);
	}
	
	
}
