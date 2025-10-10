package com.bachuco.persistence.adapter;

import com.bachuco.model.ConfirmacionDespachoRequest;
import com.bachuco.port.ConfirmacionDespachoJdbcRepository;

public class ConfDespachoJdbcRepositoryAdapter implements ConfirmacionDespachoJdbcRepository {

	private final ConfirmacionDespachoSapClientAdapter confDespachoSapClientAdapter;
	
	public ConfDespachoJdbcRepositoryAdapter(ConfirmacionDespachoSapClientAdapter confDespachoSapClientAdapter) {
		this.confDespachoSapClientAdapter = confDespachoSapClientAdapter;
	}

	@Override
	public void save(ConfirmacionDespachoRequest req) {
		Float pesoNeto=req.getPesoBruto()-req.getPesoTara();
		/*String claveSilo, 
		Integer claveMaterial, 
		String claveMovimiento,
		String numBoleta,
		String pesoNeto, 
		String destino,
		String ruta*/
		/*this.confDespachoSapClientAdapter.sendConfirmacionDespacho(req.getClaveSilo(),
				req.getClaveMaterial(),req.getTipoMovimiento(),req.getNumBoleta(),pesoNeto.toString(),req.getDestinoId().toString() , "");*/
	}

}
