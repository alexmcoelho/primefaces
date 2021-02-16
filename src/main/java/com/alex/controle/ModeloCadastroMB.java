package com.alex.controle;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

import com.alex.DAO.AparelhoDAO;
import com.alex.DAO.ModeloDAO;
import com.alex.modelo.Aparelho;
import com.alex.modelo.Modelo;
import com.alex.servico.AparelhoLazyDataModel;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class ModeloCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;

	@Inject
	private Modelo modelo;

	@Inject
	private ModeloDAO modeloDAO;
	
	@Inject 
	private AparelhoDAO aparelhoDAO;
	
	@Inject
	private Aparelho aparelho;
	
	private Long idModelo;
	
	private List<Aparelho> aparelhosFiltrados;
	
	private String nomeAparelho;
	
	private String marca;
	
	RequestContext request = RequestContext.getCurrentInstance();
	
	@Inject
	private AparelhoLazyDataModel modelAparelho;

	public void inicializar() {

		if (idModelo != null) {			
			modelo = modeloDAO.porID(idModelo);
			modelo = modelo == null ? new Modelo() : modelo;
			if(modelo.getId() != null) {
				filtros.put("parametro01", idModelo);
				filtros.put("entidadeId", "modelo");
				aparelho = modelo.getAparelho();
				nomeAparelho = aparelho.getMarca();
			}
			idModelo = null;
		}
	}
	
	@PostConstruct
	private void init() {
		modelAparelho.getServicoAutocomplete().setMinQueryLength(1);	
	}
	
	@Override
	public String getTitulo() {
		return "Cadastro de modelo";
	}

	@Override
	public String getLinkBreadCrumb() {
		if(filtros.get("parametro01") != null) {
			return "/pages/modelo/cadastro-modelo.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/modelo/cadastro-modelo.jsf";
	}
	
	@Override
	public String caminhoCancelar() {
		return "/pages/modelo/lista-modelo?faces-redirect=true";
	}
	
	public void abreModalAparelho() {
		aparelho.newObj();
		modelo.setAparelho(aparelho);
		nomeAparelho = null;
	}

	public void salvar() {
		modelo.setAparelho(aparelho);
		modeloDAO.salvar(modelo);
		
		if(modeloDAO.isControle()) {
			modelo.newObj();
			aparelho.newObj();
			nomeAparelho = null;
		}
		
	}
	
	public void selecionarRegistro(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/modelo/cadastro-modelo.jsf?id=" + idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}

	public void excluir() {
		modeloDAO.excluir(modelo.getId());
		if(modeloDAO.isControle()) {
			modelo.newObj();
			aparelho.newObj();
		}
	}
	
	public void selecionarAparelho(SelectEvent event) {
		aparelho = modelAparelho.busca(nomeAparelho, false);
	}
	
	public List<String> filtroAparelho(String txt){
		return modelAparelho.filtro(txt, 15);
	}
	
	public void salvarAparelho() {
		try {
			aparelho.setMarca(nomeAparelho);
			aparelhoDAO.salvar(aparelho);
			if(aparelhoDAO.isControle()) {
				aparelho = aparelhoDAO.getEntidade();
				modelo.setAparelho(aparelho);
				request.addCallbackParam("sucessoAparelho", true);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public Modelo getModelo() {
		return modelo;
	}

	public void setModelo(Modelo modelo) {
		this.modelo = modelo;
	}

	public Long getIdModelo() {
		return idModelo;
	}

	public void setIdModelo(Long idModelo) {
		this.idModelo = idModelo;
	}

	public List<Aparelho> getAparelhosFiltrados() {
		return aparelhosFiltrados;
	}

	public void setAparelhosFiltrados(List<Aparelho> aparelhosFiltrados) {
		this.aparelhosFiltrados = aparelhosFiltrados;
	}

	public String getNomeAparelho() {
		return nomeAparelho;
	}

	public void setNomeAparelho(String nomeAparelho) {
		this.nomeAparelho = nomeAparelho;
	}

	public Aparelho getAparelho() {
		return aparelho;
	}

	public void setAparelho(Aparelho aparelho) {
		this.aparelho = aparelho;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public AparelhoLazyDataModel getModelAparelho() {
		return modelAparelho;
	}

	public void setModelAparelho(AparelhoLazyDataModel modelAparelho) {
		this.modelAparelho = modelAparelho;
	}

}
