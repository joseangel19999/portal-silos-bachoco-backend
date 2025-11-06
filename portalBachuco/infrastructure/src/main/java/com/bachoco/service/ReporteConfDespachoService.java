package com.bachoco.service;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import com.bachoco.dto.BoletaDTO;
import com.bachoco.dto.BoletaSalida;
import com.bachoco.dto.Cliente;
import com.bachoco.model.ReportConfDespacho;
import com.bachoco.persistence.repository.ConfDespachoJdbcRepository;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class ReporteConfDespachoService {
	
    @Autowired
    private org.thymeleaf.TemplateEngine templateEngine;
	private final ConfDespachoJdbcRepository confDespachoJdbcRepository;
    
    public ReporteConfDespachoService(ConfDespachoJdbcRepository confDespachoJdbcRepository) {
		this.confDespachoJdbcRepository = confDespachoJdbcRepository;
	}
    

	public ByteArrayOutputStream reporteConfDespacho() throws FileNotFoundException, MalformedURLException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// PdfWriter writer = new PdfWriter(new FileOutputStream("reporte.pdf"));
		PdfWriter writer = new PdfWriter(out);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);

		// Título
		Paragraph titulo = new Paragraph("Reporte Confirmacion Despacho").setFontSize(20).setBold()
				.setTextAlignment(TextAlignment.CENTER).setBackgroundColor(ColorConstants.LIGHT_GRAY)
				.setMarginBottom(20);
		document.add(titulo);

		// Imagen (logo)
		Image img = new Image(com.itextpdf.io.image.ImageDataFactory.create("src/main/resources/logo.png")).setWidth(150)
				.setHeight(80).setHorizontalAlignment(HorizontalAlignment.LEFT);
		document.add(img);

		// Tabla con datos
		float[] columnas = { 100f, 200f, 100f,100f, 200f, 100f,100f };
		Table table = new Table(columnas);
		table.setWidth(UnitValue.createPercentValue(100));

		// Encabezado
		table.addHeaderCell(new Cell().add(new Paragraph("Numero SAP")).setBackgroundColor(ColorConstants.GRAY));
		table.addHeaderCell(new Cell().add(new Paragraph("Numero Pedido Traslaldo")).setBackgroundColor(ColorConstants.GRAY));
		table.addHeaderCell(new Cell().add(new Paragraph("Cantidad Toneladas")).setBackgroundColor(ColorConstants.GRAY));
		table.addHeaderCell(new Cell().add(new Paragraph("Pedido Compra asociado")).setBackgroundColor(ColorConstants.GRAY));
		table.addHeaderCell(new Cell().add(new Paragraph("Chofer")).setBackgroundColor(ColorConstants.GRAY));
		table.addHeaderCell(new Cell().add(new Paragraph("Licencia Transportista")).setBackgroundColor(ColorConstants.GRAY));
		table.addHeaderCell(new Cell().add(new Paragraph("Placa Jaula")).setBackgroundColor(ColorConstants.GRAY));

		// Filas
		table.addCell("202510151001");
		table.addCell("4500256298");
		table.addCell("1,000");
		table.addCell("4500256221");
		table.addCell("Ricardo Salazar Perez");
		table.addCell("LCI1002901");
		table.addCell("SALTILLO-10992");

		document.add(table);

		// Pie de página
		Paragraph footer = new Paragraph("Generado automáticamente por el Sistema Portal de proveedores silo")
				.setFontSize(10).setTextAlignment(TextAlignment.CENTER).setMarginTop(50);
		document.add(footer);

		document.close();
		return out;
	}
	
	
	public ByteArrayOutputStream reporteConfDespachoTymeleaf() throws IOException {

        // 🧾 1. Datos del reporte
        BoletaDTO boleta = new BoletaDTO(
                "161",
                "2025-10-15",
                "GRANEROS CONTINENTAL",
                "JUAN ANTONIO MOZQUEDA",
                "21UPB8",
                "Observaciones del despacho..."
        );

        // 🧩 2. Renderiza el HTML con Thymeleaf
        Context context = new Context();
        context.setVariable("boleta", boleta);
        String html = templateEngine.process("boleta.html", context);

        // 📄 3. Genera el PDF a memoria
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.withHtmlContent(html, null);
        builder.toStream(baos);
        builder.run();

        // 📦 4. Retorna el PDF como recurso descargable
       // ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());
        return baos;
	}

	
	public BoletaSalida obtenerDatosBoletaByQuery(ReportConfDespacho conf) {
        // En una aplicación real, aquí consultarías una base de datos.
        // Por simplicidad, simularemos los datos de tu imagen:
        Cliente proveedor = new Cliente(conf.getSilo(),
                "SENDERO NACIONAL KM. 1 S/N, COL. H. MATAMOROS, TAMAULIPAS",
                "GCO070803V61", "8688166509");
        Cliente cliente = new Cliente(conf.getSilo(),
                "AV. TECNOLÓGICO 401, COL. ZONA INDUSTRIAL CELAYA, GTO. C.P. 38010",
                "BAC800208B25", null);
        BoletaSalida boleta = new BoletaSalida();
        boleta.setProveedor(proveedor);
        boleta.setCliente(cliente);
        boleta.setNombreSilo(conf.getSilo());
        boleta.setNumeroBoleta(conf.getFolio());
        boleta.setFecha(conf.getFechaEmbarque());
        boleta.setHora("06:47:44 p.m.");
        boleta.setProducto(conf.getMaterial());
        boleta.setTransportista(conf.getLineaTransportista());
        boleta.setGuia(conf.getNumeroSap());
        boleta.setChofer(conf.getChofer());
        boleta.setNumeroTicketBascula(conf.getFolio());
        boleta.setObservaciones("DESTINO BACHOCO CELAYA GTO. NO. DE PEDIDO ".concat(conf.getNumPedTraslado()));
        boleta.setPedidoTraslado(conf.getNumPedTraslado());
        boleta.setPesoBruto(Double.parseDouble(conf.getPesoBruto()));
        boleta.setTara(Double.parseDouble(conf.getPesoTara()));
        boleta.setPesoNeto((Double.parseDouble(conf.getPesoBruto())-Double.parseDouble(conf.getPesoTara())));
        boleta.setHumedad(conf.getHumedad());
        return boleta;
    }
	
	public ByteArrayOutputStream generarPdfBoleta(Integer id) throws IOException {
		ReportConfDespacho reporte= this.confDespachoJdbcRepository.findConfDespachoById(id);
        BoletaSalida boleta = obtenerDatosBoletaByQuery(reporte);
        // 1. Crear el contexto para Thymeleaf
        Context context = new Context(new Locale("es", "MX"));
        context.setVariable("boleta", boleta);
        // 2. Procesar la plantilla HTML (src/main/resources/templates/boleta-salida.html)
        String htmlContent = templateEngine.process("boleta.html", context);
       // String baseUri = this.getClass().getClassLoader().getResource("/").toString();
        java.net.URL resourceUrl = this.getClass().getClassLoader().getResource("static");
        if (resourceUrl == null) {
           // Si la carpeta 'static' no se encuentra, algo está mal con la estructura del empaquetado.
           throw new IOException("No se pudo encontrar la carpeta 'static' en el classpath.");
        }
        String baseUri = resourceUrl.toString();
        // 3. Convertir HTML a PDF usando OpenHTMLToPDF
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
         // Usar el Base URI
            builder.withHtmlContent(htmlContent, baseUri);
            //builder.withHtmlContent(htmlContent, null); // El null es para la base URI, no necesario aquí.
            builder.toStream(os);
            builder.run();
            return os;
        }
    }
}
