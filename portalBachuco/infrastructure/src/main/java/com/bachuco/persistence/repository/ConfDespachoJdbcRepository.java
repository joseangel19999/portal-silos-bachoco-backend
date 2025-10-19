package com.bachuco.persistence.repository;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import com.bachuco.model.ConfirmacionDespachoRequest;
import com.bachuco.model.ReportConfDespacho;

@Repository
public class ConfDespachoJdbcRepository {

	private final JdbcTemplate jdbcTemplate;
	private SimpleJdbcCall simpleJdbcCall;
	private final DataSource dataSource;

	public ConfDespachoJdbcRepository(JdbcTemplate jdbcTemplate, DataSource dataSource) {
		this.jdbcTemplate = jdbcTemplate;
		this.dataSource = dataSource;
	}

	public ReportConfDespacho findConfDespachoById(Integer idConfDespacho) {
		String sql = """
				SELECT
					cd.NUMERO_MOV_SAP,
				    cd.FOLIO,
				    pt.NUMERO_PED_TRASLADO,
				    cd.FECHA_EMBARQUE,
				    p.NOMBRE,
				    'SILO' AS silo,
				    m.MATERIAL_DESCRIPCION,
				    cd.NUMERO_BOLETA,
				    cd.PESO_BRUTO,
				    cd.PESO_TARA,
				    cd.HUMEDAD,
				    cd.CHOFER,
				    cd.PLACA_JAULA,
				    cd.LINEA_TRANSPORTISTA
				FROM tc_confirmacion_despacho cd
				JOIN tc_pedido_traslado pt ON cd.TC_PEDIDO_TRASLADO_ID=pt.PEDIDO_TRASLADO_ID
				JOIN tc_planta p ON cd.TC_PLANTA_ID= p.PLANTA_ID
				JOIN tc_material m ON cd.TC_MATERIAL_ID=m.MATERIAL_ID
				where cd.CONFIRMACION_DESPACHO_ID=?
				     """;
		return (ReportConfDespacho) jdbcTemplate.queryForObject(sql, new Object[] { idConfDespacho }, // parámetros
				(rs, rowNum) -> new ReportConfDespacho(rs.getString("NUMERO_MOV_SAP"), rs.getString("FOLIO"),
						rs.getString("NUMERO_PED_TRASLADO"), rs.getString("FECHA_EMBARQUE"), rs.getString("NOMBRE"),
						rs.getString("silo"), rs.getString("MATERIAL_DESCRIPCION"), rs.getString("NUMERO_BOLETA"),
						rs.getString("PESO_BRUTO"), rs.getString("PESO_TARA"), rs.getString("HUMEDAD"),
						rs.getString("CHOFER"), rs.getString("PLACA_JAULA"), rs.getString("LINEA_TRANSPORTISTA")));
	}

