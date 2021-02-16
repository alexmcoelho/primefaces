package com.alex.controle;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.modelo.Categoria;
import com.alex.modelo.Produto;
import com.alex.servico.ProdutoLazyDataModel;
import com.alex.servico.ServicoGenerico;

@Named
@ViewScoped
public class ProdutoListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String tipoPesquisa;
	
	@Inject
	private ProdutoLazyDataModel model;
	
	@PostConstruct
	public void init() {
		tipoPesquisa = "descricao";
	}
	
	@Override
	public String getTitulo() {
		return "Pesquisa produto";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/produto/lista-produto.jsf";
	}
	
	public void criaInstanciaCategoria() {
		if(tipoPesquisa.equals("categoria")) {
			model.getProduto().setCategoria(new Categoria());
		}
	}

	public void preparaExclusao(Produto p) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setProduto(p);
	}

	public void excluir() {
		model.getObjDAO().excluir(model.getProduto().getId());
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
			model.parametros.put("id", model.getProduto().getId());
		}
		else if(tipoPesquisa.equals("descricao")) {
			model.parametros.put("descricao", ServicoGenerico.montaPesquisaInteligente(model.getProduto().getDescricao(), true));
		}
		else if(tipoPesquisa.equals("categoria")) {
			model.parametros.put("descricaoCategoria", ServicoGenerico.montaPesquisaInteligente(model.getProduto().getCategoria().getDescricao(), true));
		}
		model.parametros.put("buscaValorPagoVendaLucro", "buscaValorPagoVendaLucro");
		model.parametros.put("buscaQuant", "buscaQuant");
	}
	
	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public ProdutoLazyDataModel getModel() {
		return model;
	}

	public void setModel(ProdutoLazyDataModel model) {
		this.model = model;
	}

}
