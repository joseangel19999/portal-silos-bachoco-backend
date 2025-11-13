package com.bachoco.persistence.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.bachoco.exception.NotFoundMaterialException;
import com.bachoco.exception.NotFoundPedCompraException;
import com.bachoco.exception.NotFoundPlantaDestinoException;
import com.bachoco.exception.RegistroNoCreadoException;
import com.bachoco.model.PedidoTrasladoSapResponseDTO;
import com.bachoco.model.procedores.PedidoTrasladoArriboDTO;
import com.bachoco.model.procedores.PedidoTrasladoDTO;
import com.bachoco.persistence.config.SapProperties;
import com.bachoco.persistence.entity.MaterialEntity;
import com.bachoco.persistence.entity.PlantaEntity;
import com.bachoco.persistence.repository.CatalogJdbcRepository;
import com.bachoco.persistence.repository.MateriaJpalRepository;
import com.bachoco.persistence.repository.PedidoTrasladoJdbcRepository;
import com.bachoco.persistence.repository.PlantaJpaRepository;
import com.bachoco.port.PedidoTrasladoJdbcRepositoryPort;
import com.bachoco.secutiry.utils.BatchUtils;
import com.bachoco.service.ActualizacionTrasladosService;

@Repository
public class PedidoTrasladoJdbcRepositoryAdapter implements PedidoTrasladoJdbcRepositoryPort {

	private final PedidoTrasladoJdbcRepository pedidoTrasladoJdbcRepository;
	private final PedidoTrasladoSapWebClientoAdapter pedidoTrasladoSapWebClientoAdapter;
	private final CatalogJdbcRepository catalogJdbcRepository;
	private final PlantaJpaRepository plantaJpaRepository;
	private final MateriaJpalRepository materiaJpalRepository;
	private final ActualizacionTrasladosService actualizacionTrasladosService;
	private final SapProperties sapProperties;
	

	public PedidoTrasladoJdbcRepositoryAdapter(PedidoTrasladoJdbcRepository pedidoTrasladoJdbcRepository,
			PedidoTrasladoSapWebClientoAdapter pedidoTrasladoSapWebClientoAdapter,
			CatalogJdbcRepository catalogJdbcRepository, PlantaJpaRepository plantaJpaRepository,
			MateriaJpalRepository materiaJpalRepository, SapProperties sapProperties,
			ActualizacionTrasladosService actualizacionTrasladosService) {
		this.pedidoTrasladoJdbcRepository = pedidoTrasladoJdbcRepository;
		this.pedidoTrasladoSapWebClientoAdapter = pedidoTrasladoSapWebClientoAdapter;
		this.catalogJdbcRepository = catalogJdbcRepository;
		this.plantaJpaRepository = plantaJpaRepository;
		this.materiaJpalRepository = materiaJpalRepository;
		this.sapProperties = sapProperties;
		this.actualizacionTrasladosService=actualizacionTrasladosService;
	}

