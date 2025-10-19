package com.bachuco.service.usecase;

import java.io.IOException;
import java.util.List;

import com.bachuco.model.Documento;
import com.bachuco.model.procedores.PedidoCompraDTO;
import com.bachuco.model.service.DocumentoRepositoryPort;
import com.bachuco.port.PedidoCompraJdbcRepositoryPort;
import com.bachuco.utils.DocumentoUtil;

public class PedidoCompraUsecase {
	
	private final PedidoCompraJdbcRepositoryPort pedidoCompraJdbcRepositoryPort;
	private final DocumentoRepositoryPort documentoRepositoryPort;

	public PedidoCompraUsecase(PedidoCompraJdbcRepositoryPort pedidoCompraJdbcRepositoryPort,
			DocumentoRepositoryPort documentoRepositoryPort) {
		this.pedidoCompraJdbcRepositoryPort = pedidoCompraJdbcRepositoryPort;
		this.documentoRepositoryPort = documentoRepositoryPort;
	}

	public List<PedidoCompraDTO> findAll(String claveSilo,String claveMaterial,String fechaInicio,String fechaFin){
		return this.pedidoCompraJdbcRepositoryPort.findByFilterSiloAndMaterialAnFecha(claveSilo, claveMaterial, fechaInicio, fechaFin);
	}
	
	public List<PedidoCompraDTO> findAllPedidosCompraSap(String claveSilo,String claveMaterial,Integer materialId,String fechaInicio,String fechaFin){
		return this.pedidoCompraJdbcRepositoryPort.findAllComprasSapByFilters(claveSilo,claveMaterial, fechaInicio, fechaFin);
	}
	
	public String saveDocument(byte[] data,String fileName,String ruta,String extencion,Integer pedidoCompraId) {
		Documento documento=new Documento(data,fileName,ruta,extencion);
		try {
			return documentoRepositoryPort.guardar(documento,DocumentoUtil.RUTA_ALMACENAMIENTO,pedidoCompraId);
		}catch (IOException e) {
			new IOException("Error al guardar el documento");
		}catch (Exception e) {
			new Exception("Hubo un error");
		}
		return "";
	}
	
	public boolean deleteDocumento(String documentoId,String ruta,Integer pedidoCompraId) {
		try {
			return documentoRepositoryPort.eliminar(documentoId,ruta,pedidoCompraId);
		}catch (IOException e) {
			new IOException("Error al guardar el documento");
		}catch (Exception e) {
			new Exception("Hubo un error");
		}
		return false;
	}
	
	public byte[] dowload(String documentoId,String ruta) throws IOException {
		return this.documentoRepositoryPort.dowload(documentoId, ruta);
	}

}
