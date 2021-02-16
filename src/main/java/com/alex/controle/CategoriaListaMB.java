package com.alex.controle;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import com.alex.modelo.Categoria;
import com.alex.servico.CategoriaLazyDataModel;
import com.alex.servico.ServicoGenerico;

@Named
@ViewScoped
public class CategoriaListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private CategoriaLazyDataModel model;

	private String tipoPesquisa;
	
	@PostConstruct
	public void init() {
		tipoPesquisa = "descricao";
	}
	
	@Override
	public String getTitulo() {
		return "Pesquisa categoria";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/categoria/lista-categoria.jsf";
	}

	public void preparaExclusao(Categoria obj) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setCategoria(obj);
	}

	public void excluir() {
		model.getObjDAO().excluir(model.getCategoria().getId());
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
		} else if (tipoPesquisa.equals("codigo")) {
			model.parametros.put("id", model.getCategoria().getId());
		} else if (tipoPesquisa.equals("descricao")) {
			model.parametros.put("descricao", ServicoGenerico.montaPesquisaInteligente(model.getCategoria().getDescricao(), true));
		}
		
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public CategoriaLazyDataModel getModel() {
		return model;
	}

	public void setModel(CategoriaLazyDataModel model) {
		this.model = model;
	}

}
