package com.alex.controle;

import java.io.Serializable;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.modelo.Pedido;
import com.alex.servico.PedidoLazyDataModel;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class PedidoListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer tipoPesquisa;
	
	@Inject
	private PedidoLazyDataModel model;
	
	@Override
	public String getTitulo() {
		return "Pesquisa pedido";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/pedido/lista-pedido.jsf";
	}

	public void preparaExclusao(Pedido obj) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setPedido(obj);
	}

	public void excluir() {
		model.getObjDAO().excluir(model.getPedido().getId());
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
		
		if(tipoPesquisa == 2) {
			model.parametros.put("todos", "todos");
		}
		else {
			model.parametros.put("concluido", tipoPesquisa);
		}
		tipoPesquisa = 0;
	}

	public void selecionarRegistro(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/pedido/cadastro-pedido.jsf?id="+idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}

	public Integer getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(Integer tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public PedidoLazyDataModel getModel() {
		return model;
	}

}
