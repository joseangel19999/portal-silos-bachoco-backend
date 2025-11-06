package com.bachoco.persistence.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.bachoco.model.AuthenticationResponse;
import com.bachoco.model.EmpleadoResponse;

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

	public int sumaCantidadStockSilo(Float cantidad,String claveSilo,Integer siloId) {
		String sql = """
				UPDATE tc_silo s
					SET s.STOCK_SILO=s.STOCK_SILO+?
				WHERE s.SILO_CLAVE=?
			    """;
		return jdbcTemplate.update(sql,cantidad,claveSilo);
}
	
	public int restaCantidadStockSilo(Float cantidad,String claveSilo,Integer siloId) {
		String sql = """
				UPDATE tc_silo s
					SET s.STOCK_SILO=s.STOCK_SILO+?
				WHERE s.SILO_CLAVE=?
			    """;
			return jdbcTemplate.update(sql,cantidad,claveSilo);
	}
	
	public Optional<AuthenticationResponse> authResponse(String usuario) {
		String sql = """
				SELECT 
					u.USUARIO_ID,
					CASE WHEN e.TIPO_EMPLEADO=2 THEN e.CORREO ELSE u.USUARIO END AS USUARIO,
					u.USUARIO_PASSWORD,e.CORREO,
					u.USUARIO_TIPO,
					e.TIPO_EMPLEADO,
					u.ULTIMA_MOD_PWD
				FROM tc_empleado e
				JOIN tc_usuario u ON e.TC_USUARIO_ID=u.USUARIO_ID
				LEFT JOIN tc_empleado_interno ei ON e.EMPLEADO_ID=ei.TC_EMPLEADO_ID
				LEFT JOIN tc_empleado_externo ex ON e.EMPLEADO_ID=ex.TC_EMPLEADO_ID
				where u.USUARIO=? or e.CORREO=?
				LIMIT 1
				""";
		try {
			AuthenticationResponse result = jdbcTemplate.queryForObject(sql, new Object[] { usuario,usuario },
					new RowMapper<AuthenticationResponse>() {
						@Override
						public AuthenticationResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
							AuthenticationResponse auth = new AuthenticationResponse();
							auth.setUsuarioId(rs.getInt("USUARIO_ID"));
							auth.setUsuario(rs.getString("USUARIO"));
							auth.setPassword(rs.getString("USUARIO_PASSWORD"));
							auth.setCorreo(rs.getString("CORREO"));
							auth.setUsuarioTipo(rs.getInt("USUARIO_TIPO"));
							auth.setEmpleadoActivo(rs.getInt("TIPO_EMPLEADO"));
							Timestamp timestampDB = rs.getTimestamp("ULTIMA_MOD_PWD");
							if(timestampDB!=null) {
								LocalDateTime localDateTime = timestampDB.toLocalDateTime();
							    auth.setUltimoModifiPwd(localDateTime);
							}
							return auth;
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
	
	public Optional<EmpleadoResponse> findByUsuarioOrCorreo(String value) {
		String sql = """
				SELECT
				 	e.EMPLEADO_ID,
				 	e.TIPO_EMPLEADO,
				    i.TC_DEPARTAMENTO_ID,
				    i.TC_PUESTO_ID,
				    ex.TC_SILO_ID,
				    CASE WHEN p.PUESTO_DESCRIPCION is null then 'EXTERNO'
                    ELSE p.PUESTO_DESCRIPCION END as PUESTO_DESCRIPCION
				FROM tc_empleado e
				LEFT JOIN tc_usuario u on e.TC_USUARIO_ID=u.USUARIO_ID
				LEFT JOIN tc_empleado_externo ex on e.EMPLEADO_ID=e.EMPLEADO_ID
				LEFT JOIN tc_empleado_interno i on e.EMPLEADO_ID=i.TC_EMPLEADO_ID
                LEFT JOIN tc_puesto p ON i.TC_PUESTO_ID=p.PUESTO_ID
				where e.CORREO=? or u.USUARIO=?
				LIMIT 1
				""";
		try {
			EmpleadoResponse result = jdbcTemplate.queryForObject(sql, new Object[] { value, value },
					new RowMapper<EmpleadoResponse>() {
						@Override
						public EmpleadoResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
							EmpleadoResponse p = new EmpleadoResponse();
							p.setEmpleadoId(rs.getInt("EMPLEADO_ID"));
							p.setTipoEmpleado(rs.getInt("TIPO_EMPLEADO"));
							p.setDepartamentoId(rs.getInt("TC_DEPARTAMENTO_ID"));
							p.setPuestoId(rs.getInt("TC_PUESTO_ID"));
							p.setSiloId(rs.getInt("TC_SILO_ID"));
							p.setNombrePuesto(rs.getString("PUESTO_DESCRIPCION"));
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
	
	public List<String> findAllClavePedCompra() {
		String sql = """
				SELECT pc.FOLIO_NUM_PED_POSICION from tc_pedido_compra pc
				""";
		try {
			List<String> folios = jdbcTemplate.queryForList(sql, String.class);
			return folios;
		} catch (org.springframework.dao.EmptyResultDataAccessException e) {
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
