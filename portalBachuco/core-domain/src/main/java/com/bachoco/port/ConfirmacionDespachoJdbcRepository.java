package com.bachoco.port;

import com.bachoco.model.ConfDespachoPesosRequest;
import com.bachoco.model.ConfirmacionDespachoRequest;
import com.bachoco.model.ConfirmacionDespachoResponse;

public interface ConfirmacionDespachoJdbcRepository {

	public ConfirmacionDespachoResponse save(ConfirmacionDespachoRequest req);
	public ConfirmacionDespachoResponse updateBd(ConfirmacionDespachoRequest req);
	public ConfirmacionDespachoResponse updateSap(ConfirmacionDespachoRequest req);
	public ConfirmacionDespachoResponse esIgualPesosPorId(ConfDespachoPesosRequest req);
	public Float findCantidadPromediotrasporte(Integer siloId);
}
