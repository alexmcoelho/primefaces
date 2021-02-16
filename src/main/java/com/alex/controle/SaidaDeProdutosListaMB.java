package com.alex.controle;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.modelo.Cliente;
import com.alex.modelo.ItemProdSaida;
import com.alex.modelo.SaidaDeProdutos;
import com.alex.servico.SaidaDeProdutosLazyDataModel;
import com.alex.servico.ServicoGenerico;
import com.alex.util.ManipData;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class SaidaDeProdutosListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tipoPesquisa;

	private Date dataInicio;

	private Date dataFim;
	
	private String quebraLinha = "<br/>";
	
	@Inject
	private SaidaDeProdutosLazyDataModel model;

	@PostConstruct
	public void inicializar() {
		// tarefas = tarefaDAO.listAll();
		tipoPesquisa = "todos";
		if(model.getSaidaDeProduto().getCliente() == null) {
			model.getSaidaDeProduto().setCliente(new Cliente());
		}
	}
	
	@Override
	public String getTitulo() {
		return "Pesquisa";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/saida/lista-saida-produto.jsf";
	}
	
	public String getTituloBreadCrumbString() {
		return "Pesquisa venda";
	}

	public void preparaExclusao(SaidaDeProdutos s) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setSaidaDeProduto(s);
	}

	public void excluir() {
		model.getObjDAO().excluir(model.getSaidaDeProduto().getId());
		if (model.getObjDAO().isControle()) {
			model.limpaParametros = false;
			pesquisar();
		}
	}

	public void pesquisar() {
		
		if(model.limpaParametros) {
			model.limpaParametros();
		}
		else {
			model.limpaParametros = true;
		}
		
		if (tipoPesquisa.equals("todos")) {
			model.parametros.put("todos", tipoPesquisa);
		}
		if (tipoPesquisa.equals("numero_os") && model.getSaidaDeProduto().getId() != null) {
			model.parametros.put("id", model.getSaidaDeProduto().getId());
		} else if ((tipoPesquisa.equals("cpf") && model.getSaidaDeProduto().getCliente().getCpfCnpj() != null) || 
				(tipoPesquisa.equals("cnpj") && model.getSaidaDeProduto().getCliente().getCpfCnpj() != null)) {
			model.parametros.put("cpfCnpj", model.getSaidaDeProduto().getCliente().getCpfCnpj());
		} else if (tipoPesquisa.equals("data")) {
			model.parametros.put("dataInicio", dataInicio);
			model.parametros.put("dataFim", dataFim);
		} else if (tipoPesquisa.equals("nome")) {
			
			model.parametros.put("nomeCliente", ServicoGenerico.montaPesquisaInteligente(model.getSaidaDeProduto().getCliente().getNome(), true));
		} else if(tipoPesquisa.equals("produto") && model.getDescricaoProduto() != null 
				&& !model.getDescricaoProduto().equals("")) {
			model.parametros.put("produto", ServicoGenerico.montaPesquisaInteligente(model.getDescricaoProduto(), true));
		}
		
	}

	public void selecionarSaidaDeProduto(ActionEvent event) {
		try {
			tipoPesquisa = "numero_os";
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/saida/cadastro-saida-produto.jsf?id="+idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}
	
	public void adicionarEmail(ActionEvent event){
	   try {
			tipoPesquisa = "numero_os";
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/saida/cadastro-saida-produto.jsf");
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}	   
	}
	
	//analisa se está na garantia ou não
	public String garantia(ItemProdSaida i) {
		return ManipData.garantia(i);
	}
	
	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
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

	public SaidaDeProdutosLazyDataModel getModel() {
		return model;
	}

	public void setModel(SaidaDeProdutosLazyDataModel model) {
		this.model = model;
	}

	public String getQuebraLinha() {
		return quebraLinha;
	}

	public void setQuebraLinha(String quebraLinha) {
		this.quebraLinha = quebraLinha;
	}
}
