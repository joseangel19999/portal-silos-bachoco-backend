package com.bachuco.port;

import com.bachuco.model.ConfDespachoPesosRequest;
import com.bachuco.model.ConfirmacionDespachoRequest;
import com.bachuco.model.ConfirmacionDespachoResponse;

public interface ConfirmacionDespachoJdbcRepository {

	public ConfirmacionDespachoResponse save(ConfirmacionDespachoRequest req);
	public ConfirmacionDespachoResponse updateBd(ConfirmacionDespachoRequest req);
	public ConfirmacionDespachoResponse updateSap(ConfirmacionDespachoRequest req);
	public ConfirmacionDespachoResponse esIgualPesosPorId(ConfDespachoPesosRequest req);
}
