package com.bachoco.persistence.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bachoco.mapper.rowMapper.ReporteDespachoRowMapper;
import com.bachoco.model.ReporteDespacho;

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
		List<ReporteDespacho> response= new ArrayList<>();
		if(fechaInicio.equals("-1")) {
			fechaInicio=null;
		}
		if(fechaFin.equals("-1")) {
			fechaFin=null;
		}
		try {
			return jdbcTemplate.query(sql, reporteDespachoRowMapper, siloId, fechaInicio, fechaFin);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	
}
