package com.bachoco.persistence.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.bachoco.mapper.rowMapper.PedidoCompraRowMapper;
import com.bachoco.model.PedidoCompraSapDTO;
import com.bachoco.model.PedidoSapResponseDTO;
import com.bachoco.model.procedores.PedidoCompraDTO;

@Repository
public class PedidoCompraJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private SimpleJdbcCall simpleJdbcCall;
	private final DataSource dataSource;
	private final PedidoCompraRowMapper pedidoCompraRowMapper;


	
	public PedidoCompraJdbcRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
			DataSource dataSource, PedidoCompraRowMapper pedidoCompraRowMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
		this.dataSource = dataSource;
		this.pedidoCompraRowMapper = pedidoCompraRowMapper;
	}

	public List<PedidoCompraDTO> obtenerPedidosPorSilo(String claveSilo) {
		String sql = "{call ObtenerPedidosCompraPorSilo(?)}";
		return jdbcTemplate.query(sql, pedidoCompraRowMapper, claveSilo);
	}

	public List<PedidoCompraDTO> obtenerPedidosFiltrados(Integer siloId, Integer materialId, String fechaInicio,
			String fechaFin) {
		String sql = "{call ObtenerPedidosCompraFiltrados(?, ?, ?, ?)}";
		if (fechaInicio.equals("-1")) {
			fechaInicio = null;
		}
		if (fechaFin.equals("-1")) {
			fechaFin = null;
		}
		return jdbcTemplate.query(sql, pedidoCompraRowMapper, siloId, materialId, fechaInicio, fechaFin);
	}

	public List<PedidoCompraDTO> findAllByFolioNumCompra(List<String> folios) {
		String sql = """
				  SELECT
					pc.PEDIDO_COMPRA_ID,
			        pc.NUMERO_PEDIDO,
			        pc.POSICION,
			        pc.CANTIDAD_PEDIDA,
			        dtpc.CANTIDAD_ENTREGADA,
			        dtpc.CANTIDAD_DESPACHADA,
			        dtpc.CANTIDAD_PENDIENTE_DESPACHO,
			        pc.CONTRATO_LEGAL,
			        pc.URL_CER_CONSERVACION,
			        pc.CER_DEPOSITO,
			        pc.TIPO_EXTENCION
			    FROM
			        tc_pedido_compra pc
			    JOIN
			        tc_detalle_pedido_compra dtpc ON dtpc.TC_PEDIDO_COMPRA_ID = pc.PEDIDO_COMPRA_ID
			    WHERE
			        pc.FOLIO_NUM_PED_POSICION in (:folios)
				    """;

	    Map<String, Object> params = new HashMap<>();
	    params.put("folios", folios);
	    return namedParameterJdbcTemplate.query(sql, params,pedidoCompraRowMapper);
	}
	
	public List<String> findAllFoliosExist(List<String> folios) {
		String sql = """
				   SELECT pc.FOLIO_NUM_PED_POSICION
		            FROM tc_pedido_compra pc
		            WHERE pc.FOLIO_NUM_PED_POSICION IN (:folios)
				    """;

	    Map<String, Object> params = new HashMap<>();
	    params.put("folios", folios);
		 return namedParameterJdbcTemplate.query(
		            sql,
		            params,
		            (rs, rowNum) -> rs.getString("FOLIO_NUM_PED_POSICION")
		        );
	}
	
	public List<String> findAllNumeroCompraByFilterProgram(String claveSilo, Integer materialId) {
		String sql = """
				select CONCAT(p.NUMERO_PEDIDO,'-',p.POSICION) AS FOLIO_PED_COMPRA
				from tc_pedido_compra p
                inner join tc_detalle_pedido_compra pc on p.PEDIDO_COMPRA_ID= pc.TC_PEDIDO_COMPRA_ID
                inner join tc_silo s on p.TC_SILO_ID= s.SILO_ID
                where s.SILO_NOMBRE=?
				    """;
		return jdbcTemplate.query(sql, new Object[] { claveSilo },
				(rs, rowNum) -> rs.getString("FOLIO_PED_COMPRA"));
	}

	public void savePedidoCompra(List<PedidoSapResponseDTO> pedido) {
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource).withProcedureName("sp_insert_compra_venta");
		for (PedidoSapResponseDTO p : pedido) {
			Map<String, Object> params = new HashMap<>();
			params.put("p_numero_ped_compra", p.getPedCompra());
			params.put("p_cantidad_pedida", p.getCantidadPedido()!=null?p.getCantidadPedido():0);
			params.put("p_cantidad_entregada", p.getCantidadEntrega()!=null?p.getCantidadEntrega():0);
			params.put("p_cantidad_despachada",p.getCantidadDespacho()!=null?p.getCantidadDespacho():0);
			params.put("p_cantidad_pend_despacho", p.getCantidadPendienteDespacho());
			params.put("p_clave_material",p.getMaterial());
			params.put("p_posicion",p.getPosicion());
			params.put("p_contrato_legal",p.getContratoLegal());
			params.put("p_cert_deposito", "");
			params.put("p_silo", "CP12");
			params.put("p_material", "");
			params.put("p_planta_recep", p.getPlantaReceptor());
			try {
				simpleJdbcCall.execute(params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
