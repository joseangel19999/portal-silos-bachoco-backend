package com.bachoco.persistence.adapter;

import java.util.List;
import java.util.Map;

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
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setCode("ERRO-C-001");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setCode("ERRO-C-002");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		}
		return response;
	}
	
	private boolean isSuma(Float pesoNetoOld,Float pesoNeto) {
		return pesoNetoOld<pesoNeto?true:false;
	}

	@Override
	public ConfirmacionDespachoResponse updateSap(ConfirmacionDespachoRequest req) {
		ConfirmacionDespachoResponse response = new ConfirmacionDespachoResponse();
		boolean isSuma=false;
		try {
			Float pesoNeto = req.getPesoBruto() - req.getPesoTara();
			Float pesoNetoOld = req.getPesoBruto() - req.getPesoTara();
			Float differencia=0F;
			Float pesoNetoOldSinModified=0.0F;
			String jsonResponse="";
			if(req.getTipoMovimiento().equals("352")) {
				pesoNetoOld=this.confDespachoJdbcRepository.findPesoNetoByIdPedtraslado(req.getIdconfDespacho());
				pesoNetoOldSinModified=pesoNetoOld;
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
				if (codeResponse.equals("S")) {
					Map<String, String> result = this.confDespachoJdbcRepository.updateConfDespacho(req, numeroSap,
							codeResponse);
					if (result.get("estatus").equals("0")) {
						response.setCode("0");
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setCode("ERRO-C-001");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setCode("ERRO-C-002");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		}
		return response;
	}

	@Override
	public ConfirmacionDespachoResponse updateBd(ConfirmacionDespachoRequest req) {
		Float pesoNeto = req.getPesoBruto() - req.getPesoTara();
		ConfirmacionDespachoResponse response = new ConfirmacionDespachoResponse();
		try {
			Map<String, String> result = this.confDespachoJdbcRepository.updateConfDespachoSinSap(req, "");
			response.setCode(result.get("estatus"));
			return response;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			pesoNetoOld=this.confDespachoJdbcRepository.findPesoNetoByIdPedtraslado(req.getIdconfDespacho());
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
					int eliminado=this.confDespachoJdbcRepository.deleteById(req.getIdconfDespacho());
					if (eliminado!=0) {
						pedidoTrasladoJdbcRepository.sumaCantidadPedTraslado(pesoNetoOld, req.getNumPedidoTraslado(), 1);
						/*if(!isSuma) {
							pedidoTrasladoJdbcRepository.sumaCantidadPedTraslado(pesoNetoOld, req.getNumPedidoTraslado(), 1);
							this.catalogJdbcRepository.restaCantidadStockSilo(pesoNetoOld,req.getClaveSilo(),-1);
						}else {
							pedidoTrasladoJdbcRepository.restaCantidadPedTraslado(pesoNetoOld, req.getNumPedidoTraslado(), 1);
							this.catalogJdbcRepository.sumaCantidadStockSilo(pesoNetoOld,req.getClaveSilo(),-1);
						}*/
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setCode("ERRO-C-001");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.setCode("ERRO-C-002");
			response.setNumeroSap("");
			response.setMensaje(e.getMessage());
		}
		return response;
	}
}
