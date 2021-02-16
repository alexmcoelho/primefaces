package com.alex.controle;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.modelo.Fornecedor;
import com.alex.servico.FornecedorLazyDataModel;
import com.alex.servico.ServicoGenerico;

@Named
@ViewScoped
public class FornecedorListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private FornecedorLazyDataModel model;
	
	private String tipoPesquisa;
	
	@PostConstruct
	public void init() {
		tipoPesquisa = "nome";
	}
	
	@Override
	public String getTitulo() {
		return "Pesquisa fornecedor";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/fornecedor/lista-fornecedor.jsf";
	}

	public void preparaExclusao(Fornecedor obj) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setFornecedor(obj);
	}

	public void excluir() {
		model.getObjDAO().excluir(model.getFornecedor().getId());
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
		
		if(tipoPesquisa.equals("todos")) {
			model.parametros.put("todos", tipoPesquisa);
		} 
		else if(tipoPesquisa.equals("codigo")) {
			model.parametros.put("id", model.getFornecedor().getId());
		}
		else if(tipoPesquisa.equals("nome")) {
			model.parametros.put("nome", ServicoGenerico.montaPesquisaInteligente(model.getFornecedor().getNome(), true));
		}
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public FornecedorLazyDataModel getModel() {
		return model;
	}

	public void setModel(FornecedorLazyDataModel model) {
		this.model = model;
	}

}