	public Boolean esIgualPesoBrutoYPesotaraPorId(Float pesoBruto, Float pesoTara, Integer idConfDespacho) {
		String sql = """
				    SELECT 1 FROM tc_confirmacion_despacho cd
				    WHERE cd.CONFIRMACION_DESPACHO_ID = ?
				    AND cd.PESO_BRUTO = ?
				    AND cd.PESO_TARA = ?
				""";
		try {
			Integer resultado = jdbcTemplate.queryForObject(sql, Integer.class, // El tipo esperado: Integer
					idConfDespacho, pesoBruto, pesoTara);
			return resultado != null && resultado.equals(1);
		} catch (EmptyResultDataAccessException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	public Float findPesoNetoByIdPedtraslado(Integer idConfDespacho) {
		String sql = """
				        SELECT cd.PESO_NETO FROM tc_confirmacion_despacho cd
				    WHERE cd.CONFIRMACION_DESPACHO_ID =?
				""";
		try {
			Float resultado = jdbcTemplate.queryForObject(sql, Float.class, idConfDespacho);
			return resultado != null ? resultado : null;
		} catch (EmptyResultDataAccessException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, String> registroConfDespacho(ConfirmacionDespachoRequest req, String numeroSap, String estatus) {
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource).withProcedureName("sp_inserta_conf_despacho");
		Map<String, Object> params = new HashMap<>();
		Map<String, String> response = new HashMap<>();
		params.put("p_clave_bodega", req.getClaveBodega());
		params.put("p_clave_silo", "CE12");
		params.put("p_clave_material", req.getClaveMaterial());
		params.put("p_fecha_embarque", java.sql.Date.valueOf(req.getFechaEmbarque()));
		params.put("p_numero_boleta", req.getNumBoleta());
		params.put("p_peso_bruto", req.getPesoBruto());
		params.put("p_peso_tara", req.getPesoTara());
		params.put("p_humedad", req.getHumedad());
		params.put("p_chofer", req.getChofer());
		params.put("p_placa_jaula", req.getPlacaJaula());
		params.put("p_linea_transportista", req.getLineaTransportista());
		params.put("p_clave_destino", req.getClaveDestino());
		params.put("p_num_pedido_traslado", req.getNumPedidoTraslado());
		params.put("p_tipo_movimiento", req.getTipoMovimiento());
		params.put("p_folio", numeroSap);
		params.put("p_numero_sap", numeroSap);
		params.put("p_pedido_traslado_num", req.getNumPedidoTraslado());
		params.put("ESTATUS_DESPACHO", estatus);
		try {
			Map<String, Object> result = simpleJdbcCall.execute(params);
			Integer idGenerado = (Integer) result.get("p_conf_despacho_id");
			response.put("estatus", "0");
			response.put("id", idGenerado.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response.put("estatus", "-1");
			response.put("id", "-1");
		}
		return response;
	}

	public Map<String, String> updateConfDespacho(ConfirmacionDespachoRequest req, String numeroSap, String estatus) {
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource).withProcedureName("sp_modifica_conf_despacho");
		Map<String, Object> params = new HashMap<>();
		Map<String, String> response = new HashMap<>();
		params.put("p_conf_despacho_id", req.getIdconfDespacho());
		params.put("p_clave_bodega", req.getClaveBodega());
		params.put("p_clave_silo", "CE12");
		params.put("p_clave_material", req.getClaveMaterial());
		params.put("p_fecha_embarque", java.sql.Date.valueOf(req.getFechaEmbarque()));
		params.put("p_numero_boleta", req.getNumBoleta());
		params.put("p_peso_bruto", req.getPesoBruto());
		params.put("p_peso_tara", req.getPesoTara());
		params.put("p_humedad", req.getHumedad());
		params.put("p_chofer", req.getChofer());
		params.put("p_placa_jaula", req.getPlacaJaula());
		params.put("p_linea_transportista", req.getLineaTransportista());
		params.put("p_clave_destino", req.getClaveDestino());
		params.put("p_num_pedido_traslado", req.getNumPedidoTraslado());
		params.put("p_tipo_movimiento", req.getTipoMovimiento());
		params.put("p_folio", numeroSap);
		params.put("p_numero_sap", numeroSap);
		params.put("p_pedido_traslado_id", req.getIdPedTraslado());
		params.put("ESTATUS_DESPACHO", estatus);
		try {
			Map<String, Object> result = simpleJdbcCall.execute(params);
			// Integer idGenerado = (Integer) result.get("p_conf_despacho_id");
			response.put("estatus", "0");
			response.put("id", "");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("estatus", "-1");
			response.put("id", "-1");
		}
		return response;
	}

	public Map<String, String> updateConfDespachoSinSap(ConfirmacionDespachoRequest req, String estatus) {
		this.simpleJdbcCall = new SimpleJdbcCall(dataSource)
				.withProcedureName("sp_modifica_conf_despacho_sin_response_sap");
		Map<String, Object> params = new HashMap<>();
		Map<String, String> response = new HashMap<>();
		params.put("p_conf_despacho_id", req.getIdconfDespacho());
		params.put("p_clave_bodega", req.getClaveBodega());
		params.put("p_clave_silo", "CE12");
		params.put("p_clave_material", req.getClaveMaterial());
		params.put("p_fecha_embarque", java.sql.Date.valueOf(req.getFechaEmbarque()));
		params.put("p_numero_boleta", req.getNumBoleta());
		params.put("p_humedad", req.getHumedad());
		params.put("p_chofer", req.getChofer());
		params.put("p_placa_jaula", req.getPlacaJaula());
		params.put("p_linea_transportista", req.getLineaTransportista());
		params.put("p_clave_destino", req.getClaveDestino());
		params.put("p_num_pedido_traslado", req.getNumPedidoTraslado());
		params.put("p_tipo_movimiento", req.getTipoMovimiento());
		params.put("p_pedido_traslado_id", req.getIdPedTraslado());
		params.put("ESTATUS_DESPACHO", estatus);
		try {
			Map<String, Object> result = simpleJdbcCall.execute(params);
			// Integer idGenerado = (Integer) result.get("p_conf_despacho_id");
			response.put("estatus", "0");
			response.put("id", "");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("estatus", "-1");
			response.put("id", "-1");
		}
		return response;
	}

}
