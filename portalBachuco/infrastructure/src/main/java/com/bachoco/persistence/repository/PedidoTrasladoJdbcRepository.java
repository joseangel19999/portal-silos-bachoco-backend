package com.bachoco.persistence.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.bachoco.dto.FolioResponseDTO;
import com.bachoco.mapper.rowMapper.PedTrasladoCantDisponibleRowMapper;
import com.bachoco.mapper.rowMapper.PedidoTrasladoRowMapper;
import com.bachoco.model.PedidoTrasladoSapResponseDTO;
import com.bachoco.model.procedores.PedidoTrasladoArriboDTO;
import com.bachoco.model.procedores.PedidoTrasladoDTO;

@Repository
public class PedidoTrasladoJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private SimpleJdbcCall simpleJdbcCall;
	private final DataSource dataSource;
	private final PedidoTrasladoRowMapper pedidoTrasladoRowMapper;
	private final PedTrasladoCantDisponibleRowMapper pedTrasladoCantDisponibleRowMapper;
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public PedidoTrasladoJdbcRepository(JdbcTemplate jdbcTemplate, DataSource dataSource,
			PedidoTrasladoRowMapper pedidoTrasladoRowMapper,
			PedTrasladoCantDisponibleRowMapper pedTrasladoCantDisponibleRowMapper,
			NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
		this.dataSource = dataSource;
		this.pedidoTrasladoRowMapper = pedidoTrasladoRowMapper;
		this.pedTrasladoCantDisponibleRowMapper = pedTrasladoCantDisponibleRowMapper;
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public List<PedidoTrasladoDTO> obtenerPedidosFiltradosPoSiloYMaterial(String claveSilo,String claveMaterial) {
		String sql = "{call ObtenerPedidosTrasladoPorSiloYMaterial(?, ?)}";
		return jdbcTemplate.query(sql, pedidoTrasladoRowMapper, claveSilo, claveMaterial);
	}
	
	public List<FolioResponseDTO> findAllNumeroCompraByFilterProgram(String claveSilo, String claveMaterial) {
		String sql = """
			select pt.NUMERO_PED_TRASLADO,pt.POSICION
				from tc_pedido_traslado pt
                inner join tc_detalle_pedido_traslado dpt on pt.PEDIDO_TRASLADO_ID= dpt.TC_PEDIDO_TRASLADO_ID
                inner join tc_silo s on pt.TC_SILO_ID= s.SILO_ID
                where s.SILO_NOMBRE=?
				    """;
		return jdbcTemplate.query(sql, new Object[] { claveSilo },
				(rs, rowNum) -> new FolioResponseDTO(
				        rs.getString("NUMERO_PED_TRASLADO"),
				        rs.getString("POSICION")
				    ));
	}

	public List<String> findAllFoliosPedTrasladoExist(List<String> folios) {
		 String sql = """
		            SELECT pt.FOLIO_NUM_PED_POSICION
		            FROM tc_pedido_traslado pt
		            WHERE pt.FOLIO_NUM_PED_POSICION IN (:folios)
		        """;
		 Map<String, Object> params = Map.of("folios", folios);
		 return namedParameterJdbcTemplate.query(
		            sql,
		            params,
		            (rs, rowNum) -> rs.getString("FOLIO_NUM_PED_POSICION")
		        );
	}
	
	public List<PedidoTrasladoDTO> findAllByFolioNumCompra(List<String> folios) {
		String sql = """
					SELECT
						pt.PEDIDO_TRASLADO_ID,
				        pt.FOLIO_NUM_PED_POSICION,
				        pt.PLANTA_DESTINO,
				        pt.NUMERO_PED_TRASLADO,
				        dpt.CANTIDAD_PEDIDO,
						dpt.CANTIDAD_TRASLADO,
				        dpt.CANTIDAD_RECIBIDA as CANTIDAD_RECIBIDA_PA,
				        dpt.PENDIENTE_TRASLADO,
				        pc.NUMERO_PEDIDO as NUM_COMPRA_ASOCIADO,
				        dpt.TRASLADO_PENDIENTE_FACTURA,
				        pt.POSICION
				    FROM
				        tc_pedido_traslado pt
				    JOIN tc_detalle_pedido_traslado dpt ON dpt.TC_PEDIDO_TRASLADO_ID = pt.PEDIDO_TRASLADO_ID
				    JOIN tc_pedido_compra pc ON pt.TC_PEDIDO_COMPRA_ID=pc.PEDIDO_COMPRA_ID
				    WHERE
						pt.FOLIO_NUM_PED_POSICION in (:folios)
				    """;
	    Map<String, Object> params = new HashMap<>();
	    params.put("folios", folios);
	    return namedParameterJdbcTemplate.query(sql, params,pedidoTrasladoRowMapper);
	}

	public int sumaCantidadPedTraslado(Float cantidad, String numPedTraslado, Integer idPedTraslado) {
		String sql = """
				UPDATE tc_pedido_traslado pt
				INNER JOIN tc_detalle_pedido_traslado dpt
				            ON pt.PEDIDO_TRASLADO_ID= dpt.TC_PEDIDO_TRASLADO_ID
					SET dpt.CANTIDAD_PEDIDO=dpt.CANTIDAD_PEDIDO+?
				WHERE pt.NUMERO_PED_TRASLADO=?
				   """;
		return jdbcTemplate.update(sql, cantidad, numPedTraslado);
	}

	public int restaCantidadPedTraslado(Float cantidad, String numPedTraslado, Integer idPedTraslado) {
		String sql = """
				UPDATE tc_pedido_traslado pt
				INNER JOIN tc_detalle_pedido_traslado dpt
				               ON pt.PEDIDO_TRASLADO_ID= dpt.TC_PEDIDO_TRASLADO_ID
					SET dpt.CANTIDAD_PEDIDO=dpt.CANTIDAD_PEDIDO-?
				WHERE pt.NUMERO_PED_TRASLADO=?
				   """;
		return jdbcTemplate.update(sql, cantidad, numPedTraslado);
	}
	public List<PedidoTrasladoDTO> obtenerPedidosFiltrados(String claveSilo,String claveMaterial, String fechaInicio,
			String fechaFin) {
		String sql = "{call ObtenerPedidosTrasladoFilters2(?, ?)}";
		if(fechaInicio.equals("-1")) {
			fechaInicio=null;
		}
		if(fechaFin.equals("-1")) {
			fechaFin=null;
		}
		return jdbcTemplate.query(sql, pedidoTrasladoRowMapper, claveSilo, claveMaterial);
	}
	
	public List<PedidoTrasladoDTO> obtenerPedidosFiltradosCantidadDisponible(Integer siloId,Integer materialId, String fechaInicio,
			String fechaFin) {
		String sql = "{call ObtenerPedTrasladoFiltersCantDisponible(?,?,?,?)}";
		if(fechaInicio.equals("-1")) {
			fechaInicio=null;
		}
		if(fechaFin.equals("-1")) {
			fechaFin=null;
		}
		return jdbcTemplate.query(sql, pedidoTrasladoRowMapper, siloId, materialId,fechaInicio,fechaFin);
	}
	
	public List<PedidoTrasladoArriboDTO> obtenerPedidosTrasladoParaArribo(Integer siloId,String plantaDestino,Integer materialId) {
		String sql = "{call sp_obtener_pedidos_traslados_totales(?,?,?)}";
		return jdbcTemplate.query(sql, pedTrasladoCantDisponibleRowMapper, siloId, plantaDestino,materialId);
	}
	
	
	public List<PedidoTrasladoArriboDTO> findByFilterProgramArribo(Integer siloId,String plantaDestino,Integer materialId){
		 String sql = """
				  SELECT 
						pt.PEDIDO_TRASLADO_ID,
						pt.FOLIO_NUM_PED_POSICION,
						pt.NUMERO_PED_TRASLADO as num_pedido,
                        CASE
							WHEN (dpt.CANTIDAD_PEDIDO-da.CANTIDAD) is null THEN dpt.CANTIDAD_PEDIDO
							ELSE (dpt.CANTIDAD_PEDIDO-da.CANTIDAD)
						END AS cantidad_pedido
					FROM tc_pedido_traslado pt
					JOIN tc_detalle_pedido_traslado dpt on pt.PEDIDO_TRASLADO_ID= dpt.TC_PEDIDO_TRASLADO_ID
					JOIN tc_pedido_compra pc ON pt.TC_PEDIDO_COMPRA_ID=pc.PEDIDO_COMPRA_ID
					LEFT JOIN tc_detalle_arribo da ON dpt.TC_PEDIDO_TRASLADO_ID=da.TC_PEDIDO_TRASLADO_ID
					WHERE 
						pc.TC_SILO_ID=?
					    AND pt.PLANTA_DESTINO=?
						AND pt.TC_MATERIAL_ID=?
						AND (0<>(dpt.CANTIDAD_PEDIDO-da.CANTIDAD) or (dpt.CANTIDAD_PEDIDO-da.CANTIDAD) is null)
						AND (pt.FECHA_PED_TRASLADO IS NULL OR ESTATUS_CONF_DESPACHO='R');
		        """;
		        return jdbcTemplate.query(
		            sql,
		            new Object[]{siloId,plantaDestino, materialId},  // parámetros
		            (rs, rowNum) -> new PedidoTrasladoArriboDTO(
		            	rs.getInt("PEDIDO_TRASLADO_ID"),	
		            	rs.getString("FOLIO_NUM_PED_POSICION"),
		                rs.getString("num_pedido"),
		                rs.getFloat("cantidad_pedido")
		            )
		        );
	}
	
	public List<PedidoTrasladoArriboDTO> findByPedidosTrasladoParaConfDespacho(Integer siloId,Integer materialId,
			String fechaInicio,String fechaFin){
		 String sql = """
					SELECT 
		 		    	pt.PEDIDO_TRASLADO_ID,
						pt.FOLIO_NUM_PED_POSICION,
						pt.NUMERO_PED_TRASLADO as num_pedido,
						dpt.CANTIDAD_PEDIDO as cantidad_pedido
		 			FROM tc_pedido_traslado pt 
			 		JOIN tc_detalle_pedido_traslado dpt on pt.PEDIDO_TRASLADO_ID= dpt.TC_PEDIDO_TRASLADO_ID
                    JOIN tc_detalle_arribo da ON pt.PEDIDO_TRASLADO_ID=da.TC_PEDIDO_TRASLADO_ID
                    JOIN tc_pedido_compra pc ON pt.TC_PEDIDO_COMPRA_ID=pc.PEDIDO_COMPRA_ID
			 		where 
	                    pc.TC_SILO_ID=?
                        AND pt.TC_MATERIAL_ID=?
	                    AND dpt.CANTIDAD_PEDIDO<>0
	                    AND pt.FECHA_PED_TRASLADO IS NOT NULL
	                    AND (? IS NULL OR da.FECHA_PROGRAMADA >= STR_TO_DATE(?, '%Y-%m-%d'))
		 				AND (? IS NULL OR da.FECHA_PROGRAMADA <= STR_TO_DATE(?, '%Y-%m-%d'));
		        """;
				 if(fechaInicio.equals("-1")) {
						fechaInicio=null;
					}
					if(fechaFin.equals("-1")) {
						fechaFin=null;
					}
		        return jdbcTemplate.query(
		            sql,
		            new Object[]{siloId,materialId,fechaInicio,fechaInicio,fechaFin,fechaFin},  // parámetros
		            (rs, rowNum) -> new PedidoTrasladoArriboDTO(
		            	rs.getInt("PEDIDO_TRASLADO_ID"),	
		            	rs.getString("FOLIO_NUM_PED_POSICION"),
		                rs.getString("num_pedido"),
		                rs.getFloat("cantidad_pedido")
		            )
		        );
	}
	
	public void savePedidoTraslado(List<PedidoTrasladoSapResponseDTO> pedido,String claveSilo) {
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource).withProcedureName("sp_insert_ped_traslado");
		/*List<PedidoTrasladoSapResponseDTO> prueba= new ArrayList<>();
		prueba.add(pedido.get(0));*/
		for (PedidoTrasladoSapResponseDTO p : pedido) {
			if(p.getPedidoDeComprasAsociado()!=null && p.getMaterial()!=null 
					&& p.getNumeroPedTraslado()!=null
					&& p.getPosicion()!=null
					&& p.getPlantaDestino()!=null) {
				Map<String, Object> params = new HashMap<>();
				params.put("p_numero_ped_traslado", p.getNumeroPedTraslado());
				params.put("p_numero_ped_compras_asociado", p.getPedidoDeComprasAsociado());
				params.put("p_pedido_traslado", p.getCantidadEnTraslado()!=null?p.getCantidadEnTraslado():0);
				params.put("p_cantidad_pedido", p.getCantidadPedido()!=null?p.getCantidadPedido():0);
				params.put("p_cantidad_traslado", p.getCantidadPendienteTraslado()!=null?p.getCantidadPendienteTraslado():0);
				params.put("p_cantidad_recibida_pa",p.getCantidadRecibidaEnPa()!=null?p.getCantidadRecibidaEnPa():0);
				params.put("p_cantidad_pend_traslado",p.getCantidadPendienteTraslado()!=null?p.getCantidadPendienteTraslado():0);
				params.put("p_traslado_fact_pendiente",0);
				params.put("p_clave_material",p.getMaterial());
				params.put("p_posicion",p.getPosicion());
				params.put("p_planta",p.getPlantaDestino());
				params.put("p_clave_silo",claveSilo); //FALTA CAMPO DE CONTRATO LEGAL
				params.put("p_material", p.getMaterial());
				params.put("p_planta_recep", p.getPlantaDestino());
				params.put("p_clave_planta_destino",p.getPlantaDestino());
				try {
					simpleJdbcCall.execute(params);
				}catch (DataIntegrityViolationException e) {
					e.printStackTrace();
					throw new RuntimeException("Error: violacion de integridad no existe ");
				} 
				catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error: "+e.getMessage());
				}
			}
		}

	}
}