	@Override
	public List<PedidoTrasladoDTO> findByFilterSiloAndMaterialAnFecha(String claveSilo,String claveMaterial,
			String plantaDestino,String fechaInicio, String fechaFin) {
		List<PedidoTrasladoDTO> response=new ArrayList<>();
		List<String> foliosExists=new ArrayList<>();
		List<String> materialesSap=new ArrayList<String>();
		List<String> plantasDestino=new ArrayList<String>();
		List<String> pedidoCompras=new ArrayList<String>();
		List<PedidoTrasladoSapResponseDTO> pedidosTrasladoSap= new ArrayList<>();
		try {
			List<PedidoTrasladoSapResponseDTO> pedCompraSap=this.pedidoTrasladoSapWebClientoAdapter.findAllPedTraslado(claveSilo,claveMaterial,plantaDestino,toReplace(fechaInicio),toReplace(fechaFin),sapProperties.getUrl());
			List<PedidoTrasladoSapResponseDTO> pedidosSap = new ArrayList<>(pedCompraSap);
			
			//validacion que existan las plantas que viene en los pedido traslado
			plantasDestino=pedidosSap.stream().map(pedido->pedido.getPlantaDestino())
					.filter(planta -> planta != null && !planta.trim().isEmpty())
					.collect(Collectors.toList());

			/*String resultValidacionPlantas=this.validateExistePlantaDestino(plantasDestino);
			if(!resultValidacionPlantas.equals("")) {
				throw  new NotFoundPlantaDestinoException(resultValidacionPlantas);
			}*/
			if(plantaDestino.trim().length()!=0) {
				pedidosSap.removeIf(pedidoC -> !pedidoC.getPlantaDestino().equals(plantaDestino.trim()));
			}else {
				Set<String> plantasNoexist=this.validateExistePlantaDestinoSet(plantasDestino);
				if(plantasNoexist.size()>0) {
					for (String elemento : plantasNoexist) {
					    System.out.println(elemento);
						pedidosSap.removeIf(pedidoC -> plantasNoexist.contains(pedidoC.getPlantaDestino()));
					}
				}
			}
			//validacion que existan las plantas que viene en los pedido traslado
			materialesSap=pedidosSap.stream().map(pedido->pedido.getMaterial())
					.filter(material -> material != null && !material.trim().isEmpty())
					.collect(Collectors.toList());
			String resultValidacionMateriales=this.validateExisteMateriales(materialesSap);
			if(!resultValidacionMateriales.equals("")) {
				throw  new NotFoundMaterialException(resultValidacionMateriales);
			}
			
			//validacion que existan los pedido traslado que este asociado a pedido compra
			pedidoCompras = pedidosSap.stream()
				    .map(pedido -> {
				        String posicion = pedido.getPosicion();
				        if (pedido.getPedidoDeComprasAsociado() == null ||
				            (posicion == null || posicion.trim().isEmpty())) {
				            return null;
				        }
				        return buildFolioPedCompraAnPosicion(String.valueOf(pedido.getPedidoDeComprasAsociado()), posicion);
				    })
				    .filter(result -> result != null && !result.trim().isEmpty())
				    .collect(Collectors.toList());
			List<String> resultValidacionPedidoCompras=this.validateExistePedidoCompra(pedidoCompras);
			
			//se remueven los pedido traslado que no tienen pedido compra asociado
			pedidosSap.removeIf(pedido -> {
			    return pedido.getPedidoDeComprasAsociado() == null ||
			    		pedido.getPosicion() == null;
			});
			if(resultValidacionPedidoCompras.size()>0) {
				for(String value:resultValidacionPedidoCompras) {
					String[] parts=value.split("-");
						pedidosSap.removeIf(pedidoC -> String.valueOf(pedidoC.getPedidoDeComprasAsociado()).equals(parts[0])
								&& pedidoC.getPosicion().equals(parts[1]));
				}
			}
			if(pedidosSap.size()>0) {
				actualizacionTrasladosService.procesarActualizacionTraslados(pedidosSap);
				List<String> folios=pedidosSap.stream().map(s->s.getNumeroPedTraslado().trim().concat("-").concat(s.getPosicion().trim())).toList();
				List<List<String>> batches = BatchUtils.partition(folios, 100);
				for (List<String> batch : batches) {
					foliosExists.addAll(this.pedidoTrasladoJdbcRepository.findAllFoliosPedTrasladoExist(batch));
				}
				Set<String> foliosExist = new HashSet<>(foliosExists);
				List<PedidoTrasladoSapResponseDTO> pedidosSapNoExistBd=pedidosSap.stream().filter(p->!foliosExist.contains(folio(p.getNumeroPedTraslado(),p.getPosicion()))).toList();
				List<String> foliosNoExistSap=pedidosSapNoExistBd.stream().map(p->buildFolioPedCompraAnPosicion(p.getNumeroPedTraslado(),p.getPosicion())).toList();
				if(pedidosSapNoExistBd.size()>0){
					this.pedidoTrasladoJdbcRepository.savePedidoTraslado(pedidosSapNoExistBd,claveSilo);
					foliosExists.addAll(foliosNoExistSap);
					List<List<String>> batchesFolios = BatchUtils.partition(foliosExists, 100);
					for (List<String> batch : batchesFolios) {
						response.addAll(this.pedidoTrasladoJdbcRepository.findAllByFolioNumCompra(batch));
					}
					return response;
				}else {
					for (List<String> batch : batches) {
						response.addAll(this.pedidoTrasladoJdbcRepository.findAllByFolioNumCompra(batch));
					}
				}
			}
		}catch (NotFoundPlantaDestinoException ex) {
			throw ex;
		}catch(NotFoundMaterialException ex) {
			throw ex;
		}catch(NotFoundPedCompraException ex) {
			throw ex;
		}catch (Exception e) {
			e.printStackTrace();
			throw  new RegistroNoCreadoException(e.getMessage());
		}
		return response;
	}
	
