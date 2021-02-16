package com.alex.controle;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import com.alex.modelo.Aparelho;
import com.alex.servico.AparelhoLazyDataModel;
import com.alex.servico.ServicoGenerico;

@Named
@ViewScoped
public class AparelhoListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private AparelhoLazyDataModel model;

	private String tipoPesquisa;
	
	@PostConstruct
	public void init() {
		tipoPesquisa = "marca";
	}
	
	@Override
	public String getTitulo() {
		return "Pesquisa aparelho";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/aparelho/lista-aparelho.jsf";
	}

	public void preparaExclusao(Aparelho obj) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setAparelho(obj);
	}

	public void excluir() {
		model.getObjDAO().excluir(model.getAparelho().getId());
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
			model.parametros.put("id", model.getAparelho().getId());
		} else if (tipoPesquisa.equals("marca")) {
			model.parametros.put("marca", ServicoGenerico.montaPesquisaInteligente(model.getAparelho().getMarca(), true));
		}
		
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public AparelhoLazyDataModel getModel() {
		return model;
	}

	public void setModel(AparelhoLazyDataModel model) {
		this.model = model;
	}

}
