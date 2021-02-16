package com.alex.controle;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.modelo.Aparelho;
import com.alex.modelo.Modelo;
import com.alex.servico.ModeloLazyDataModel;
import com.alex.servico.ServicoGenerico;

@Named
@ViewScoped
public class ModeloListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String tipoPesquisa;
	
	@Inject
	private ModeloLazyDataModel model;
	
	@PostConstruct
	public void init() {
		tipoPesquisa = "modelo";
		if(model.getModelo().getAparelho() == null) {
			model.getModelo().setAparelho(new Aparelho());
		}
	}
	
	@Override
	public String getTitulo() {
		return "Pesquisa modelo";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/modelo/lista-modelo.jsf";
	}

	public void preparaExclusao(Modelo obj) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setModelo(obj);
	}

	public void excluir() {
		model.getObjDAO().excluir(model.getModelo().getId());
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
			model.parametros.put("id", model.getModelo().getId());
		} else if (tipoPesquisa.equals("modelo")) {
			model.parametros.put("modelo", ServicoGenerico.montaPesquisaInteligente(model.getModelo().getModelo(), true));
		} else if(tipoPesquisa.equals("aparelho")) {
			model.parametros.put("marcaAparelho", ServicoGenerico.montaPesquisaInteligente(model.getModelo().getAparelho().getMarca(), true));
		}
		
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public ModeloLazyDataModel getModel() {	
		return model;
	}

	public void setModel(ModeloLazyDataModel model) {
		this.model = model;
	}

}
