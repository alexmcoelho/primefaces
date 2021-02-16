package com.alex.controle;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.DAO.DetalhesContaAPagarDAO;
import com.alex.modelo.DetalhesContaAPagar;
import com.alex.modelo.SemInformacoes;
import com.alex.relatorios.GeradorRelatorio;

import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class ContasAPagarRelatorioMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private DetalhesContaAPagarDAO detalhesContaAPagarDAO;
	
	List<DetalhesContaAPagar> listaDetalhesContaAPagar = new ArrayList<>();
	
	List<SemInformacoes> semInformacoes = new ArrayList<>();
	
	private String tipoRelatorio;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	private Long codigo;
	
	private boolean contasPagas;
	
	@PostConstruct
	public void init() {		
		tipoRelatorio = "numero_entrada";
		contasPagas = false;
	}
	
	public void gerarRelatorio() {
		//filtra
		if(tipoRelatorio.equalsIgnoreCase("todos")) {
			listaDetalhesContaAPagar = detalhesContaAPagarDAO.filtraDetalheContaPorSituacao(contasPagas);
		} else if(tipoRelatorio.equalsIgnoreCase("data")) {
			listaDetalhesContaAPagar = detalhesContaAPagarDAO.intervaloEntreDatas(contasPagas, dataInicio, dataFim);
		} else if(tipoRelatorio.equalsIgnoreCase("numero_entrada")) {
			listaDetalhesContaAPagar = detalhesContaAPagarDAO.filtraFaturaPorIdEntradaSituacao(contasPagas, codigo);
		}
		try {
	        String filename = "ContasAPagar.pdf";
	        String jasperPath = "";
			if(listaDetalhesContaAPagar.size() > 0) {	        
		        jasperPath = "/resources/relatorios/contas_a_pagar.jasper";
		        GeradorRelatorio.PDF(null, jasperPath, listaDetalhesContaAPagar, filename);
			}else {
				jasperPath = "/resources/relatorios/sem_informacoes.jasper";
				semInformacoes.add(new SemInformacoes("Contas à pagar", "Relatório de contas não quitadas. Todas relacionadas a uma entrada de produtos.", "Nenhum registro foi encontrado.", contasPagas));
				GeradorRelatorio.PDF(null, jasperPath, semInformacoes, filename);
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

	public boolean isContasPagas() {
		return contasPagas;
	}

	public void setContasPagas(boolean contasPagas) {
		this.contasPagas = contasPagas;
	}	
}
