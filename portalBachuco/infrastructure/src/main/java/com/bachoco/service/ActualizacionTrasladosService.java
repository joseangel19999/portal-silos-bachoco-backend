package com.bachoco.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.bachoco.model.PedidoTrasladoSapResponseDTO;
import com.bachoco.utils.ComparadorTrasladosSAP;

@Component
public class ActualizacionTrasladosService {

	 	@Autowired
	    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	    
	    public void procesarActualizacionTraslados(List<PedidoTrasladoSapResponseDTO> pedidosSapNoExistBd) {
	        if (pedidosSapNoExistBd == null || pedidosSapNoExistBd.isEmpty()) {
	            return;
	        }
	        
	        // 1. Extraer todos los folios únicos de los DTOs SAP
	        List<String> folios = extraerFoliosDeSAPTraslado(pedidosSapNoExistBd);
	        
	        // 2. Obtener datos actuales de la base de datos
	        List<Map<String, Object>> datosActualesBD = findTrasladosByFolios(folios);
	        
	        // 3. Encontrar qué folios necesitan actualización
	        ComparadorTrasladosSAP comparador = new ComparadorTrasladosSAP();
	        List<PedidoTrasladoSapResponseDTO> trasladosParaActualizar = 
	            comparador.encontrarTrasladosParaActualizar(datosActualesBD, pedidosSapNoExistBd);
	        
	        actualizarTrasladosEnBaseDeDatos(trasladosParaActualizar);
	        // 4. Actualizar en la base de datos
	        if (!trasladosParaActualizar.isEmpty()) {
	            System.out.println("Actualizando " + trasladosParaActualizar.size() + " traslados...");
	            actualizarTrasladosEnBaseDeDatos(trasladosParaActualizar);
	        } else {
	            System.out.println("No hay traslados que actualizar");
	        }
	    }
	    
	    private List<String> extraerFoliosDeSAPTraslado(List<PedidoTrasladoSapResponseDTO> pedidosSap) {
	        return pedidosSap.stream()
	            .map(dto -> construirFolioTraslado(dto))
	            .distinct()
	            .collect(Collectors.toList());
	    }
	    
	    private String construirFolioTraslado(PedidoTrasladoSapResponseDTO dto) {
	        return dto.getNumeroPedTraslado() +"-"+ dto.getPosicion();
	    }
	    
	    private void actualizarTrasladosEnBaseDeDatos(List<PedidoTrasladoSapResponseDTO> trasladosParaActualizar) {
	        String sql = """
	            UPDATE tc_pedido_traslado pt
	            JOIN tc_detalle_pedido_traslado dtpt ON dtpt.TC_PEDIDO_TRASLADO_ID = pt.PEDIDO_TRASLADO_ID
	            SET dtpt.PEDIDO_TRASLADO = :pedidoTraslado,
	                dtpt.CANTIDAD_PEDIDO = :cantidadPedido,
	                dtpt.CANTIDAD_TRASLADO = :cantidadTraslado,
	                dtpt.CANTIDAD_RECIBIDA = :cantidadRecibida,
	                dtpt.PENDIENTE_TRASLADO = :pendienteTraslado,
	                dtpt.TRASLADO_PENDIENTE_FACTURA = :numeroPedndFacturas
	            WHERE pt.NUMERO_PED_TRASLADO = :numeroPedTraslado
	            AND pt.POSICION = :posicion
	            """;
	        List<MapSqlParameterSource> batchParams = new ArrayList<>();
	        for (PedidoTrasladoSapResponseDTO dto : trasladosParaActualizar) {
	            MapSqlParameterSource params = new MapSqlParameterSource();
	            params.addValue("numeroPedTraslado", dto.getNumeroPedTraslado());
	            params.addValue("posicion", dto.getPosicion());
	            params.addValue("pedidoTraslado", parseFloatSafe(dto.getCantidaddespacho()));
	            params.addValue("cantidadPedido", parseFloatSafe(dto.getCantidadPedido()));
	            params.addValue("cantidadTraslado", parseFloatSafe(dto.getCantidadEnTraslado()));
	            params.addValue("cantidadRecibida", parseFloatSafe(dto.getCantidadRecibidaEnPa()));
	            params.addValue("pendienteTraslado", parseFloatSafe(dto.getCantidadPendienteTraslado()));
	            params.addValue("numeroPedndFacturas", parseFloatSafe(dto.getTrasladosPendientes()));
	            batchParams.add(params);
	        }
	        // Ejecutar actualización por lotes
	        try {
		        namedParameterJdbcTemplate.batchUpdate(sql, batchParams.toArray(new MapSqlParameterSource[0]));
	        }catch (Exception e) {
				// TODO: handle exception
			}
	    }
	    
	    private Float parseFloatSafe(String value) {
	        if (value == null || value.trim().isEmpty()) {
	            return 0.0f;
	        }
	        try {
	            return Float.parseFloat(value);
	        } catch (NumberFormatException e) {
	            return 0.0f;
	        }
	    }
	    
	    // Método para obtener datos de BD
	    public List<Map<String, Object>> findTrasladosByFolios(List<String> folios) {
	        String sql = """
	            SELECT
	                ped.FOLIO_NUM_PED_POSICION as folio,
	                dtpt.PEDIDO_TRASLADO,
	                dtpt.CANTIDAD_PEDIDO,
	                dtpt.CANTIDAD_TRASLADO AS CANTIDAD_EN_TRASLADO,
	                dtpt.CANTIDAD_RECIBIDA AS CANTIDAD_RECIBIDA_ENPA,
	                dtpt.PENDIENTE_TRASLADO AS CANTIDAD_PENDIENTE_TRASLADO
	            FROM tc_pedido_traslado ped
	            JOIN tc_detalle_pedido_traslado dtpt ON dtpt.TC_PEDIDO_TRASLADO_ID = ped.PEDIDO_TRASLADO_ID
	            WHERE ped.FOLIO_NUM_PED_POSICION IN (:folios)
	            """;

	        Map<String, Object> params = new HashMap<>();
	        params.put("folios", folios);

	        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
	            Map<String, Object> row = new HashMap<>();
	            row.put("folio", rs.getString("folio"));
	            row.put("pedidoTraslado", rs.getFloat("PEDIDO_TRASLADO"));
	            row.put("cantidadPedido", rs.getFloat("CANTIDAD_PEDIDO"));
	            row.put("cantidadTraslado", rs.getFloat("CANTIDAD_EN_TRASLADO"));
	            row.put("cantidadRecibidaEnPA", rs.getFloat("CANTIDAD_RECIBIDA_ENPA"));
	            row.put("cantidadPendienteTraslado", rs.getFloat("CANTIDAD_PENDIENTE_TRASLADO"));
	            return row;
	        });
	    }
	    
	    public Map<String, Map<String, Object>> convertToFolioMapTraslado(List<Map<String, Object>> resultados) {
	        return resultados.stream()
	            .collect(Collectors.toMap(
	                row -> (String) row.get("folio"),
	                row -> row
	            ));
	    }
}
