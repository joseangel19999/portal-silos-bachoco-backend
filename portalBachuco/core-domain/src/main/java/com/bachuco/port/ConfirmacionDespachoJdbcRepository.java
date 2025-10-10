package com.bachuco.port;

import com.bachuco.model.ConfirmacionDespachoRequest;

public interface ConfirmacionDespachoJdbcRepository {

	public void save(ConfirmacionDespachoRequest req);
}
