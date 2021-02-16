package com.alex.controle;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.DAO.DetalhesFaturaDAO;
import com.alex.modelo.DetalhesFatura;
import com.alex.modelo.SemInformacoes;
import com.alex.relatorios.GeradorRelatorio;

import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class ContasAReceberRelatorioMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private DetalhesFaturaDAO detalhesFaturaDAO;
	
	List<DetalhesFatura> detalhesFaturas = new ArrayList<>();
	
	List<SemInformacoes> semInformacoes = new ArrayList<>();
	
	private String tipoRelatorio;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	private Long codigo;
	
	private boolean faturasBaixadas;
	
	private String tipoFatura;
	
	@PostConstruct
	public void init() {		
		tipoRelatorio = "todos";
		faturasBaixadas = false;
	}
	
	@Override
	public String getTitulo() {
		return "Emissão do relatório de contas à receber";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/relatorios/contas-a-receber-relatorio.jsf";
	}
	
	public void gerarRelatorio() {
		//filtra
		if (tipoRelatorio.equals("todos") && tipoFatura.equalsIgnoreCase("ordem")) {
			detalhesFaturas = detalhesFaturaDAO.filtraFaturaPorSituacaoMasPegaSoOrdem(faturasBaixadas);
		}
		if(tipoRelatorio.equals("todos") && tipoFatura.equalsIgnoreCase("venda")) {
			detalhesFaturas = detalhesFaturaDAO.filtraFaturaPorSituacaoMasPegaSoSaida(faturasBaixadas);
		}
		if(tipoRelatorio.equals("todos") && tipoFatura.equalsIgnoreCase("todos")) {
			detalhesFaturas = detalhesFaturaDAO.filtraDetalheFaturaPorSituacao(faturasBaixadas);
		}
		
		if (tipoRelatorio.equals("numero") && codigo != null && tipoFatura.equalsIgnoreCase("ordem")) {
			detalhesFaturas = detalhesFaturaDAO.filtraFaturaPorIdOrdemAndSituacao(codigo, faturasBaixadas);
		} else if(tipoRelatorio.equals("numero") && codigo != null && tipoFatura.equalsIgnoreCase("venda")) {
			detalhesFaturas = detalhesFaturaDAO.filtraFaturaPorIdSaidaAndSituacao(codigo, faturasBaixadas);
		} else if (tipoRelatorio.equals("data") && tipoFatura.equalsIgnoreCase("ordem")) {
			detalhesFaturas = detalhesFaturaDAO.intervaloEntreDatasPegaSoOrdem(faturasBaixadas, dataInicio, dataFim);
		} else if (tipoRelatorio.equals("data") && tipoFatura.equalsIgnoreCase("venda")) {
			detalhesFaturas = detalhesFaturaDAO.intervaloEntreDatasPegaSoSaida(faturasBaixadas, dataInicio, dataFim);
		}
		try {
	        String filename = "ContasAReceber.pdf";
	        String jasperPath = "";
			if(detalhesFaturas.size() > 0) {	        
		        jasperPath = "/resources/relatorios/contas_a_receber.jasper";
		        GeradorRelatorio.PDF(null, jasperPath, detalhesFaturas, filename);
			}else {
				Map<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("titulo", "Contas à receber");
				parametros.put("subtitulo", "Relatório de faturas não quitadas.");
				jasperPath = "/resources/relatorios/sem_informacoes_parametros.jasper";
				semInformacoes.add(new SemInformacoes("", "", "Nenhum registro foi encontrado.", faturasBaixadas));
				GeradorRelatorio.PDF(parametros, jasperPath, semInformacoes, filename);
			}
		} catch (JRException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Long getCodigo() {
		return codigo;
	}

	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}

	public boolean isFaturasBaixadas() {
		return faturasBaixadas;
	}

	public void setFaturasBaixadas(boolean faturasBaixadas) {
		this.faturasBaixadas = faturasBaixadas;
	}

	public String getTipoFatura() {
		return tipoFatura;
	}

	public void setTipoFatura(String tipoFatura) {
		this.tipoFatura = tipoFatura;
	}

}
