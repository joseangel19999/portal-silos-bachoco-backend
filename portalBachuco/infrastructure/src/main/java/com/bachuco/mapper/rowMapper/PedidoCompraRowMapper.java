package com.bachuco.mapper.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.bachuco.model.procedores.PedidoCompraDTO;

@Component
public class PedidoCompraRowMapper implements RowMapper<PedidoCompraDTO> {

	@Override
	public PedidoCompraDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
		PedidoCompraDTO pedido= new PedidoCompraDTO();
		pedido.setPedidoCompraId(rs.getInt("PEDIDO_COMPRA_ID"));
		pedido.setNumeroPedido(rs.getString("NUMERO_PEDIDO"));
		pedido.setPosicion(rs.getString("POSICION"));
		pedido.setCantidadPedida(rs.getFloat("CANTIDAD_PEDIDA"));
		pedido.setCantidadEntregada(rs.getFloat("CANTIDAD_ENTREGADA"));
		pedido.setCantidadDespachada(rs.getFloat("CANTIDAD_DESPACHADA"));
		if(rs.getString("CONTRATO_LEGAL")==null) {
			pedido.setContratoLegal("");
		}else {
			pedido.setContratoLegal(rs.getString("CONTRATO_LEGAL"));
		}
		pedido.setUrlCertificadoDeposito(rs.getString("URL_CER_CONSERVACION"));
		pedido.setTipoExtencion(rs.getString("TIPO_EXTENCION"));
		return pedido;
	}

}
