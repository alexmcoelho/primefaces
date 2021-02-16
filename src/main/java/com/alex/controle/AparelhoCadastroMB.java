package com.alex.controle;

import java.io.Serializable;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.alex.DAO.AparelhoDAO;
import com.alex.modelo.Aparelho;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class AparelhoCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;
	
	@Inject 
	private AparelhoDAO aparelhoDAO;
	
	@Inject
	private Aparelho aparelho;
	
	private Long idAparelho;
	
	RequestContext request = RequestContext.getCurrentInstance();

	public void inicializar() {

		if (idAparelho != null) {			
			aparelho = aparelhoDAO.porID(idAparelho);
			aparelho = aparelho == null ? new Aparelho() : aparelho;
			if(aparelho.getId() != null) {
				filtros.put("parametro01", idAparelho);
				filtros.put("entidadeId", "aparelho");
			}
			idAparelho = null;
		}
	}
	
	@Override
	public String getTitulo() {
		return "Cadastro de aparelho";
	}

	@Override
	public String getLinkBreadCrumb() {
		if(filtros.get("parametro01") != null) {
			return "/pages/aparelho/cadastro-aparelho.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/aparelho/cadastro-aparelho.jsf";
	}
	
	@Override
	public String caminhoCancelar() {
		return "/pages/aparelho/lista-aparelho?faces-redirect=true";
	}

	public void salvar() {
		aparelhoDAO.salvar(aparelho);
		
		if(aparelhoDAO.isControle()) {
			aparelho = new Aparelho();
		}
		
	}
	
	public void selecionarRegistro(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/aparelho/cadastro-aparelho.jsf?id=" + idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}

	public void excluir() {
		aparelhoDAO.excluir(aparelho.getId());
		if(aparelhoDAO.isControle()) {
			aparelho = new Aparelho();
		}
	}

	public Aparelho getAparelho() {
		return aparelho;
	}

	public void setAparelho(Aparelho aparelho) {
		this.aparelho = aparelho;
	}

	public Long getIdAparelho() {
		return idAparelho;
	}

	public void setIdAparelho(Long idAparelho) {
		this.idAparelho = idAparelho;
	}

}
