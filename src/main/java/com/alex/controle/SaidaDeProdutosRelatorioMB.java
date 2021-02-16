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

import com.alex.DAO.ItemProdSaidaDAO;
import com.alex.modelo.Cliente;
import com.alex.modelo.ItemProdSaida;
import com.alex.modelo.SaidaDeProdutos;
import com.alex.modelo.SemInformacoes;
import com.alex.relatorios.GeradorRelatorio;
import com.alex.servico.ServicoGenerico;

import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class SaidaDeProdutosRelatorioMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ItemProdSaidaDAO iItemProdSaidaDAO;

	@Inject
	private SaidaDeProdutos saidaDeProdutos;
	
	@Inject
	private Cliente cliente;
	
	private String tipoRelatorio;
	
	private String tipoPesquisa;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	private int faturas; //1 - Vendas sem faturas geradas, 2 - Vendas com faturas geradas, 3 - Todas  
	
	List<ItemProdSaida> itemProdSaidas = new ArrayList<ItemProdSaida>();
	
	List<SemInformacoes> semInformacoes = new ArrayList<>();

	@PostConstruct
	public void init() {		
		tipoRelatorio = "descricao";
		tipoPesquisa = "todos";
		faturas = 1;
	}
	
	@Override
	public String getTitulo() {
		return "Emissão do relatório de vendas";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/relatorios/detalhes-vendas-relatorio.jsf";
	}
	
	public void gerarRelatorio() {
		//todos
		if (tipoPesquisa.equals("todos") && faturas == 1) {
			itemProdSaidas = iItemProdSaidaDAO.itensProdSaidaSemFaturasGeradas();
		} else if (tipoPesquisa.equals("todos") && faturas == 2) {
			itemProdSaidas = iItemProdSaidaDAO.itensProdSaidaComFaturasGeradas();
		} else if (tipoPesquisa.equals("todos") && faturas == 3) {
			itemProdSaidas = iItemProdSaidaDAO.listAll();
		} 
		//número venda
		else if (tipoPesquisa.equals("numero_venda") && saidaDeProdutos.getId() != null && faturas == 1) {
			itemProdSaidas = iItemProdSaidaDAO.itensProdSaidaSemFaturasGeradasPorIdSaida(saidaDeProdutos.getId());
		} else if (tipoPesquisa.equals("numero_venda") && saidaDeProdutos.getId() != null && faturas == 2) {
			itemProdSaidas = iItemProdSaidaDAO.itensProdSaidaComFaturasGeradasPorIdSaida(saidaDeProdutos.getId());
		} else if (tipoPesquisa.equals("numero_venda") && saidaDeProdutos.getId() != null && faturas == 3) {
			itemProdSaidas = iItemProdSaidaDAO.listaFiltradoPorSaida(saidaDeProdutos.getId());
		}
		//nome cliente
		else if (tipoPesquisa.equals("nome") && cliente.getNome() != null && faturas == 1) {
			itemProdSaidas = iItemProdSaidaDAO.itensProdSaidaSemFaturasGeradasPorNomeCliente(ServicoGenerico.montaPesquisaInteligente(cliente.getNome(), true));
		} else if (tipoPesquisa.equals("nome") && cliente.getNome() != null && faturas == 2) {
			itemProdSaidas = iItemProdSaidaDAO.itensProdSaidaComFaturasGeradasPorNomeCliente(ServicoGenerico.montaPesquisaInteligente(cliente.getNome(), true));
		} else if (tipoPesquisa.equals("nome") && cliente.getNome() != null && faturas == 3) {
			itemProdSaidas = iItemProdSaidaDAO.listaFiltradoPorNomeCliente(ServicoGenerico.montaPesquisaInteligente(cliente.getNome(), true));
		}
		//cpf ou cnpj
		else if ((tipoPesquisa.equals("cpf") || tipoPesquisa.equals("cnpj")) && cliente.getCpfCnpj() != null && faturas == 1) {
			itemProdSaidas = iItemProdSaidaDAO.itensProdSaidaSemFaturasGeradasPorCpfOuCnpjCliente(cliente.getCpfCnpj());
		} else if ((tipoPesquisa.equals("cpf") || tipoPesquisa.equals("cnpj")) && cliente.getCpfCnpj() != null && faturas == 2) {
			itemProdSaidas = iItemProdSaidaDAO.itensProdSaidaComFaturasGeradasPorCpfOuCnpjCliente(cliente.getCpfCnpj());
		} else if ((tipoPesquisa.equals("cpf") || tipoPesquisa.equals("cnpj")) && cliente.getCpfCnpj() != null && faturas == 3) {
			itemProdSaidas = iItemProdSaidaDAO.listaFiltradoPorCpfouCnpjCliente(cliente.getCpfCnpj());
		}
		//intervalo entre datas
		else if (tipoPesquisa.equals("data") && dataInicio != null && dataFim != null && faturas == 1) {
			itemProdSaidas = iItemProdSaidaDAO.itensProdSaidaSemFaturasGeradasPorIntervaloDeDatas(dataInicio, dataFim);
		} else if (tipoPesquisa.equals("data") && dataInicio != null && dataFim != null && faturas == 2) {
			itemProdSaidas = iItemProdSaidaDAO.itensProdSaidaComFaturasGeradasPorIntervaloDeDatas(dataInicio, dataFim);
		} else if (tipoPesquisa.equals("data") && dataInicio != null && dataFim != null && faturas == 3) {
			itemProdSaidas = iItemProdSaidaDAO.intervaloEntreDatas(dataInicio, dataFim);
		}
		
		try {
	        String filename = "SaidaDeProdutos.pdf";
	        String jasperPath = "";
			if(itemProdSaidas.size() > 0) {	        
		        jasperPath = "/resources/relatorios/detalhes_vendas.jasper";
		        GeradorRelatorio.PDF(null, jasperPath, itemProdSaidas, filename);
			}else {
				jasperPath = "/resources/relatorios/sem_informacoes.jasper";
				semInformacoes.add(new SemInformacoes("Histórico da saída de produtos", "Relatório de vendas.", "Nenhum registro foi encontrado.", false));
				GeradorRelatorio.PDF(null, jasperPath, semInformacoes, filename);
			}
		} catch (JRException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public ItemProdSaidaDAO getiItemProdSaidaDAO() {
		return iItemProdSaidaDAO;
	}

	public void setiItemProdSaidaDAO(ItemProdSaidaDAO iItemProdSaidaDAO) {
		this.iItemProdSaidaDAO = iItemProdSaidaDAO;
	}

	public int getFaturas() {
		return faturas;
	}

	public void setFaturas(int faturas) {
		this.faturas = faturas;
	}

	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}
	
	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public SaidaDeProdutos getSaidaDeProdutos() {
		return saidaDeProdutos;
	}

	public void setSaidaDeProdutos(SaidaDeProdutos saidaDeProdutos) {
		this.saidaDeProdutos = saidaDeProdutos;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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

}
