package com.bachuco.persistence.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.bachuco.model.ProgramArriboRequest;
import com.bachuco.persistence.repository.CatalogJdbcRepository;
import com.bachuco.persistence.repository.ProgramArriboJdbcRepository;
import com.bachuco.port.ProgramArriboRepositoryPort;
import com.bachuco.port.ProgramArriboSapRepositoryPort;

@Component
public class ProgramArriboRepositoryAdapter implements ProgramArriboRepositoryPort{

	private final ProgramArriboSapRepositoryPort programArriboSapRepositoryPor;
	private final ProgramArriboJdbcRepository programArriboJdbcRepository;
	private final CatalogJdbcRepository catalogJdbcRepository;
	private final String urlPedCompra="http://bopaxdev01.bachoco.net:50000/RESTAdapter/portal-cosecha";
	
	public ProgramArriboRepositoryAdapter(ProgramArriboSapRepositoryPort programArriboSapRepositoryPor,
			ProgramArriboJdbcRepository programArriboJdbcRepository, CatalogJdbcRepository catalogJdbcRepository) {
		this.programArriboSapRepositoryPor = programArriboSapRepositoryPor;
		this.programArriboJdbcRepository = programArriboJdbcRepository;
		this.catalogJdbcRepository = catalogJdbcRepository;
	}

	@Override
	public Double stockSilo(String claveSilo) {
		// TODO Auto-generated method stub
		Double response=this.programArriboSapRepositoryPor.stockSilo(claveSilo, urlPedCompra);
		if(response!=null) {
			this.catalogJdbcRepository.resetStockSilo(String.valueOf(response), claveSilo,1);
		}
		return response;
	}

	@Override
	public String saveProgramArrivo(List<ProgramArriboRequest> arribos) {
		return this.programArriboJdbcRepository.saveProgramArribo(arribos);
	}

}
