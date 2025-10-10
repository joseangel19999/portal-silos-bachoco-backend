package com.bachuco.persistence.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.bachuco.dto.ReporteProgramArriboDTO;
import com.bachuco.mapper.rowMapper.ReporteProgramArriboRowMapper;

@Repository
public class ReporteProgramArriboRepository {

	private final JdbcTemplate jdbcTemplate;
	private final ReporteProgramArriboRowMapper reporteProgramArriboRowMapper;
	
	public ReporteProgramArriboRepository(JdbcTemplate jdbcTemplate,
			ReporteProgramArriboRowMapper reporteProgramArriboRowMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.reporteProgramArriboRowMapper = reporteProgramArriboRowMapper;
	}
	
	public List<ReporteProgramArriboDTO> obtenerPedidosFiltrados(Integer siloId, String fechaInicio,String fechaFin) {
		String sql = "{call ObtenerReporteProgramArriboFiltrados(?, ?, ?)}";
		if(fechaInicio.equals("-1")) {
			fechaInicio=null;
		}
		if(fechaFin.equals("-1")) {
			fechaFin=null;
		}
		return jdbcTemplate.query(sql, reporteProgramArriboRowMapper, siloId, fechaInicio, fechaFin);
	}
}
