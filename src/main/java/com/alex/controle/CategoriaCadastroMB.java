package com.alex.controle;

import java.io.Serializable;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;
import com.alex.DAO.CategoriaDAO;
import com.alex.modelo.Categoria;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class CategoriaCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;
	
	@Inject 
	private CategoriaDAO categoriaDAO;
	
	@Inject
	private Categoria categoria;
	
	private Long idCategoria;
	
	RequestContext request = RequestContext.getCurrentInstance();

	public void inicializar() {

		if (idCategoria != null) {			
			categoria = categoriaDAO.porID(idCategoria);
			categoria = categoria == null ? new Categoria() : categoria;
			if(categoria.getId() != null) {
				filtros.put("parametro01", idCategoria);
				filtros.put("entidadeId", "categoria");
			}
			idCategoria = null;
		}
	}
	
	@Override
	public String getTitulo() {
		return "Cadastro de categoria";
	}

	@Override
	public String getLinkBreadCrumb() {
		if(filtros.get("parametro01") != null) {
			return "/pages/categoria/cadastro-categoria.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/categoria/cadastro-categoria.jsf";
	}
	
	@Override
	public String caminhoCancelar() {
		return "/pages/categoria/lista-categoria?faces-redirect=true";
	}

	public void salvar() {
		categoriaDAO.salvar(categoria);
		
		if(categoriaDAO.isControle()) {
			categoria = new Categoria();
		}
		
	}
	
	public void selecionarRegistro(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/categoria/cadastro-categoria.jsf?id=" + idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}

	public void excluir() {
		categoriaDAO.excluir(categoria.getId());
		if(categoriaDAO.isControle()) {
			categoria = new Categoria();
		}
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Long getIdCategoria() {
		return idCategoria;
	}

	public void setIdCategoria(Long idCategoria) {
		this.idCategoria = idCategoria;
	}

}
