package com.alex.controle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
/*import java.util.List;
import com.alex.DAO.ClienteDAO;
import com.alex.DAO.OrdemDeServicoDAO;
import com.alex.DAO.SaidaDeProdutosDAO;*/

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.alex.DAO.DetalhesFaturaDAO;
import com.alex.modelo.Cliente;
import com.alex.modelo.OrdemDeServico;
import com.alex.modelo.SaidaDeProdutos;
import com.alex.servico.OrdemDeServicoLazyDataModel;
import com.alex.servico.SaidaDeProdutosLazyDataModel;
import com.alex.servico.ServicoGenerico;
import com.alex.util.FacesUtil;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class FaturaListaMB extends ComplementoMB implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private OrdemDeServico ordemDeServico;
	
	@Inject
	private DetalhesFaturaDAO detalhesFaturaDAO;
	
	@Inject
	private Cliente cliente;
	
	@Inject
	private SaidaDeProdutos saidaDeProduto;
	
	private String tipoFatura;
	
	private String tipoPesquisa;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	private Long idOrdemOuSaida;
	
	private FacesContext fc = FacesContext.getCurrentInstance();
	
	private String url;
	
	@Inject
	private OrdemDeServicoLazyDataModel modelOrdemDeServico;
	
	@Inject
	private SaidaDeProdutosLazyDataModel modelSaidaDeProdutos;
	
	@PostConstruct
	public void init() {
		tipoFatura = "ordem";
		tipoPesquisa = "todos";
		HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
		url = request.getRequestURI();
	}
	
	@Override
	public String getTitulo() {
		if(url.contains("lista-parcelas-geradas")) {
			return "Pesquisa de ordens de serviços ou vendas de produtos que foi gerado faturas";
		}
		return "Pesquisa"; //tela gera faturas
	}

	@Override
	public String getLinkBreadCrumb() {
		if(url.contains("lista-parcelas-geradas")) {
			return "/pages/fatura/lista-parcelas-geradas.jsf";
		}
		return "/pages/fatura/gera-fatura.jsf"; //tela gera faturas
	}
	
	//sobrescrevendo título breadcrumb
	public String getTituloBreadCrumbString() {
		if(url.contains("lista-parcelas-geradas")) {
			return getTitulo();
		}
		return "Gerar faturas"; //tela gera faturas
	}
	
	public void pesquisarOrdemComFaturaGerada() {

		if(modelOrdemDeServico.limpaParametros) {
			modelOrdemDeServico.limpaParametros();
		}
		else {
			modelOrdemDeServico.limpaParametros = true;
		}

		modelOrdemDeServico.parametros.put("comFaturaGerada", "comFaturaGerada");
		/*if (tipoPesquisa.equals("todos")) {
			ordemDeServicos = ordemDeServicoDAO.ordensComFaturaGerada();
		}*/
		if (tipoPesquisa.equals("numero") && idOrdemOuSaida != null) {
			modelOrdemDeServico.parametros.put("id", idOrdemOuSaida);
		} else if(tipoPesquisa.equals("numero_cel") && ordemDeServico.getImei() != null) {
			modelOrdemDeServico.parametros.put("imei", ordemDeServico.getImei());
		} else if ((tipoPesquisa.equals("cpf") || tipoPesquisa.equals("cnpj")) && cliente.getCpfCnpj() != null) {
			modelOrdemDeServico.parametros.put("cpfCnpj", cliente.getCpfCnpj());
		} else if(tipoPesquisa.equals("data")){
			modelOrdemDeServico.parametros.put("dataInicio", dataInicio);
			modelOrdemDeServico.parametros.put("dataFim", dataFim);
		} else if(tipoPesquisa.equals("nome")){
			modelOrdemDeServico.parametros.put("nomeCliente", ServicoGenerico.montaPesquisaInteligente(cliente.getNome(), true));
		}
	}
	
	public void pesquisarOrdemSemFaturaGerada() {
		modelOrdemDeServico.limpaParametros();
		modelOrdemDeServico.parametros.put("semFaturaGerada", "semFaturaGerada");
		/*if (tipoPesquisa.equals("todos")) {
			modelOrdemDeServico.parametros.put("semFaturaGerada", "semFaturaGerada");
		}*/
		if (tipoPesquisa.equals("numero") && idOrdemOuSaida != null) {
			modelOrdemDeServico.parametros.put("id", idOrdemOuSaida);
		} else if(tipoPesquisa.equals("numero_cel") && ordemDeServico.getImei() != null) {
			modelOrdemDeServico.parametros.put("imei", ordemDeServico.getImei());
		}
		else if ((tipoPesquisa.equals("cpf") || tipoPesquisa.equals("cnpj")) && cliente.getCpfCnpj() != null) {
			modelOrdemDeServico.parametros.put("cpfCnpj", cliente.getCpfCnpj());
		} else if(tipoPesquisa.equals("data")){
			modelOrdemDeServico.parametros.put("dataInicio", dataInicio);
			modelOrdemDeServico.parametros.put("dataFim", dataFim);
		} else if(tipoPesquisa.equals("nome")){
			modelOrdemDeServico.parametros.put("nomeCliente", ServicoGenerico.montaPesquisaInteligente(cliente.getNome(), true));
		}
	}
	
	/* Irá salvar na sessão a saída de produto selecionada para em seguida gerar as parcelas*/
	public void selecionarSaidaDeProduto(SaidaDeProdutos s) {
		
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().put("saidaDeProdutosFatura", s);
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/fatura/cadastro-fatura-ordem.jsf");
			tipoPesquisa = "todos";
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}
	
	public void selecionarOrdemDeServico(OrdemDeServico o) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().put("ordemDeServicoFatura", o);
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/fatura/cadastro-fatura-ordem.jsf");
			tipoPesquisa = "todos";
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}
	
	public void pesquisarVendaSemFaturaGerada() {
		modelSaidaDeProdutos.limpaParametros();
		modelSaidaDeProdutos.parametros.put("semFaturaGerada", "semFaturaGerada");
		/*if (tipoPesquisa.equals("todos")) {
			saidaDeProdutos = saidaDeProdutosDAO.saidasSemFaturasGeradas();
		}*/
		if (tipoPesquisa.equals("numero") && idOrdemOuSaida != null) {
			modelSaidaDeProdutos.parametros.put("id", idOrdemOuSaida);
		} else if ((tipoPesquisa.equals("cpf") || tipoPesquisa.equals("cnpj")) && cliente.getCpfCnpj() != null) {
			modelSaidaDeProdutos.parametros.put("cpfCnpj", cliente.getCpfCnpj());
		} else if(tipoPesquisa.equals("data")){
			modelSaidaDeProdutos.parametros.put("dataInicio", dataInicio);
			modelSaidaDeProdutos.parametros.put("dataFim", dataFim);
		} else if(tipoPesquisa.equals("nome")) {
			modelSaidaDeProdutos.parametros.put("nomeCliente", ServicoGenerico.montaPesquisaInteligente(cliente.getNome(), true));
		}
	}
	
	public void pesquisarVendaComFaturaGerada() {
		
		if(modelSaidaDeProdutos.limpaParametros) {
			modelSaidaDeProdutos.limpaParametros();
		}
		else {
			modelSaidaDeProdutos.limpaParametros = true;
		}
		
		modelSaidaDeProdutos.parametros.put("comFaturaGerada", "comFaturaGerada");
		
		if (tipoPesquisa.equals("numero") && idOrdemOuSaida != null) {
			modelSaidaDeProdutos.parametros.put("id", idOrdemOuSaida);
		} else if ((tipoPesquisa.equals("cpf") || tipoPesquisa.equals("cnpj")) && cliente.getCpfCnpj() != null) {
			modelSaidaDeProdutos.parametros.put("cpfCnpj", cliente.getCpfCnpj());
		} else if(tipoPesquisa.equals("data")){
			modelSaidaDeProdutos.parametros.put("dataInicio", dataInicio);
			modelSaidaDeProdutos.parametros.put("dataFim", dataFim);
		} else if(tipoPesquisa.equals("nome")) {
			modelSaidaDeProdutos.parametros.put("nomeCliente", ServicoGenerico.montaPesquisaInteligente(cliente.getNome(), true));
		}
	}
	
	public void selecionarOrdemDeServico(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidadeOrdem = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/fatura/cadastro-fatura-ordem.jsf?idOrdem=" + idEntidadeOrdem);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}
	
	public void selecionarSaidaDeProdutos(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/fatura/cadastro-fatura-ordem.jsf?idSaida=" + idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}
	
	public void preparaExclusao(OrdemDeServico o) {
		//pode ser que este objeto entre em conflito com outra função, caso isso aconteca criar um metodo
		//que instancie este obj novamente
		ordemDeServico = o;
	}
	
	public void preparaExclusaoVenda(SaidaDeProdutos s) {
		//pode ser que este objeto entre em conflito com outra função, caso isso aconteca criar um metodo
		//que instancie este obj novamente
		saidaDeProduto = s;
	}
	
	public void excluirOrdem() {
		int tamListaFaturasPagas = 0;
		tamListaFaturasPagas = detalhesFaturaDAO.filtraFaturaPorIdOrdemAndSituacao(ordemDeServico.getId(), true).size();
		if(tamListaFaturasPagas > 0) {
			FacesUtil.addWarnMessage("Não é possível excluir o registro, pois foi dado baixa em uma ou mais faturas!", "");
		}else {
			detalhesFaturaDAO.excluizaoEmMassa(new ArrayList<>(ordemDeServico.getDetalhesFaturas()));
			if(detalhesFaturaDAO.isControle()) {
				modelOrdemDeServico.limpaParametros = false;
			}
		}		
	}
	
	public void excluirVenda() {
		int tamListaFaturasPagas = 0;
		tamListaFaturasPagas = detalhesFaturaDAO.filtraFaturaPorIdSaidaAndSituacao(saidaDeProduto.getId(), true).size();
		if(tamListaFaturasPagas > 0) {
			FacesUtil.addWarnMessage("Não é possível excluir o registro, pois foi dado baixa em uma ou mais faturas!", "");
		}else {
			detalhesFaturaDAO.excluizaoEmMassa(detalhesFaturaDAO.filtraFaturaPorIdSaida(saidaDeProduto.getId()));
			if(detalhesFaturaDAO.isControle()) {
				modelSaidaDeProdutos.limpaParametros = false;
				pesquisarVendaComFaturaGerada();
			}
			
		}		
	}	

	public String getTipoFatura() {
		return tipoFatura;
	}

	public void setTipoFatura(String tipoFatura) {
		this.tipoFatura = tipoFatura;
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public OrdemDeServico getOrdemDeServico() {
		return ordemDeServico;
	}

	public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
		this.ordemDeServico = ordemDeServico;
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

	public Long getIdOrdemOuSaida() {
		return idOrdemOuSaida;
	}

	public void setIdOrdemOuSaida(Long idOrdemOuSaida) {
		this.idOrdemOuSaida = idOrdemOuSaida;
	}

	public OrdemDeServicoLazyDataModel getModelOrdemDeServico() {
		return modelOrdemDeServico;
	}

	public void setModelOrdemDeServico(OrdemDeServicoLazyDataModel modelOrdemDeServico) {
		this.modelOrdemDeServico = modelOrdemDeServico;
	}

	public SaidaDeProdutosLazyDataModel getModelSaidaDeProdutos() {
		return modelSaidaDeProdutos;
	}

	public void setModelSaidaDeProdutos(SaidaDeProdutosLazyDataModel modelSaidaDeProdutos) {
		this.modelSaidaDeProdutos = modelSaidaDeProdutos;
	}

}
