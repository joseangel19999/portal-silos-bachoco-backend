package com.bachuco.persistence.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import com.bachuco.model.EmpleadoResponse;

@Component
public class CatalogJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private final DataSource dataSource;

	public CatalogJdbcRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
			DataSource dataSource) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.dataSource = dataSource;
	}

	public Optional<EmpleadoResponse> findByUsuarioOrCorreo(String value) {
		String sql = """
				SELECT
				 e.EMPLEADO_ID,
				                i.TC_DEPARTAMENTO_ID,
				                i.TC_PUESTO_ID,
				    ex.TC_SILO_ID
				FROM tc_empleado e
				LEFT JOIN tc_usuario u on e.TC_USUARIO_ID=u.USUARIO_ID
				LEFT JOIN tc_empleado_externo ex on e.EMPLEADO_ID=e.EMPLEADO_ID
				            LEFT JOIN tc_empleado_interno i on e.EMPLEADO_ID=i.TC_EMPLEADO_ID
				where e.CORREO=? or u.USUARIO=?
				LIMIT 1;
				""";
		try {
			EmpleadoResponse result = jdbcTemplate.queryForObject(sql, new Object[] { value, value },
					new RowMapper<EmpleadoResponse>() {
						@Override
						public EmpleadoResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
							EmpleadoResponse p = new EmpleadoResponse();
							p.setEmpleadoId(rs.getInt("EMPLEADO_ID"));
							p.setDepartamentoId(rs.getInt("TC_DEPARTAMENTO_ID"));
							p.setPuestoid(rs.getInt("TC_PUESTO_ID"));
							p.setSiloId(rs.getInt("TC_SILO_ID"));
							return p;
						}
					});
			if (result != null) {
				return Optional.of(result);
			} else {
				return Optional.empty();
			}
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}
	public Integer obtenerSiloId(String siloNombre) {
		String sql = "SELECT SILO_ID " + "FROM tc_silo " + "WHERE SILO_NOMBRE = ? OR SILO_CLAVE = ? " + "LIMIT 1";
		try {
			Integer siloId = jdbcTemplate.queryForObject(sql, Integer.class, siloNombre, siloNombre);
			return siloId;
		} catch (org.springframework.dao.EmptyResultDataAccessException e) {
			System.out.println("Silo no encontrado: " + siloNombre);
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public int resetStockSilo(String cantidad, String claveSilo, Integer idSilo) {
		Integer id = obtenerSiloId(claveSilo);
		int response = -1;
		if (id != null) {
			String sql = """
					UPDATE tc_silo s
					SET s.STOCK_SILO=?
					WHERE s.SILO_ID=?
					            and s.STOCK_SILO=0
					   """;
			response = jdbcTemplate.update(sql, cantidad, id);
		}
		return response;
	}

}
