package com.bachuco.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bachuco.model.ConfDespachoPesosRequest;
import com.bachuco.model.ConfirmacionDespachoRequest;
import com.bachuco.model.ConfirmacionDespachoResponse;
import com.bachuco.service.ReporteConfDespachoService;
import com.bachuco.service.usecase.ConfirmacionDespachoUseCase;

@RestController
@RequestMapping("/v1/confirmacion-despacho")
public class ConfirmacionDespachoController {
	
	private final ReporteConfDespachoService reporteConfDespachoService;
	private final ConfirmacionDespachoUseCase confirmacionDespachoUseCase;

	public ConfirmacionDespachoController(ReporteConfDespachoService reporteConfDespachoService,
			ConfirmacionDespachoUseCase confirmacionDespachoUseCase) {
		this.reporteConfDespachoService = reporteConfDespachoService;
		this.confirmacionDespachoUseCase = confirmacionDespachoUseCase;
	}

	@PostMapping
	public ResponseEntity<ConfirmacionDespachoResponse> save(@RequestBody ConfirmacionDespachoRequest req){
		ConfirmacionDespachoResponse response =this.confirmacionDespachoUseCase.save(req);
		return new ResponseEntity<ConfirmacionDespachoResponse>(response,HttpStatus.OK);
	}
	
	@PutMapping("/update-sap")
	public ResponseEntity<ConfirmacionDespachoResponse> updateSap(@RequestBody ConfirmacionDespachoRequest req){
		ConfirmacionDespachoResponse response =this.confirmacionDespachoUseCase.updateSap(req);
		return new ResponseEntity<ConfirmacionDespachoResponse>(response,HttpStatus.OK);
	}
	
	@PutMapping("/update-sin-sap")
	public ResponseEntity<ConfirmacionDespachoResponse> updateSinSap(@RequestBody ConfirmacionDespachoRequest req){
		ConfirmacionDespachoResponse response =this.confirmacionDespachoUseCase.updateSinSap(req);
		return new ResponseEntity<ConfirmacionDespachoResponse>(response,HttpStatus.OK);
	}
	
	@GetMapping("/filters-pesos")
	public ResponseEntity<ConfirmacionDespachoResponse> validatePesos(@RequestParam Integer id,
			@RequestParam Float pesobruto,@RequestParam Float pesotara){
		ConfDespachoPesosRequest req=new ConfDespachoPesosRequest();
		req.setIdconfDespacho(id);
		req.setPesoBruto(pesobruto);
		req.setPesoTara(pesotara);
		ConfirmacionDespachoResponse response =this.confirmacionDespachoUseCase.esIgualLosPesosPorId(req);
		return new ResponseEntity<ConfirmacionDespachoResponse>(response,HttpStatus.OK);
	}
	
	
	@GetMapping("/dowload-pdf/{id}")
    public ResponseEntity<Resource> descargarReporte(@PathVariable Integer id) throws IOException {
        try {
        	ByteArrayOutputStream data=this.reporteConfDespachoService.generarPdfBoleta(id);
             Resource resource = new ByteArrayResource(data.toByteArray());
            if (resource.exists() && resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "ReporteConfDespacho" + "\"");
                return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
