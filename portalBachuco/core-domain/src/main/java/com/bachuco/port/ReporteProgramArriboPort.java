package com.bachuco.port;

import java.util.List;

import com.bachuco.model.ReportePorgramArribo;

public interface ReporteProgramArriboPort {

	public List<ReportePorgramArribo> findAllFilters(Integer siloId,String fechaI, String fechaF);;
}
