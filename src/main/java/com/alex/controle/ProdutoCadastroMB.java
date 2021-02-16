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

import com.alex.DAO.ProdutoDAO;
import com.alex.modelo.Categoria;
import com.alex.modelo.Produto;
import com.alex.servico.CategoriaLazyDataModel;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class ProdutoCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;

	@Inject
	private Produto produto;
	
	@Inject
	private Categoria categoria;

	@Inject
	private ProdutoDAO produtoDAO;
	
	private Long idProduto;
	
	private boolean defineValor = false;
	
	private String descricaoCategoria;
	
	@Inject
	private CategoriaLazyDataModel modelCategoria;
	
	private List<Categoria> categorias;

	RequestContext request = RequestContext.getCurrentInstance();
	
	public void inicializar() {

		if (idProduto != null) {			
			produto = produtoDAO.porID(idProduto);
			produto = produto == null ? new Produto() : produto;
			if(produto.getId() != null) {
				defineValor = produto.getValorInformado() != null;
				categoria = produto.getCategoria();
				filtros.put("parametro01", idProduto);
				filtros.put("entidadeId", "produto");
			}
			idProduto = null;
		}
	}
	
	@PostConstruct
	public void init() {
		categorias = modelCategoria.getObjDAO().listAllSelectOneMenu();
	}

	@Override
	public String caminhoCancelar() {
		return "/pages/produto/lista-produto?faces-redirect=true";
	}
	
	@Override
	public String getTitulo() {
		return "Cadastro de produto";
	}

	@Override
	public String getLinkBreadCrumb() {
		if(filtros.get("parametro01") != null) {
			return "/pages/produto/cadastro-produto.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/produto/cadastro-produto.jsf";
	}

	// Nao retorna string, pois nao tera necessidade de voltar para pagina com a
	// lista de orcamentos
	public void salvar() {
		produto.setDescricao(produto.getDescricao().trim());
		produto.setCategoria(categoria);
		if(defineValor) {
			produto.setPercentualLucro(null);
		}
		else {
			produto.setValorInformado(null);
		}
		if(produto.getCategoria() != null && produto.getCategoria().getDescricao() != null &&
				!produto.getCategoria().getDescricao().toLowerCase().contains("celular".toLowerCase())) {
			produto.setImei(null);
		}
		produto.setCategoria(categoria);
		produtoDAO.salvar(produto);
		if(produtoDAO.isControle()) {
			produto = new Produto();
			categoria = new Categoria();
		}
	}
	
	public void selecionarProduto(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/produto/cadastro-produto.jsf?id=" + idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}

	public void excluir() {
		produtoDAO.excluir(produto.getId());
		if(produtoDAO.isControle()) {
			produto = new Produto();
		}
	}
	
	public void salvarCategoria() {
		modelCategoria.getObjDAO().salvar(categoria);
		if(modelCategoria.getObjDAO().isControle()) {
			categoria = modelCategoria.getObjDAO().getEntidade();
			categorias.add(categoria);
		}
	}
	
	public void abreModalCategoria() {
		categoria = new Categoria();
	}

	public Long getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Long idProduto) {
		this.idProduto = idProduto;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public boolean isDefineValor() {
		return defineValor;
	}

	public void setDefineValor(boolean defineValor) {
		this.defineValor = defineValor;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getDescricaoCategoria() {
		return descricaoCategoria;
	}

	public void setDescricaoCategoria(String descricaoCategoria) {
		this.descricaoCategoria = descricaoCategoria;
	}

	public CategoriaLazyDataModel getModelCategoria() {
		return modelCategoria;
	}

	public void setModelCategoria(CategoriaLazyDataModel modelCategoria) {
		this.modelCategoria = modelCategoria;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

}
