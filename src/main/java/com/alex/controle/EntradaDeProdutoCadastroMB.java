package com.alex.controle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

import com.alex.DAO.EntradaDeProdutosDAO;
import com.alex.DAO.FornecedorDAO;
import com.alex.DAO.ItemEntradaDAO;
import com.alex.DAO.ProdutoDAO;
import com.alex.modelo.Categoria;
import com.alex.modelo.EntradaDeProdutos;
import com.alex.modelo.Fornecedor;
import com.alex.modelo.ItemEntrada;
import com.alex.modelo.Produto;
import com.alex.modelo.Telefone;
import com.alex.modelo.enums.FormaPagamento;
import com.alex.modelo.enums.TipoTelefone;
import com.alex.modelo.validacao.Validacoes;
import com.alex.servico.CategoriaLazyDataModel;
import com.alex.servico.FornecedorLazyDataModel;
import com.alex.servico.ItemEntradaLazyDataModel;
import com.alex.servico.ProdutoLazyDataModel;
import com.alex.util.FacesUtil;

@Named
@ViewScoped
public class EntradaDeProdutoCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntradaDeProdutos entradaDeProduto;

	@Inject
	private EntradaDeProdutosDAO entradaDeProdutosDAO;

	@Inject
	private FornecedorDAO fornecedorDAO;

	@Inject
	private ProdutoDAO produtoDAO;

	@Inject
	private ItemEntradaDAO itemEntradaDAO;

	@Inject
	private Fornecedor fornecedor;

	@Inject
	private Produto produto;

	@Inject
	private Categoria categoria;

	@Inject
	private ItemEntrada itemEntrada;

	private Long idEntrada;

	private String pessoa = "Física";

	private List<Fornecedor> fornecedorFiltrados;

	private List<ItemEntrada> itemEntradas = new ArrayList<>();

	private List<Produto> produtos = new ArrayList<>();
	
	private List<Categoria> categorias;

	RequestContext request = RequestContext.getCurrentInstance();

	private String nome;// nome cliente

	private String descricaoProduto;

	private Long idProduto;

	private boolean mostrarFormProduto = false;

	private String txtBotaoInserir = "Adicionar";

	private int guardaIndexItemProd = -1;// pq tem o indice 0

	private String txtTitleBotaoInserir = "Adicionar à lista de peças";

	private BigDecimal totalSaida = new BigDecimal("0.00");

	@Inject
	private Produto selected;

	@Inject
	private Telefone telefone;

	@Inject
	private Telefone telefoneAlterar;

	private int guardaIndexItemTelefone = -1;

	private List<Telefone> telefones = new ArrayList<Telefone>();

	private List<TipoTelefone> listaEnumTipoTelefone = Arrays.asList(TipoTelefone.values());

	private List<FormaPagamento> listaEnumFormaPagamento = Arrays.asList(FormaPagamento.values());

	private double valorUnitario = 0.00;

	private boolean disabledCamposForm = false;

	private boolean disabledCamposFormProduto = false;

	@Inject
	private FornecedorLazyDataModel modelFornecedor;

	@Inject
	private ProdutoLazyDataModel modelProduto;

	@Inject
	private ItemEntradaLazyDataModel modelItemEntrada;

	private boolean defineValor = false;// controla input de percentual e valor de venda no cadastro de produto

	@Inject
	private CategoriaLazyDataModel modelCategoria;

	private String descricaoCategoria;
	
	private String nomeFornecedor;

	public void inicializar() {

		if (idEntrada != null) {
			entradaDeProduto = entradaDeProdutosDAO.porID(idEntrada);
			entradaDeProduto = entradaDeProduto == null ? new EntradaDeProdutos() : entradaDeProduto;
			if (entradaDeProduto.getId() != null) {

				itemEntradas = entradaDeProduto.getItemEntradas();
				modelItemEntrada.parametros.put("listaDeItens", itemEntradas);				

				entradaDeProduto.setValor(itemEntradaDAO.getValorTotal(entradaDeProduto.getId()));
				totalSaida = entradaDeProduto.getValor();
				nomeFornecedor = entradaDeProduto.getFornecedor().getCodDescricao();
				filtros.put("parametro01", idEntrada);
				filtros.put("entidadeId", "entrada de produto");
			}
			idEntrada = null;
		}
	}

	@PostConstruct
	public void init() {
		produto.setDescricao("");
		produto.setPercentualLucro(new BigDecimal("0.00"));
		itemEntradas = new ArrayList<>();
		entradaDeProduto.setData(new Date());
		telefone.setTipoTelefone(TipoTelefone.CELULAR);
		telefone.setDdd("047");
	}

	@Override
	public String getTitulo() {
		return "Entrada de produto";
	}

	@Override
	public String getLinkBreadCrumb() {
		if (filtros.get("parametro01") != null) {
			return "/pages/estoque/cadastro-entrada-produto.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/estoque/cadastro-entrada-produto.jsf";
	}

	@Override
	public String caminhoCancelar() {
		return "/pages/estoque/lista-entrada-produto?faces-redirect=true";
	}

	public void abreModalFornecedor() {
		fornecedor = new Fornecedor();
		entradaDeProduto.setFornecedor(fornecedor);

	}

	public void fechaModalItem(CloseEvent event) {
		disabledCamposForm = false;
		RequestContext.getCurrentInstance().update("form-add-produto");
		RequestContext.getCurrentInstance().execute("$(function(){PrimeFaces.focus('form-add-produto:quant-prod');});");
	}

	public void fechaModalPesquisaItemTelefone(CloseEvent event) {
		disabledCamposForm = false;
		telefone = new Telefone();
		telefone.setTipoTelefone(TipoTelefone.CELULAR);
		telefone.setDdd("047");
		txtBotaoInserir = "Adicionar";
		Collection<String> updates = Arrays.asList("form-fornecedor", "form-add-telefone");
		RequestContext.getCurrentInstance().update(updates);
		RequestContext.getCurrentInstance().execute("$(function(){PrimeFaces.focus('form-fornecedor:email');});");
	}

	public void fechaModalCategoria() {
		disabledCamposFormProduto = false;
	}

	public void abreModalItem() {
		disabledCamposForm = true;
	}

	public void abreModalItemParaAdicionarItem() {
		produto = new Produto();
		if(categorias == null) {
			categorias = modelCategoria.getObjDAO().listAllSelectOneMenu();
		}
		disabledCamposForm = true;
	}

	public void abreModalItemTelefone() {
		fornecedor.setNome(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("form-fornecedor:nome-fornecedor"));
		fornecedor.setEmail(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("form-fornecedor:email"));
		disabledCamposForm = true;

		if ("Adicionar".equals(txtBotaoInserir)) {
			guardaIndexItemTelefone = -1;
		}
	}

	// tela de cadastro
	public void abreModalCategoria() {
		categoria = new Categoria();
		abreModalCategoriaPesquisa();
	}

	public void abreModalCategoriaPesquisa() {
		disabledCamposFormProduto = true;
		
		produto.setDescricao(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("form-produto:descricao"));
		if(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("form-produto:definir-valor").equalsIgnoreCase("false")) {
			defineValor = false;
			if(!"".equalsIgnoreCase(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("form-produto:percentual_hinput"))) {
				produto.setPercentualLucro(new BigDecimal(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
						.get("form-produto:percentual_hinput")));
			}
		}
		else {
			defineValor = true;
			if(!"".equalsIgnoreCase(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("form-produto:valor-venda_hinput"))) {
				produto.setValorInformado(new BigDecimal(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
						.get("form-produto:valor-venda_hinput")));
			}
		}
		
	}

	public void calculaTotal() {
		totalSaida = new BigDecimal("0.00");
		for (ItemEntrada item : itemEntradas) {
			totalSaida = totalSaida.add(item.getSubTotal(), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);
		}
	}

	public void salvarProduto() {
		produto.setDescricao(produto.getDescricao().trim());
		if (defineValor) {
			produto.setPercentualLucro(null);
		} else {
			produto.setValorInformado(null);
		}
		produto.setCategoria(categoria);
		if(produto.getCategoria() != null && produto.getCategoria().getDescricao() != null && 
				!produto.getCategoria().getDescricao().toLowerCase().contains("celular".toLowerCase())) {
			produto.setImei(null);
		}
		produtoDAO.salvar(produto);
		if (produtoDAO.isControle()) {
			produto = produtoDAO.getEntidade();
			descricaoProduto = produtoDAO.getEntidade().getCodDescricao();
			modelProduto.setProduto(produtoDAO.getEntidade());
			modelProduto.addLista();
			categoria = new Categoria();
			request.addCallbackParam("sucessoProduto", true);
			RequestContext.getCurrentInstance().execute("PF('cadProduto').hide();");
		}
	}

	public void mostrarFormProd() {
		mostrarFormProduto = true;
	}

	// Nao retorna string, pois nao tera necessidade de voltar para pagina com a
	// lista de orcamentos
	public void salvar() {

		// Verifica se lista de itens eh igual a zero
		if ((itemEntradas.isEmpty() || itemEntradas == null || itemEntradas.size() == 0)) {
			FacesUtil.addWarnMessage("A lista de itens não pode ser vazia!", "");
		} else {
			entradaDeProduto.setCodigoNota(
					"".equals(entradaDeProduto.getCodigoNota()) ? null : entradaDeProduto.getCodigoNota());
			entradaDeProduto.setItemEntradas(itemEntradas);
			entradaDeProdutosDAO.salvar(entradaDeProduto, itemEntradas);
			if (entradaDeProdutosDAO.isControle()) {
				entradaDeProduto = new EntradaDeProdutos();
				entradaDeProduto.setData(new Date());
				itemEntrada = new ItemEntrada();
				itemEntradas = new ArrayList<>();
				idEntrada = null;
				nomeFornecedor = null;
				totalSaida = new BigDecimal("0.00");
				modelItemEntrada.limpaParametros();
			}
		}
	}

	public void excluir() {
		entradaDeProdutosDAO.excluir(entradaDeProduto.getId());
		if (entradaDeProdutosDAO.isControle()) {
			entradaDeProduto = new EntradaDeProdutos();
			itemEntrada = new ItemEntrada();
			itemEntradas = new ArrayList<>();
			idEntrada = null;
			totalSaida = new BigDecimal("0.00");
		}
	}

	public void salvarNaListaDeTelefones() {

		if (!Validacoes.verificaTelefone(telefone, guardaIndexItemTelefone, telefones)) {
			// verifica se o número veio para o back do tipo 88888-888
			if (telefone.getNumero() != null && telefone.getNumero().length() == 9) {
				String[] telefoneDividioPorHifen = telefone.getNumero().split("-");
				if (telefoneDividioPorHifen.length == 2 && telefoneDividioPorHifen[1].length() == 3)
					telefone.setNumero(telefoneDividioPorHifen[0].substring(0, 4) + "-"
							+ telefoneDividioPorHifen[0].substring(4, 5) + telefoneDividioPorHifen[1]);
			}

			if (guardaIndexItemTelefone == -1) {
				this.telefones.add(telefone);
				request.addCallbackParam("sucessoAdd", true);
			} else {
				this.telefones.set(guardaIndexItemTelefone, telefone);
				guardaIndexItemTelefone = -1;
				txtBotaoInserir = "Adicionar";
			}
			RequestContext.getCurrentInstance().execute("PF('addTelefone').hide();");
		} else {
			request.addCallbackParam("sucessoAdd", false);
			FacesUtil.addWarnMessage("Esse telefone já foi informado para este cliente!", "");
		}

	}

	public void alterarNaListaDeTelefones(Telefone t) {
		telefone = new Telefone();
		telefone.setTipoTelefone(t.getTipoTelefone());
		telefone.setDdd(t.getDdd());
		telefone.setNumero(t.getNumero());
		telefone.setRamal(t.getRamal());
		// pega indice do item na lista
		guardaIndexItemTelefone = telefones.indexOf(t);
		// muda texto do botão
		txtBotaoInserir = "Alterar";

		abreModalItemTelefone();
	}

	public void removerNaListaDeTelefones(Telefone i) {
		// pega indice do item na lista
		telefoneAlterar = new Telefone();
		telefoneAlterar.setNumero(i.getNumero());
		int index = telefones.indexOf(telefoneAlterar);
		this.telefones.remove(index);
	}

	public void salvarFornecedor() {
		try {
			fornecedor.setTelefones(telefones);
			fornecedorDAO.salvar(fornecedor);
			if (fornecedorDAO.isControle()) {
				entradaDeProduto.setFornecedor(fornecedorDAO.getEntidade());
				telefones.clear();
				request.addCallbackParam("sucesso", true);
				nomeFornecedor = fornecedorDAO.getEntidade().getCodDescricao();
				modelFornecedor.setFornecedor(fornecedorDAO.getEntidade());
				modelFornecedor.addLista();
				RequestContext.getCurrentInstance().execute("PF('addFornecedor').hide();");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void salvarCategoria() {
		
		modelCategoria.getObjDAO().salvar(categoria);
		if (modelCategoria.getObjDAO().isControle()) {
			categoria = modelCategoria.getObjDAO().getEntidade();
			if(categorias == null) {
				categorias = modelCategoria.getObjDAO().listAllSelectOneMenu();
			}
			categorias.add(categoria);
			RequestContext.getCurrentInstance().execute("PF('addCategoria').hide();");
		}
	}

	public void selecionarFornecedor(SelectEvent event) {
		fornecedor = modelFornecedor.busca(nomeFornecedor);
		entradaDeProduto.setFornecedor(fornecedor);
		nomeFornecedor = null;
	}
	
	public void selcionarProduto(SelectEvent event) {
		produto = modelProduto.busca(descricaoProduto);
		if(produto.getCategoria() != null && !produto.getCategoria().getDescricao().toLowerCase().contains("celular")) {
			itemEntrada.setImei(null);
		}
	}

	public String salvarNaListaDeItens() {
		
		produto = modelProduto.busca(descricaoProduto);

		if (Validacoes.verificaProdutoEntrada(produto, itemEntrada.getImei(), guardaIndexItemProd, itemEntradas)) {
			FacesUtil.addWarnMessage("Esse produto já foi informado para essa Entrada de produto(s)!", "");
			return null;
		}
		if (itemEntrada.getImei() != null && !itemEntrada.getImei().equals("000000-00-000000-0") && 
				itemEntradaDAO.verificaImeiJaEstaCadastrado(entradaDeProduto.getId(), itemEntrada.getImei())) {
			FacesUtil.addWarnMessage("Este IMEI já está cadastrado em outra Entrada e não foi dado Saída para o mesmo!", "");
			return null;
		}
		if (itemEntrada.getValorUnit() != null) {
			itemEntrada.setProduto(produto);
			double num = itemEntrada.getValorUnit().doubleValue();

			if (num >= 0) {
				if (guardaIndexItemProd == -1) {
					this.itemEntradas.add(itemEntrada);
					request.addCallbackParam("sucessoAddProduto", true);
				} else {
					this.itemEntradas.set(guardaIndexItemProd, itemEntrada);
					guardaIndexItemProd = -1;
					txtBotaoInserir = "Adicionar";
					txtTitleBotaoInserir = "Adicionar à lista de peças";
					request.addCallbackParam("sucessoAddProduto", true);
				}
				modelItemEntrada.parametros.put("listaDeItens", itemEntradas);
				/*
				 * itemEntrada = new ItemEntrada(); produto = new Produto();
				 * itemEntrada.setQuant(1); itemEntrada.setValorUnit(new BigDecimal("0.00"));
				 * txtBotaoInserir = "Adicionar";
				 */
				calculaTotal();
				RequestContext.getCurrentInstance().execute("PF('addProduto').hide();");
			}
		}
		return null;
	}

	public void preparaInclusao() {
		itemEntrada = new ItemEntrada();
		produto = new Produto();
		itemEntrada.setQuant(1);
		itemEntrada.setValorUnit(new BigDecimal("0.00"));
		itemEntrada.setImei(null);
		descricaoProduto = null;
		guardaIndexItemProd = -1;
		txtBotaoInserir = "Adicionar";
		// RequestContext.getCurrentInstance().execute("PF('addProduto').show();");
	}

	public void removerNaListaDeItens(ItemEntrada i) {
		if (i.getId() != null) {
			itemEntradas.removeIf(x -> i.getId().equals(x.getId()));
		} else {
			itemEntradas.removeIf(x -> x.getProduto().getId().equals(i.getProduto().getId()));
		}
		modelItemEntrada.parametros.put("listaDeItens", itemEntradas);
		calculaTotal();
	}

	public void alterarNaListaDeItens(ItemEntrada i) {
		itemEntrada = new ItemEntrada();
		produto = new Produto();
		itemEntrada.setId(i.getId());
		itemEntrada.setEntradaDeProdutos(entradaDeProduto);
		itemEntrada.setProduto(i.getProduto());
		itemEntrada.setQuant(i.getQuant());
		itemEntrada.setValorUnit(i.getValorUnit());
		itemEntrada.setImei(i.getImei());
		produto = i.getProduto();
		descricaoProduto = produto.getCodDescricao();
		/*caso o objeto selecionado não exista na lista, ele terá que ser adicionado, 
		para depois fazer na validação na hora que o método salvarNaLista ser chamado*/
		if(!modelProduto.getLista().contains(produto)) {
			modelProduto.setProduto(produto);
			modelProduto.addLista();
		}
		// pega indice do item na lista
		guardaIndexItemProd = itemEntradas.indexOf(i);
		// muda texto do botão
		txtBotaoInserir = "Alterar";
		txtTitleBotaoInserir = "Alterar item da lista";
		calculaTotal();
	}
		
	public List<String> filtroFornecedor(String txt){
		return modelFornecedor.filtro(txt, 15);
	}
	
	public List<String> filtroProduto(String txt){
		modelProduto.setComEntradaGerada(false);
		return modelProduto.filterTrazendoListas(txt, 15);
	}

	public Long getIdEntrada() {
		return idEntrada;
	}

	public void setIdEntrada(Long idEntrada) {
		this.idEntrada = idEntrada;
	}

	public String getPessoa() {
		return pessoa;
	}

	public void setPessoa(String pessoa) {
		this.pessoa = pessoa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public List<Produto> getProdutos() {
		return produtos;
	}

	public void setProdutos(List<Produto> produtos) {
		this.produtos = produtos;
	}

	public boolean isMostrarFormProduto() {
		return mostrarFormProduto;
	}

	public void setMostrarFormProduto(boolean mostrarFormProduto) {
		this.mostrarFormProduto = mostrarFormProduto;
	}

	public String getTxtBotaoInserir() {
		return txtBotaoInserir;
	}

	public void setTxtBotaoInserir(String txtBotaoInserir) {
		this.txtBotaoInserir = txtBotaoInserir;
	}

	public String getDescricaoProduto() {
		return descricaoProduto;
	}

	public void setDescricaoProduto(String descricaoProduto) {
		this.descricaoProduto = descricaoProduto;
	}

	public EntradaDeProdutos getEntradaDeProduto() {
		return entradaDeProduto;
	}

	public void setEntradaDeProduto(EntradaDeProdutos entradaDeProduto) {
		this.entradaDeProduto = entradaDeProduto;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public ItemEntrada getItemEntrada() {
		return itemEntrada;
	}

	public void setItemEntrada(ItemEntrada itemEntrada) {
		this.itemEntrada = itemEntrada;
	}

	public List<Fornecedor> getFornecedorFiltrados() {
		return fornecedorFiltrados;
	}

	public void setFornecedorFiltrados(List<Fornecedor> fornecedorFiltrados) {
		this.fornecedorFiltrados = fornecedorFiltrados;
	}

	public List<ItemEntrada> getItemEntradas() {
		return itemEntradas;
	}

	public void setItemEntradas(List<ItemEntrada> itemEntradas) {
		this.itemEntradas = itemEntradas;
	}

	public BigDecimal getTotalSaida() {
		return totalSaida;
	}

	public void setTotalSaida(BigDecimal totalSaida) {
		this.totalSaida = totalSaida;
	}

	public String getTxtTitleBotaoInserir() {
		return txtTitleBotaoInserir;
	}

	public void setTxtTitleBotaoInserir(String txtTitleBotaoInserir) {
		this.txtTitleBotaoInserir = txtTitleBotaoInserir;
	}

	public Produto getSelected() {
		return selected;
	}

	public void setSelected(Produto selected) {
		this.selected = selected;
	}

	public List<FormaPagamento> getListaEnumFormaPagamento() {
		return listaEnumFormaPagamento;
	}

	public void setListaEnumFormaPagamento(List<FormaPagamento> listaEnumFormaPagamento) {
		this.listaEnumFormaPagamento = listaEnumFormaPagamento;
	}

	public Telefone getTelefone() {
		return telefone;
	}

	public void setTelefone(Telefone telefone) {
		this.telefone = telefone;
	}

	public List<Telefone> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

	public List<TipoTelefone> getListaEnumTipoTelefone() {
		return listaEnumTipoTelefone;
	}

	public void setListaEnumTipoTelefone(List<TipoTelefone> listaEnumTipoTelefone) {
		this.listaEnumTipoTelefone = listaEnumTipoTelefone;
	}

	public double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public boolean isDisabledCamposForm() {
		return disabledCamposForm;
	}

	public void setDisabledCamposForm(boolean disabledCamposForm) {
		this.disabledCamposForm = disabledCamposForm;
	}

	public FornecedorLazyDataModel getModelFornecedor() {
		return modelFornecedor;
	}

	public void setModelFornecedor(FornecedorLazyDataModel modelFornecedor) {
		this.modelFornecedor = modelFornecedor;
	}

	public ProdutoLazyDataModel getModelProduto() {
		return modelProduto;
	}

	public void setModelProduto(ProdutoLazyDataModel modelProduto) {
		this.modelProduto = modelProduto;
	}

	public ItemEntradaLazyDataModel getModelItemEntrada() {
		return modelItemEntrada;
	}

	public void setModelItemEntrada(ItemEntradaLazyDataModel modelItemEntrada) {
		this.modelItemEntrada = modelItemEntrada;
	}

	public boolean isDefineValor() {
		return defineValor;
	}

	public void setDefineValor(boolean defineValor) {
		this.defineValor = defineValor;
	}

	public Long getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Long idProduto) {
		this.idProduto = idProduto;
	}

	public CategoriaLazyDataModel getModelCategoria() {
		return modelCategoria;
	}

	public void setModelCategoria(CategoriaLazyDataModel modelCategoria) {
		this.modelCategoria = modelCategoria;
	}

	public String getDescricaoCategoria() {
		return descricaoCategoria;
	}

	public void setDescricaoCategoria(String descricaoCategoria) {
		this.descricaoCategoria = descricaoCategoria;
	}

	public boolean isDisabledCamposFormProduto() {
		return disabledCamposFormProduto;
	}

	public void setDisabledCamposFormProduto(boolean disabledCamposFormProduto) {
		this.disabledCamposFormProduto = disabledCamposFormProduto;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getNomeFornecedor() {
		return nomeFornecedor;
	}

	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}

}
