package com.bachoco.persistence.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.bachoco.model.ProgramArriboRequest;

@Repository
public class ProgramArriboJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private SimpleJdbcCall simpleJdbcCall;
	private final DataSource dataSource;
	
	public ProgramArriboJdbcRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
			DataSource dataSource) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.dataSource = dataSource;
	}
	
	private int restaCantidadPedTraslado(List<ProgramArriboRequest> req) {
		ProgramArriboRequest update = req.stream()
		        .filter(r -> "Y".equals(r.getIsRestaCantidad()))
		        .findFirst()
		        .orElse(null);
		if(update!=null) {
			String sql = """
					UPDATE tc_pedido_traslado pt
					INNER JOIN tc_detalle_pedido_traslado dpt 
                    ON pt.PEDIDO_TRASLADO_ID= dpt.TC_PEDIDO_TRASLADO_ID 
					SET ESTATUS_CONF_DESPACHO='R'
					WHERE pt.PEDIDO_TRASLADO_ID=?
				    """;
			return jdbcTemplate.update(sql,update.getPedidoTrasladoId());
		}
		return -1;
	}
	
	public String saveProgramArribo(List<ProgramArriboRequest> req) {
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource).withProcedureName("sp_inserta_program_arribo");
		for (ProgramArriboRequest p : req) {
			Map<String, Object> params = new HashMap<>();
			params.put("p_cantidad",p.getCantidad());
			params.put("p_fecha_programada", java.sql.Date.valueOf(p.getFechaProgramada()));
			params.put("p_silo_id",p.getSiloId());
			params.put("p_material_id", p.getMaterialId());
			params.put("p_planta_id",p.getPlantaId());
			params.put("p_pedido_traslado_id",p.getPedidoTrasladoId());
			params.put("p_estatus_conf_despacho",p.getIsRestaCantidad()); //P ES PROGRAMADO, R ES RESTA CANTIDAD
			try {
				simpleJdbcCall.execute(params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		restaCantidadPedTraslado(req);
		return "success";
	}
	
}
