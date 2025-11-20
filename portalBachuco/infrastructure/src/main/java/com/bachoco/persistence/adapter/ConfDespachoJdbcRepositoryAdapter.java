package com.bachoco.persistence.adapter;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bachoco.dto.sap.confDespacho.SapResponse;
import com.bachoco.model.ConfDespachoPesosRequest;
import com.bachoco.model.ConfirmDespachoResponse;
import com.bachoco.model.ConfirmacionDespachoRequest;
import com.bachoco.model.ConfirmacionDespachoResponse;
import com.bachoco.persistence.repository.CatalogJdbcRepository;
import com.bachoco.persistence.repository.ConfDespachoJdbcRepository;
import com.bachoco.persistence.repository.PedidoTrasladoJdbcRepository;
import com.bachoco.port.ConfirmacionDespachoJdbcRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConfDespachoJdbcRepositoryAdapter implements ConfirmacionDespachoJdbcRepository {

	private final ConfirmacionDespachoSapClientAdapter confDespachoSapClientAdapter;
	private final ConfDespachoJdbcRepository confDespachoJdbcRepository;
	private final PedidoTrasladoJdbcRepository pedidoTrasladoJdbcRepository;
	private final CatalogJdbcRepository catalogJdbcRepository;
	private final String ESTATUS_EXITOSO_SAP="S";
	private static final Logger logger = LoggerFactory.getLogger(ConfDespachoJdbcRepositoryAdapter.class);

	public ConfDespachoJdbcRepositoryAdapter(ConfirmacionDespachoSapClientAdapter confDespachoSapClientAdapter,
			ConfDespachoJdbcRepository confDespachoJdbcRepository,
			PedidoTrasladoJdbcRepository pedidoTrasladoJdbcRepository, CatalogJdbcRepository catalogJdbcRepository) {
		this.confDespachoSapClientAdapter = confDespachoSapClientAdapter;
		this.confDespachoJdbcRepository = confDespachoJdbcRepository;
		this.pedidoTrasladoJdbcRepository = pedidoTrasladoJdbcRepository;
		this.catalogJdbcRepository = catalogJdbcRepository;
	}

	@Override
	public ConfirmacionDespachoResponse save(ConfirmacionDespachoRequest req) {
		Float pesoNeto = req.getPesoBruto() - req.getPesoTara();
		ConfirmacionDespachoResponse response = new ConfirmacionDespachoResponse();
		try {
			String jsonResponse = this.confDespachoSapClientAdapter.sendConfirmacionDespacho(req.getClaveSilo(),
					req.getClaveMaterial(), req.getNumPedidoTraslado(), req.getTipoMovimiento(), req.getNumBoleta(),
					pesoNeto.toString(), req.getClaveDestino(), "");
			if (jsonResponse != null) {
				ObjectMapper mapper = new ObjectMapper();
				SapResponse responseObj = mapper.readValue(jsonResponse, SapResponse.class);
				response.setCode(responseObj.getT_RETURN().get(0).getType());
				response.setNumeroSap(String.valueOf(responseObj.getT_RETURN().get(0).getMessage_V1()));
				if (response.getCode().equals("S")) {
					Map<String, String> result = this.confDespachoJdbcRepository.registroConfDespacho(req,
							(String) responseObj.getT_RETURN().get(0).getMessage_V1(),
							responseObj.getT_RETURN().get(0).getType());
					response.setMensaje(responseObj.getT_RETURN().get(0).getMessage());
					if (result.get("estatus").equals("0")) {
						response.setId(result.get("id"));
						response.setMensaje(result.get("folio"));
						this.catalogJdbcRepository.restaCantidadStockSilo(pesoNeto,req.getClaveSilo(),-1);
						pedidoTrasladoJdbcRepository.restaCantidadPedTraslado(pesoNeto, req.getNumPedidoTraslado(), 1);
					} else {
						response.setCode("-2");
						response.setId(result.get("id"));
						response.setMensaje("Error al registrar la confirmacion despacho");
					}
				}else {
					response.setCode("-1");
					response.setId("");
					response.setMensaje(responseObj.getT_RETURN().get(0).getMessage());
				}
				return response;
			}
		} catch (JsonMappingException e) {
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.save: "+e.getMessage());
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.save: "+e.getCause());
			response.setCode("ERRO-C-001");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		} catch (JsonProcessingException e) {
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.save: "+e.getMessage());
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.save: "+e.getCause());
			response.setCode("ERRO-C-002");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		}
		/*Map<String, String> result = this.confDespachoJdbcRepository.registroConfDespacho(req,"63535","S");
		response.setId(result.get("id"));
		response.setMensaje(result.get("folio"));
		response.setCode("S");*/
		return response;
	}
	
	private boolean isSuma(Float pesoNetoOld,Float pesoNeto) {
		return pesoNetoOld<pesoNeto?true:false;
	}

	@Override
	public ConfirmacionDespachoResponse updateSap(ConfirmacionDespachoRequest req) {
		ConfirmacionDespachoResponse response = new ConfirmacionDespachoResponse();
		boolean isSuma=false;
		Float pesoNetoResult=0.0F;
		try {
			Float pesoNeto = req.getPesoBruto() - req.getPesoTara();
			Float pesoNetoOld = req.getPesoBruto() - req.getPesoTara();
			Float differencia=0F;
			Float pesoNetoOldSinModified=0.0F;
			String jsonResponse="";
			if(req.getTipoMovimiento().equals("352")) {
				pesoNetoOld=this.confDespachoJdbcRepository.findPesoNetoByIdPedtraslado(req.getIdConfDespacho());
				pesoNetoOldSinModified=pesoNetoOld;
				pesoNetoResult=pesoNetoOldSinModified;
				if(pesoNeto!=null && pesoNetoOld!=null) {
					isSuma=this.isSuma(pesoNetoOld, pesoNeto);
				}
				//isSuma=this.isSuma(pesoNetoOld, pesoNeto);
				if(pesoNetoOld!=null) {
					differencia= Math.abs(pesoNeto-pesoNetoOld);
				}
				jsonResponse=this.confDespachoSapClientAdapter.sendConfirmacionDespacho(req.getClaveSilo(),
						req.getClaveMaterial(), req.getNumPedidoTraslado(), req.getTipoMovimiento(), req.getNumBoleta(),
						pesoNetoOldSinModified.toString(), req.getClaveDestino(), "");
			}else {
				jsonResponse = this.confDespachoSapClientAdapter.sendConfirmacionDespacho(req.getClaveSilo(),
						req.getClaveMaterial(), req.getNumPedidoTraslado(), req.getTipoMovimiento(), req.getNumBoleta(),
						pesoNeto.toString(), req.getClaveDestino(), "");
				pesoNetoResult=pesoNeto;
			}
			if (jsonResponse != null) {
				ObjectMapper mapper = new ObjectMapper();
				SapResponse responseObj = mapper.readValue(jsonResponse, SapResponse.class);
				String codeResponse = responseObj.getT_RETURN().get(0).getType();
				String numeroSap = String.valueOf(responseObj.getT_RETURN().get(0).getMessage_V1());
				String mensajeSap = responseObj.getT_RETURN().get(0).getMessage();
				response.setCode(codeResponse);
				response.setNumeroSap(numeroSap);
				response.setMensaje(mensajeSap);
				if (codeResponse.equals(ESTATUS_EXITOSO_SAP)) {
					Map<String, String> result = this.confDespachoJdbcRepository.updateConfDespacho(req, numeroSap,
							codeResponse);
					if (result.get("estatus").equals("0")) {
						response.setCode("0");
						logger.info("=============== TRANSACCION REGISTRO CON SAP ================");
						logger.info("TIPO MOVIMIENTO: "+req.getTipoMovimiento());
						logger.info("PESO NETO: "+pesoNetoResult);
						logger.info("NUMERO PEDIDO TRASLADO: "+req.getNumPedidoTraslado());
						logger.info("SILO: "+req.getClaveSilo());
						logger.info("MATERIAL: "+req.getClaveMaterial());
						logger.info("===============================================");
						if(!isSuma) {
							pedidoTrasladoJdbcRepository.sumaCantidadPedTraslado(differencia, req.getNumPedidoTraslado(), 1);
							this.catalogJdbcRepository.restaCantidadStockSilo(differencia,req.getClaveSilo(),-1);
						}else {
							pedidoTrasladoJdbcRepository.restaCantidadPedTraslado(differencia, req.getNumPedidoTraslado(), 1);
							this.catalogJdbcRepository.sumaCantidadStockSilo(differencia,req.getClaveSilo(),-1);
						}
						
					}else {
						response.setCode("-1");
						response.setMensaje("Hubo un error al actualizar la confirmacion despacho");
					}
				}else {
					response.setCode("-1");
					response.setId("");
					response.setMensaje("Error al registrar la confirmacion despacho");
				}
				return response;
			}
		} catch (JsonMappingException e) {
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.updateSap: "+e.getMessage());
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.updateSap: "+e.getCause());
			response.setCode("ERRO-C-001");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		} catch (JsonProcessingException e) {
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.updateSap: "+e.getMessage());
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.updateSap: "+e.getCause());
			response.setCode("ERRO-C-002");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		}
		/*Map<String, String> result = this.confDespachoJdbcRepository.updateConfDespacho(req, "00000023",
				"S");
		response.setCode("0");*/
		return response;
	}

	@Override
	public ConfirmacionDespachoResponse updateBd(ConfirmacionDespachoRequest req) {
		Float pesoNeto = req.getPesoBruto() - req.getPesoTara();
		ConfirmacionDespachoResponse response = new ConfirmacionDespachoResponse();
		try {
			Map<String, String> result = this.confDespachoJdbcRepository.updateConfDespachoSinSap(req, "");
			response.setCode(result.get("estatus"));
			logger.info("=============== TRANSACCION REGISTRO SIN SAP ================");
			logger.info("TIPO MOVIMIENTO: "+req.getTipoMovimiento());
			logger.info("NUMERO PEDIDO TRASLADO: "+req.getNumPedidoTraslado());
			logger.info("SILO: "+req.getClaveSilo());
			logger.info("MATERIAL: "+req.getClaveMaterial());
			logger.info("===============================================");
			return response;
		} catch (Exception e) {
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.updateBd: "+e.getMessage());
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.updateBd: "+e.getCause());
			response.setCode("ERRO-C-002");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		}
		return response;
	}

	@Override
	public ConfirmacionDespachoResponse esIgualPesosPorId(ConfDespachoPesosRequest req) {
		ConfirmacionDespachoResponse response = new ConfirmacionDespachoResponse();
		try {
			Boolean result = this.confDespachoJdbcRepository.esIgualPesoBrutoYPesotaraPorId(req.getPesoBruto(),
					req.getPesoTara(), req.getIdconfDespacho());
			if (result) {
				response.setCode("0");
				response.setMensaje("Los pesos son los mismos");
			} else {
				response.setCode("-1");
				response.setMensaje("Los pesos no son los mismsos");
			}
			return response;
		} catch (Exception e) {
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.esIgualPesosPorId: "+e.getMessage());
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.esIgualPesosPorId: "+e.getCause());
			response.setCode("ERRO-C-002");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		}
		return response;
	}

	@Override
	public Float findCantidadPromediotrasporte(Integer siloId) {
		return this.confDespachoJdbcRepository.findPromedioPesoNetoConfDespacho();
	}

	@Override
	public List<ConfirmDespachoResponse> findAllConfirmacionDespacho(String silo, String material, String fechaInicio,
			String fechaFin) {
		return this.confDespachoJdbcRepository.findAllConfirmacionesDespacho(silo,material,fechaInicio,fechaFin);
	}

	@Override
	public ConfirmacionDespachoResponse delete(ConfirmacionDespachoRequest req) {
		ConfirmacionDespachoResponse response = new ConfirmacionDespachoResponse();
		try {
			Float pesoNetoOld = 0.0f;
			String jsonResponse="";
			pesoNetoOld=this.confDespachoJdbcRepository.findPesoNetoByIdPedtraslado(req.getIdConfDespacho());
			if(pesoNetoOld==null) {
				return response;
			}
			jsonResponse=this.confDespachoSapClientAdapter.sendConfirmacionDespacho(req.getClaveSilo(),
						req.getClaveMaterial(), req.getNumPedidoTraslado(), req.getTipoMovimiento(), req.getNumBoleta(),
						pesoNetoOld.toString(), req.getClaveDestino(), "");
			if (jsonResponse != null) {
				ObjectMapper mapper = new ObjectMapper();
				SapResponse responseObj = mapper.readValue(jsonResponse, SapResponse.class);
				String codeResponse = responseObj.getT_RETURN().get(0).getType();
				String numeroSap = String.valueOf(responseObj.getT_RETURN().get(0).getMessage_V1());
				String mensajeSap = responseObj.getT_RETURN().get(0).getMessage();
				response.setCode(codeResponse);
				response.setNumeroSap(numeroSap);
				response.setMensaje(mensajeSap);
				if (codeResponse.equals("S")) {
					//int eliminado=1;
					int eliminado=this.confDespachoJdbcRepository.deleteById(req.getIdConfDespacho());
					if (eliminado!=0) {
						pedidoTrasladoJdbcRepository.sumaCantidadPedTraslado(pesoNetoOld, req.getNumPedidoTraslado(), 1);
					}else {
						response.setCode("-1");
						response.setMensaje("Hubo un error al eliminar la confirmacion despacho");
					}
				}else {
					response.setCode("-1");
					response.setId("");
					response.setMensaje("Error al eliminar la confirmacion despacho");
				}
				return response;
			}
		} catch (JsonMappingException e) {
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.delete: "+e.getMessage());
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.delete: "+e.getCause());
			response.setCode("ERRO-C-001");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		} catch (JsonProcessingException e) {
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.delete: "+e.getMessage());
			logger.error("ERROR EN METODO ConfDespachoJdbcRepositoryAdapter.delete: "+e.getCause());
			response.setCode("ERRO-C-002");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		}
		return response;
	}
}
