package com.bachuco.service.usecase;

import com.bachuco.model.ConfDespachoPesosRequest;
import com.bachuco.model.ConfirmacionDespachoRequest;
import com.bachuco.model.ConfirmacionDespachoResponse;
import com.bachuco.port.ConfirmacionDespachoJdbcRepository;

public class ConfirmacionDespachoUseCase {

	private final ConfirmacionDespachoJdbcRepository confDespachoJdbcRepository;

	public ConfirmacionDespachoUseCase(ConfirmacionDespachoJdbcRepository confDespachoJdbcRepository) {
		this.confDespachoJdbcRepository = confDespachoJdbcRepository;
	}
	
	public ConfirmacionDespachoResponse save(ConfirmacionDespachoRequest req) {
		return this.confDespachoJdbcRepository.save(req);
	}
	public ConfirmacionDespachoResponse updateSap(ConfirmacionDespachoRequest req) {
		return this.confDespachoJdbcRepository.updateSap(req);
	}
	public ConfirmacionDespachoResponse updateSinSap(ConfirmacionDespachoRequest req) {
		return this.confDespachoJdbcRepository.updateBd(req);
	}
	public ConfirmacionDespachoResponse esIgualLosPesosPorId(ConfDespachoPesosRequest req) {
		return this.confDespachoJdbcRepository.esIgualPesosPorId(req);
	}
}