	@Override
	public void executeDowloadPedTrasladoBySap(String claveSilo, String claveMaterial,String plantaDestino, String fechaInicio,
			String fechaFin) {
		List<String> foliosExists=new ArrayList<>();
		try {
			List<PedidoTrasladoSapResponseDTO> pedidosSap=this.pedidoTrasladoSapWebClientoAdapter.findAllPedTraslado(claveSilo,claveMaterial,plantaDestino,toReplace(fechaInicio),toReplace(fechaFin),sapProperties.getUrl());
			if(pedidosSap.size()>0) {
				List<String> folios=pedidosSap.stream().map(s->s.getNumeroPedTraslado().trim().concat("-").concat(s.getPosicion().trim())).toList();
				List<List<String>> batches = BatchUtils.partition(folios, 100);
				for (List<String> batch : batches) {
					foliosExists.addAll(this.pedidoTrasladoJdbcRepository.findAllFoliosPedTrasladoExist(batch));
				}
				Set<String> foliosExist = new HashSet<>(foliosExists);
				List<PedidoTrasladoSapResponseDTO> pedidosSapNoExistBd=pedidosSap.stream().filter(p->!foliosExist.contains(folio(p.getNumeroPedTraslado(),p.getPosicion()))).toList();
				if(pedidosSapNoExistBd.size()>0){
					this.pedidoTrasladoJdbcRepository.savePedidoTraslado(pedidosSapNoExistBd,claveSilo);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw  new RegistroNoCreadoException(e.getMessage());
		}
	}
	
	private String folio(String numPedTraslado,String posicion) {
		String folio=numPedTraslado.trim().concat("-").concat(posicion.trim());
		return folio;
	}

	@Override
	public List<PedidoTrasladoArriboDTO> findByFilterProgramArribo(Integer siloId, String planta,Integer materialId) {
		return this.pedidoTrasladoJdbcRepository.obtenerPedidosTrasladoParaArribo(siloId,planta, materialId);
	}
	
	@Override
	public List<PedidoTrasladoArriboDTO> findByPedTrasladoByConfDespacho(Integer siloId,
			Integer materialId,String fechaInicio,String fechaFin) {
		return this.pedidoTrasladoJdbcRepository.findByPedidosTrasladoParaConfDespacho(siloId, materialId,fechaInicio,fechaFin);
	}
	@Override
	public List<PedidoTrasladoDTO> findByFiltersCantidadDisponible(Integer siloId,Integer materialId,
			String fechaInicio, String fechaFin) {
		return pedidoTrasladoJdbcRepository.obtenerPedidosFiltradosCantidadDisponible(siloId,materialId,fechaInicio,fechaFin);
	}
	public String folioPedTraslado(String numPed,String posicion) {
		return numPed+"-"+posicion;
	}
	private String toReplace(String value) {
		return value.replace("-", "");
	}
	private String buildFolioPedCompraAnPosicion(String pedCompra,String posicion) {
		return pedCompra.trim().concat("-").concat(posicion.trim());
	}
	
	private String validateExistePlantaDestino(List<String> plantas) {
		// 1. Obtiene las plantas de SAP y las convierte a un Set de nombres (ya lo tienes).
		Set<String> plantasSap = new HashSet<>(plantas);
		// 2. Obtiene TODAS las plantas de la DB (JpaRepository)
		List<PlantaEntity> resultDB = this.plantaJpaRepository.findAll();
		// 3. ¡Nuevo Set! Crea un Set de NOMBRES de las plantas que ya existen en la DB.
		// Esto es el corazón de la eficiencia para la comparación.
		Set<String> nombresPlantaDB = resultDB.stream()
		    .map(PlantaEntity::getPlanta)
		    .collect(Collectors.toSet());
		// 4. Filtra el Set de SAP para encontrar cuáles NO existen en el Set de la DB.
		// El stream se ejecuta sobre el Set de SAP (plantasSap).
		Set<String> plantasNoExisten = plantasSap.stream()
		    .filter(nombre -> !nombresPlantaDB.contains(nombre))
		    .collect(Collectors.toSet());
		if(plantasNoExisten.size()>0) {
			return plantasNoExisten.stream().collect(Collectors.joining(","));
		}else {
			return "";
		}
	}

	private Set<String> validateExistePlantaDestinoSet(List<String> plantas) {
		// 1. Obtiene las plantas de SAP y las convierte a un Set de nombres (ya lo tienes).
		Set<String> plantasSap = new HashSet<>(plantas);
		// 2. Obtiene TODAS las plantas de la DB (JpaRepository)
		List<PlantaEntity> resultDB = this.plantaJpaRepository.findAll();
		// 3. ¡Nuevo Set! Crea un Set de NOMBRES de las plantas que ya existen en la DB.
		// Esto es el corazón de la eficiencia para la comparación.
		Set<String> nombresPlantaDB = resultDB.stream()
		    .map(PlantaEntity::getPlanta)
		    .collect(Collectors.toSet());
		// 4. Filtra el Set de SAP para encontrar cuáles NO existen en el Set de la DB.
		// El stream se ejecuta sobre el Set de SAP (plantasSap).
		Set<String> plantasNoExisten = plantasSap.stream()
		    .filter(nombre -> !nombresPlantaDB.contains(nombre))
		    .collect(Collectors.toSet());
		return plantasNoExisten;
	}
	
	private String validateExisteMateriales(List<String> materiales) {
		// 1. Obtiene las plantas de SAP y las convierte a un Set de nombres (ya lo tienes).
		Set<String> materialesSap = new HashSet<>(materiales);
		// 2. Obtiene TODAS las plantas de la DB (JpaRepository)
		List<MaterialEntity> resultDB = this.materiaJpalRepository.findAll();
		// 3. ¡Nuevo Set! Crea un Set de NOMBRES de las plantas que ya existen en la DB.
		// Esto es el corazón de la eficiencia para la comparación.
		Set<String> numMaterialDB = resultDB.stream()
		    .map(MaterialEntity::getNumero)
		    .collect(Collectors.toSet());
		// 4. Filtra el Set de SAP para encontrar cuáles NO existen en el Set de la DB.
		// El stream se ejecuta sobre el Set de SAP (plantasSap).
		Set<String> materialesNoExisten = materialesSap.stream()
		    .filter(nombre -> !numMaterialDB.contains(nombre))
		    .collect(Collectors.toSet());
		if(materialesNoExisten.size()>0) {
			return materialesNoExisten.stream().collect(Collectors.joining(","));
		}else {
			return "";
		}
	}
	
	private List<String> validateExisteMaterialeslIST(List<String> materiales) {
		// 1. Obtiene las plantas de SAP y las convierte a un Set de nombres (ya lo tienes).
		Set<String> materialesSap = new HashSet<>(materiales);
		// 2. Obtiene TODAS las plantas de la DB (JpaRepository)
		List<MaterialEntity> resultDB = this.materiaJpalRepository.findAll();
		// 3. ¡Nuevo Set! Crea un Set de NOMBRES de las plantas que ya existen en la DB.
		// Esto es el corazón de la eficiencia para la comparación.
		Set<String> numMaterialDB = resultDB.stream()
		    .map(MaterialEntity::getNumero)
		    .collect(Collectors.toSet());
		// 4. Filtra el Set de SAP para encontrar cuáles NO existen en el Set de la DB.
		// El stream se ejecuta sobre el Set de SAP (plantasSap).
		Set<String> materialesNoExisten = materialesSap.stream()
		    .filter(nombre -> !numMaterialDB.contains(nombre))
		    .collect(Collectors.toSet());
		return new ArrayList<>(materialesNoExisten);
	}
	
	private List<String> validateExistePedidoCompra(List<String> pedCompraAsociado) {
		// 1. Obtiene las plantas de SAP y las convierte a un Set de nombres (ya lo tienes).
		if(pedCompraAsociado.size()==0) return Collections.EMPTY_LIST;
		Set<String> pedCompraAsociadoSap = new HashSet<>(pedCompraAsociado);
		// 2. Obtiene TODAS las plantas de la DB (JpaRepository)
		List<String> resultDB = this.catalogJdbcRepository.findAllClavePedCompra();
		// 3. ¡Nuevo Set! Crea un Set de NOMBRES de las plantas que ya existen en la DB.
		// Esto es el corazón de la eficiencia para la comparación.
		Set<String> pedCompraDB = new HashSet<>(resultDB);
		// 4. Filtra el Set de SAP para encontrar cuáles NO existen en el Set de la DB.
		// El stream se ejecuta sobre el Set de SAP (plantasSap).
		Set<String> pedCompraNoExisten = pedCompraAsociadoSap.stream()
		    .filter(nombre -> !pedCompraDB.contains(nombre))
		    .collect(Collectors.toSet());
		return pedCompraNoExisten.stream().collect(Collectors.toList());
	}

}
