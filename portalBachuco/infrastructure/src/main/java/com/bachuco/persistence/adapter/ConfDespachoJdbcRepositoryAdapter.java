package com.bachuco.persistence.adapter;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.bachuco.dto.sap.confDespacho.SapResponse;
import com.bachuco.model.ConfDespachoPesosRequest;
import com.bachuco.model.ConfirmacionDespachoRequest;
import com.bachuco.model.ConfirmacionDespachoResponse;
import com.bachuco.persistence.repository.ConfDespachoJdbcRepository;
import com.bachuco.persistence.repository.PedidoTrasladoJdbcRepository;
import com.bachuco.port.ConfirmacionDespachoJdbcRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ConfDespachoJdbcRepositoryAdapter implements ConfirmacionDespachoJdbcRepository {

	private final ConfirmacionDespachoSapClientAdapter confDespachoSapClientAdapter;
	private final ConfDespachoJdbcRepository confDespachoJdbcRepository;
	private final PedidoTrasladoJdbcRepository pedidoTrasladoJdbcRepository;

	public ConfDespachoJdbcRepositoryAdapter(ConfirmacionDespachoSapClientAdapter confDespachoSapClientAdapter,
			ConfDespachoJdbcRepository confDespachoJdbcRepository,
			PedidoTrasladoJdbcRepository pedidoTrasladoJdbcRepository) {
		this.confDespachoSapClientAdapter = confDespachoSapClientAdapter;
		this.confDespachoJdbcRepository = confDespachoJdbcRepository;
		this.pedidoTrasladoJdbcRepository = pedidoTrasladoJdbcRepository;
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
						pedidoTrasladoJdbcRepository.restaCantidadPedTraslado(pesoNeto, req.getNumPedidoTraslado(), 1);
					} else {
						response.setCode("-1");
						response.setId(result.get("id"));
						response.setMensaje("Error al registrar la confirmacion despacho");
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
	public ConfirmacionDespachoResponse updateSap(ConfirmacionDespachoRequest req) {
		ConfirmacionDespachoResponse response = new ConfirmacionDespachoResponse();
		try {
			Float pesoNeto = req.getPesoBruto() - req.getPesoTara();
			if(req.getTipoMovimiento().equals("352")) {
				pesoNeto=this.confDespachoJdbcRepository.findPesoNetoByIdPedtraslado(req.getIdconfDespacho());
			}
			String jsonResponse = this.confDespachoSapClientAdapter.sendConfirmacionDespacho(req.getClaveSilo(),
					req.getClaveMaterial(), req.getNumPedidoTraslado(), req.getTipoMovimiento(), req.getNumBoleta(),
					pesoNeto.toString(), req.getClaveDestino(), "");
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
						pedidoTrasladoJdbcRepository.restaCantidadPedTraslado(pesoNeto, req.getNumPedidoTraslado(), 1);
					}else {
						response.setCode("0");
						response.setMensaje("Hubo un error al actualizar la confirmacion despacho");
					}
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
}
