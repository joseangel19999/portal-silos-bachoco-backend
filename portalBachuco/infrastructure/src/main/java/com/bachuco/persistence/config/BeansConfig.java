package com.bachuco.persistence.config;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bachuco.model.Silo;
import com.bachuco.model.service.DocumentoRepositoryPort;
import com.bachuco.port.BodegaRepositoryPort;
import com.bachuco.port.BodegaSiloRepositoryPort;
import com.bachuco.port.CategoriaRepositoryPort;
import com.bachuco.port.CrudGenericRepositoryPort;
import com.bachuco.port.EmpleadoExternoRepositoryPort;
import com.bachuco.port.EmpleadoInternoRepositoryPort;
import com.bachuco.port.EmpleadoRepositoryPort;
import com.bachuco.port.MaterialRepositoryPort;
import com.bachuco.port.PedidoCompraJdbcRepositoryPort;
import com.bachuco.port.PedidoCompraRepositoryPort;
import com.bachuco.port.PedidoTrasladoJdbcRepositoryPort;
import com.bachuco.port.PerfilRepositoryPort;
import com.bachuco.port.PlantaRepositoryPort;
import com.bachuco.port.ReporteDespachosPort;
import com.bachuco.port.ReporteProgramArriboPort;
import com.bachuco.port.SiloRepositoryPort;
import com.bachuco.port.UsuarioRepositoryPort;
import com.bachuco.service.usecase.BodegaUsecase;
import com.bachuco.service.usecase.EmpleadoExternoUseCase;
import com.bachuco.service.usecase.EmpleadoUseCase;
import com.bachuco.service.usecase.MaterialUseCase;
import com.bachuco.service.usecase.PedidoCompraUsecase;
import com.bachuco.service.usecase.PedidoTrasladoJdbcUseCase;
import com.bachuco.service.usecase.PlantaUseCase;
import com.bachuco.service.usecase.ReporteDespachoUseCase;
import com.bachuco.service.usecase.ReporteProgramArriboUseCase;
import com.bachuco.service.usecase.SiloUseCase;
import com.bachuco.service.usecase.UsuarioUseCase;

@Configuration
public class BeansConfig {

	@Bean
	public MaterialUseCase createMaterialUseCase(MaterialRepositoryPort materialRepositoryPort,CategoriaRepositoryPort categoriaRepositoryPort) {
		return new MaterialUseCase(materialRepositoryPort,categoriaRepositoryPort);
	}
	
	/*@Bean
	public SiloUseCase siloUsecase(@Qualifier("siloJpaAdapter") CrudGenericRepositoryPort<Silo,Integer> siloCrudGenericRepositoryPort) {
		return new SiloUseCase(siloCrudGenericRepositoryPort);
	}*/
	
	@Bean
	public SiloUseCase siloUsecase(SiloRepositoryPort siloRepositoryPort) {
		return new SiloUseCase(siloRepositoryPort);
	}
	
	
	@Bean
	public BodegaUsecase bodegaSiloCaseUse(BodegaRepositoryPort bodegaRepositoryPort,
			SiloRepositoryPort siloRepositoryPort) {
		return new BodegaUsecase(bodegaRepositoryPort,siloRepositoryPort);
	}
	
	@Bean
	public PlantaUseCase plantaUseCase(PlantaRepositoryPort plantaRepositoryPort) {
		return new PlantaUseCase(plantaRepositoryPort);
	}
	
	@Bean
	public EmpleadoUseCase empleadoUseCase(UsuarioRepositoryPort usuarioRepositoryPort,EmpleadoRepositoryPort empleadoRepositoryPort,
			 EmpleadoInternoRepositoryPort empleadoInternoRepositoryPort,PerfilRepositoryPort perfilRepositoryPort) {
		return new EmpleadoUseCase(usuarioRepositoryPort, empleadoRepositoryPort,empleadoInternoRepositoryPort,perfilRepositoryPort);
	}
	
	@Bean
	public UsuarioUseCase usuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort) {
		return new UsuarioUseCase(usuarioRepositoryPort);
	}
	
	@Bean
	public PedidoCompraUsecase pedidoCompraUsecase(PedidoCompraJdbcRepositoryPort pedidoCompraRepositoryPort,DocumentoRepositoryPort documentoRepositoryPort) {
		return new PedidoCompraUsecase(pedidoCompraRepositoryPort,documentoRepositoryPort);
	}
	
	@Bean
	public PedidoTrasladoJdbcUseCase pedidoTrasladoJdbcUseCase(PedidoTrasladoJdbcRepositoryPort pedidoTrasladoJdbcRepositoryPort) {
		return new PedidoTrasladoJdbcUseCase(pedidoTrasladoJdbcRepositoryPort);
	}
	
	@Bean
	public ReporteProgramArriboUseCase reporteProgramArriboUseCase( ReporteProgramArriboPort programArriboPort) {
		return new ReporteProgramArriboUseCase(programArriboPort);
	}
	
	@Bean
	public ReporteDespachoUseCase reporteDespachoUseCase(ReporteDespachosPort despachosPort) {
		return new ReporteDespachoUseCase(despachosPort);
	}
	
	@Bean
	public EmpleadoExternoUseCase empleadoExternoUseCase(EmpleadoExternoRepositoryPort empExternoRepositoryPort) {
		return new EmpleadoExternoUseCase(empExternoRepositoryPort);
	}
	
}
