package com.alex.controle;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import com.alex.modelo.Servico;
import com.alex.servico.ServicoGenerico;
import com.alex.servico.ServicoLazyDataModel;

@Named
@ViewScoped
public class ServicoListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ServicoLazyDataModel model;

	private String tipoPesquisa;
	
	@PostConstruct
	public void init() {
		tipoPesquisa = "descricao";
	}
	
	@Override
	public String getTitulo() {
		return "Pesquisa serviço";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/servico/lista-servico.jsf";
	}

	public void preparaExclusao(Servico obj) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setServico(obj);
	}

	public void excluir() {
		model.getObjDAO().excluir(model.getServico().getId());
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
			model.parametros.put("id", model.getServico().getId());
		} else if (tipoPesquisa.equals("descricao")) {
			model.parametros.put("descricao", ServicoGenerico.montaPesquisaInteligente(model.getServico().getDescricao(), true));
		}
		
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public ServicoLazyDataModel getModel() {
		return model;
	}

}
