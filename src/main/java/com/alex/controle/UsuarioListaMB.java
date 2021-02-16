package com.alex.controle;

import java.io.Serializable;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.modelo.Usuario;
import com.alex.servico.ServicoGenerico;
import com.alex.servico.UsuarioLazyDataModel;

@Named
@ViewScoped
public class UsuarioListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UsuarioLazyDataModel model;
	
	@Override
	public String getTitulo() {
		return "Pesquisa usuário";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/usuario/lista-usuario.jsf";
	}

	public void preparaExclusao(Usuario u) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setUsuario(u);
	}

	@SuppressWarnings("unchecked")
	public void excluir() {
		model.getObjDAO().excluir(model.getUsuario());
		if(model.getObjDAO().isControle()) {
			//aplicando exclusão na lista que está na memória (sessão)
			List<Usuario> usuariosOrdemServico;
			usuariosOrdemServico = (List<Usuario>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuariosOrdemServico");
			if(usuariosOrdemServico != null) {
				model.limpaParametros = false;
				pesquisar();
				usuariosOrdemServico.remove(model.getUsuario());
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuariosOrdemServico", usuariosOrdemServico);
			}
		}
	}

	public void pesquisar() {

		if(model.limpaParametros) {
			model.limpaParametros();
		}
		else {
			model.limpaParametros = true;
		}
		
		model.parametros.put("nome", ServicoGenerico.montaPesquisaInteligente(model.getUsuario().getNome(), true));
	}

	public UsuarioLazyDataModel getModel() {
		return model;
	}

	public void setModel(UsuarioLazyDataModel model) {
		this.model = model;
	}

}
