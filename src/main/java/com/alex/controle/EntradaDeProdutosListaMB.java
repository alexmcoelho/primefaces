package com.alex.controle;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.application.ProjectStage;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.modelo.EntradaDeProdutos;
import com.alex.servico.EntradaDeProdutosLazyDataModel;
import com.alex.servico.ServicoGenerico;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class EntradaDeProdutosListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tipoPesquisa;

	private Date dataInicio;

	private Date dataFim;
	
	@Inject
	private EntradaDeProdutosLazyDataModel model;

	@PostConstruct
	public void inicializar() {
		// tarefas = tarefaDAO.listAll();
		tipoPesquisa = "todos";
	}
	
	@Override
	public String getTitulo() {
		return "Pesquisa entrada";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/estoque/lista-entrada-produto.jsf";
	}

	public void preparaExclusao(EntradaDeProdutos e) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setEntradaDeProduto(e);
	}

	public void excluir() {
		model.getObjDAO().excluir(model.getEntradaDeProduto().getId());
		if(model.getObjDAO().isControle()) {
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
		if (tipoPesquisa.equals("numero_entrada") && model.getEntradaDeProduto().getId() != null) {
			model.parametros.put("id", model.getEntradaDeProduto().getId());
		} else if (tipoPesquisa.equals("data")) {
			model.parametros.put("dataInicio", dataInicio);
			model.parametros.put("dataFim", dataFim);
		} else if(tipoPesquisa.equals("codigo_nota") && model.getEntradaDeProduto().getCodigoNota() != null 
				&& !model.getEntradaDeProduto().getCodigoNota().equals("")) {
			model.parametros.put("codigoNota", model.getEntradaDeProduto().getCodigoNota());
		} else if(tipoPesquisa.equals("produto") && model.getDescricaoProduto() != null 
				&& !model.getDescricaoProduto().equals("")) {
			model.parametros.put("produto", ServicoGenerico.montaPesquisaInteligente(model.getDescricaoProduto(), true));
		}
	}

	public void selecionarEntradaDeProduto(ActionEvent event) {
		try {
			tipoPesquisa = "numero_entrada";
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/estoque/cadastro-entrada-produto.jsf?id="+idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}
	
	public void adicionarEmail(ActionEvent event){
	   try {
			tipoPesquisa = "numero_entrada";
			FacesContext context = FacesContext.getCurrentInstance();
			if(context.getApplication().getProjectStage().equals(ProjectStage.Development)) {
				context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/estoque/cadastro-entrada-produto.jsf");
			}
			else {
				context.getExternalContext().redirect("/pages/estoque/cadastro-entrada-produto.jsf");
			}
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}	   
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

	public EntradaDeProdutosLazyDataModel getModel() {
		return model;
	}

	public void setModel(EntradaDeProdutosLazyDataModel model) {
		this.model = model;
	}

}
