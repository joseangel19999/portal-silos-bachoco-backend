package com.bachoco.persistence.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bachoco.model.ProgramArriboRequest;
import com.bachoco.persistence.config.SapProperties;
import com.bachoco.persistence.entity.SiloEntity;
import com.bachoco.persistence.repository.CatalogJdbcRepository;
import com.bachoco.persistence.repository.ProgramArriboJdbcRepository;
import com.bachoco.persistence.repository.SiloJpaRepository;
import com.bachoco.port.ProgramArriboRepositoryPort;
import com.bachoco.port.ProgramArriboSapRepositoryPort;

@Component
public class ProgramArriboRepositoryAdapter implements ProgramArriboRepositoryPort{

	private final ProgramArriboSapRepositoryPort programArriboSapRepositoryPor;
	private final ProgramArriboJdbcRepository programArriboJdbcRepository;
	private final CatalogJdbcRepository catalogJdbcRepository;
	private final SiloJpaRepository siloJpaRepository;
	private final SapProperties sapProperties;

	public ProgramArriboRepositoryAdapter(ProgramArriboSapRepositoryPort programArriboSapRepositoryPor,
			ProgramArriboJdbcRepository programArriboJdbcRepository, CatalogJdbcRepository catalogJdbcRepository,
			SiloJpaRepository siloJpaRepository, SapProperties sapProperties) {
		this.programArriboSapRepositoryPor = programArriboSapRepositoryPor;
		this.programArriboJdbcRepository = programArriboJdbcRepository;
		this.catalogJdbcRepository = catalogJdbcRepository;
		this.siloJpaRepository = siloJpaRepository;
		this.sapProperties = sapProperties;
	}

	@Override
	public Double stockSilo(String claveSilo,String material) {
		Double response=this.programArriboSapRepositoryPor.stockSilo(claveSilo,material, sapProperties.getUrl());
		//Double response=2000D;
		if(response!=null) {
			this.catalogJdbcRepository.resetStockSilo(String.valueOf(response), claveSilo,1);
			return response;
		}else {
			this.catalogJdbcRepository.resetStockSilo(String.valueOf("0"), claveSilo,1);
			return 0.0D;
		}
	}

	@Override
	public String saveProgramArrivo(List<ProgramArriboRequest> arribos) {
		return this.programArriboJdbcRepository.saveProgramArribo(arribos);
	}

}
