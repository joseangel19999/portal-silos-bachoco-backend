package com.bachuco.persistence.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import com.bachuco.dto.FolioResponseDTO;
import com.bachuco.mapper.rowMapper.PedidoTrasladoRowMapper;
import com.bachuco.model.PedidoSapResponseDTO;
import com.bachuco.model.procedores.PedidoTrasladoArriboDTO;
import com.bachuco.model.procedores.PedidoTrasladoDTO;

@Repository
public class PedidoTrasladoJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private SimpleJdbcCall simpleJdbcCall;
	private final DataSource dataSource;
	private final PedidoTrasladoRowMapper pedidoTrasladoRowMapper;
	


	public PedidoTrasladoJdbcRepository(JdbcTemplate jdbcTemplate, DataSource dataSource,
			PedidoTrasladoRowMapper pedidoTrasladoRowMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.dataSource = dataSource;
		this.pedidoTrasladoRowMapper = pedidoTrasladoRowMapper;
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
	
	public List<PedidoTrasladoArriboDTO> findByFilterProgramArribo(Integer siloId,Integer materialId){
		 String sql = """
		            select pt.NUMERO_PED_TRASLADO as num_pedido,dpt.CANTIDAD_PEDIDO as cantidad_pedido
		 			from tc_pedido_traslado pt 
		 			inner join tc_detalle_pedido_traslado dpt on pt.PEDIDO_TRASLADO_ID= dpt.TC_PEDIDO_TRASLADO_ID
		 			where pt.TC_SILO_ID=? and pt.TC_MATERIAL_ID=? dpt.CANTIDAD_PEDIDO<>0
		        """;
		        return jdbcTemplate.query(
		            sql,
		            new Object[]{siloId, materialId},  // parámetros
		            (rs, rowNum) -> new PedidoTrasladoArriboDTO(
		                rs.getString("num_pedido"),
		                rs.getFloat("cantidad_pedido")
		            )
		        );
	}
	
	public void savePedidoTraslado(List<PedidoSapResponseDTO> pedido) {
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource).withProcedureName("sp_insert_ped_traslado");
		for (PedidoSapResponseDTO p : pedido) {
			Map<String, Object> params = new HashMap<>();
			params.put("p_numero_ped_traslado", p.getPedCompra());
			params.put("p_numero_ped_compras_asociado", p.getPedidoRelacionado());
			params.put("p_pedido_traslado", p.getCantidadPedido()!=null?p.getCantidadPedido():0);
			params.put("p_cantidad_pedido", p.getCantidadEntrega()!=null?p.getCantidadEntrega():0);
			params.put("p_cantidad_traslado", 0);
			params.put("p_cantidad_recibida_pa", 0);
			params.put("p_cantidad_pend_traslado", 0);
			params.put("p_traslado_fact_pendiente",0);
			params.put("p_clave_material",p.getMaterial());
			params.put("p_posicion",p.getPosicion());
			params.put("p_planta","CP12");
			params.put("p_clave_silo", "CP12"); //FALTA CAMPO DE CONTRATO LEGAL
			params.put("p_material", "");
			params.put("p_planta_recep", p.getPlantaReceptor());
			params.put("p_clave_planta_destino", p.getPlantaReceptor());

			try {
				simpleJdbcCall.execute(params);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
