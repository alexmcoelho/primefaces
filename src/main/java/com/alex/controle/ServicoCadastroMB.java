package com.alex.controle;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.alex.DAO.ServicoDAO;
import com.alex.modelo.Servico;
import com.alex.modelo.enums.MaoDeObra;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class ServicoCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;
	
	@Inject 
	private ServicoDAO servicoDAO;
	
	@Inject
	private Servico servico;
	
	private Long idServico;
	
	private List<MaoDeObra> listaMaoDeObra = Arrays.asList(MaoDeObra.values());
	
	RequestContext request = RequestContext.getCurrentInstance();

	public void inicializar() {

		if (idServico != null) {			
			servico = servicoDAO.porID(idServico);
			servico = servico == null ? new Servico() : servico;
			if(servico.getId() != null) {
				filtros.put("parametro01", idServico);
				filtros.put("entidadeId", "serviço");
			}
			idServico = null;
		}
	}
	
	@Override
	public String getTitulo() {
		return "Cadastro de serviço";
	}

	@Override
	public String getLinkBreadCrumb() {
		if(filtros.get("parametro01") != null) {
			return "/pages/servico/cadastro-servico.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/servico/cadastro-servico.jsf";
	}
	
	@Override
	public String caminhoCancelar() {
		return "/pages/servico/lista-servico?faces-redirect=true";
	}

	public void salvar() {
		servicoDAO.salvar(servico);
		if(servicoDAO.isControle()) {
			servico = new Servico();
		}
		
	}
	
	public void selecionarRegistro(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/servico/cadastro-servico.jsf?id=" + idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}

	public void excluir() {
		servicoDAO.excluir(servico.getId());
		if(servicoDAO.isControle()) {
			servico = new Servico();
		}
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public Long getIdServico() {
		return idServico;
	}

	public void setIdServico(Long idServico) {
		this.idServico = idServico;
	}

	public List<MaoDeObra> getListaMaoDeObra() {
		return listaMaoDeObra;
	}

	public void setListaMaoDeObra(List<MaoDeObra> listaMaoDeObra) {
		this.listaMaoDeObra = listaMaoDeObra;
	}

}
