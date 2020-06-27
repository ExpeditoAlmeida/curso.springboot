package curso.springboot.controller;

import java.io.Serializable;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportUtil implements Serializable {

	public byte[] gerarRelatorio(List listDados, String relatorio, ServletContext servletContext) throws Exception {

		// Cria a lista de dados para relatorio com a lista de objetos
		JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(listDados);

		// Carrega o caminho do arquivo jasper compilado, relatorios é a pasta cuja
		// arquivo compilado está
		String caminhoJasper = servletContext.getRealPath("relatorios") + java.io.File.separator + relatorio
				+ ".jasper";

		// Carrega o arquivo jasper passado os dados
		JasperPrint impressoraJasper = JasperFillManager.fillReport(caminhoJasper, new HashedMap(), jrbcds);

		// Exporta para byte[] para fazer download do pdf
		return JasperExportManager.exportReportToPdf(impressoraJasper);
	}
}
