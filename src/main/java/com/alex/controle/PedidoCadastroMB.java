package com.alex.controle;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;

import com.alex.DAO.PedidoDAO;
import com.alex.DAO.ProdutoDAO;
import com.alex.modelo.Categoria;
import com.alex.modelo.ItemPedido;
import com.alex.modelo.Pedido;
import com.alex.modelo.Produto;
import com.alex.modelo.SemInformacoes;
import com.alex.modelo.Telefone;
import com.alex.modelo.Usuario;
import com.alex.modelo.enums.EnumSimNaoGenerico;
import com.alex.modelo.enums.TipoTelefone;
import com.alex.modelo.validacao.Validacoes;
import com.alex.relatorios.GeradorRelatorio;
import com.alex.servico.CategoriaLazyDataModel;
import com.alex.servico.ItemPedidoLazyDataModel;
import com.alex.servico.ProdutoLazyDataModel;
import com.alex.util.FacesUtil;

import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class PedidoCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;

	@Inject
	private Pedido pedido;

	@Inject
	private PedidoDAO pedidoDAO;

	@Inject
	private ProdutoDAO produtoDAO;

	@Inject
	private Produto produto;
	
	@Inject
	private Categoria categoria;

	@Inject
	private ItemPedido itemPedido;

	private Long idPedido;

	private String pessoa = "Física";

	private List<ItemPedido> itemPedidos = new ArrayList<>();

	private List<Produto> produtos = new ArrayList<>();
	
	List<SemInformacoes> semInformacoes = new ArrayList<>();
	
	private List<Categoria> categorias;

	RequestContext request = RequestContext.getCurrentInstance();

	private String nome;// nome cliente

	private String descricaoProduto;
	
	private String descricaoCategoria;
	
	private Long idProduto;

	private boolean mostrarFormProduto = false;
	
	private boolean disabledCamposFormProduto = false;

	private String txtBotaoInserir = "Adicionar";

	private int guardaIndexItemProd = -1;// pq tem o indice 0

	private String txtTitleBotaoInserir = "Adicionar à lista de peças";
	
	private List<EnumSimNaoGenerico> listaEnumSiNaoGenerico = Arrays.asList(EnumSimNaoGenerico.values());
	
	@Inject
	private Produto selected;

	@Inject
	private Telefone telefone;

	private double valorUnitario = 0.00;

	private boolean disabledCamposForm = false;

	@Inject
	private ProdutoLazyDataModel modelProduto;

	@Inject
	private ItemPedidoLazyDataModel modelItemPedido;
	
	@Inject
	private CategoriaLazyDataModel modelCategoria;
	
	private boolean defineValor = false;

	public void inicializar() {

		if (idPedido != null) {
			pedido = pedidoDAO.porID(idPedido);
			pedido = pedido == null ? new Pedido() : pedido;
			if (pedido.getId() != null) {

				itemPedidos = pedido.getItemPedidos();
				modelItemPedido.parametros.put("listaDeItens", itemPedidos);

				filtros.put("parametro01", idPedido);
				filtros.put("entidadeId", "pedido");
			}
			idPedido = null;
		}
	}

	@PostConstruct
	public void init() {
		produto.setDescricao("");
		produto.setPercentualLucro(new BigDecimal("0.00"));
		itemPedidos = new ArrayList<>();
		telefone.setTipoTelefone(TipoTelefone.CELULAR);
		telefone.setDdd("047");
	}

	@Override
	public String getTitulo() {
		return "Pedido";
	}

	@Override
	public String getLinkBreadCrumb() {
		if (filtros.get("parametro01") != null) {
			return "/pages/pedido/cadastro-pedido.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/pedido/cadastro-pedido.jsf";
	}

	@Override
	public String caminhoCancelar() {
		return "/pages/pedido/lista-pedido?faces-redirect=true";
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

	public void salvarProduto() {
		produto.setDescricao(produto.getDescricao().trim());
		if(defineValor) {
			produto.setPercentualLucro(null);
		}
		else {
			produto.setValorInformado(null);
		}
		produto.setCategoria(categoria);
		if (produto.getCategoria() != null && 
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

	public void mostrarFormProd() {
		mostrarFormProduto = true;
	}

	// Nao retorna string, pois nao tera necessidade de voltar para pagina com a
	// lista de orcamentos
	public void salvar() {

		// Verifica se lista de itens eh igual a zero
		if ((itemPedidos.isEmpty() || itemPedidos == null || itemPedidos.size() == 0)) {
			FacesUtil.addWarnMessage("A lista de itens não pode ser vazia!", "");
		} else {

			pedido.setItemPedidos(itemPedidos);
			pedidoDAO.salvar(pedido, itemPedidos);
			if (pedidoDAO.isControle()) {
				pedido = new Pedido();
				itemPedido = new ItemPedido();
				itemPedidos = new ArrayList<>();
				idPedido = null;
				modelProduto.limpaParametros();
				modelItemPedido.limpaParametros();
			}
		}
	}

	public void excluir() {
		pedidoDAO.excluir(pedido.getId());
		if (pedidoDAO.isControle()) {
			pedido = new Pedido();
			itemPedido = new ItemPedido();
			itemPedidos = new ArrayList<>();
			idPedido = null;
		}
	}

	public void salvarNaListaDeItens() {
		
		produto = modelProduto.busca(descricaoProduto);

		if (!Validacoes.verificaProdutoPedido(produto, guardaIndexItemProd, itemPedidos)) {
			itemPedido.setProduto(produto);
			itemPedido.setData(new Date());
			itemPedido.setUsuario((Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario"));
			if (guardaIndexItemProd == -1) {
				this.itemPedidos.add(itemPedido);
				request.addCallbackParam("sucessoAddProduto", true);
			} else {
				this.itemPedidos.set(guardaIndexItemProd, itemPedido);
				guardaIndexItemProd = -1;
				txtBotaoInserir = "Adicionar";
				txtTitleBotaoInserir = "Adicionar à lista de peças";
				request.addCallbackParam("sucessoAddProduto", true);

			}
			modelItemPedido.parametros.put("listaDeItens", itemPedidos);
			RequestContext.getCurrentInstance().execute("PF('addProduto').hide();");
		} else {
			FacesUtil.addWarnMessage("Esse produto já foi informado para esse Pedido!", "");
		}
	}

	public void preparaInclusao() {
		itemPedido = new ItemPedido();
		produto = new Produto();
		itemPedido.setQuant(1);
		descricaoProduto = null;
		guardaIndexItemProd = -1;
		txtBotaoInserir = "Adicionar";
		// RequestContext.getCurrentInstance().execute("PF('addProduto').show();");
	}

	public void removerNaListaDeItens(ItemPedido i) {
		if (i.getId() != null) {
			itemPedidos.removeIf(x -> i.getId().equals(x.getId()));
		} else {
			itemPedidos.removeIf(x -> x.getProduto().getId().equals(i.getProduto().getId()));
		}
		modelItemPedido.parametros.put("listaDeItens", itemPedidos);
	}

	public void alterarNaListaDeItens(ItemPedido i) {
		itemPedido = new ItemPedido();
		produto = new Produto();
		itemPedido.setId(i.getId());
		itemPedido.setPedido(pedido);
		itemPedido.setProduto(i.getProduto());
		itemPedido.setQuant(i.getQuant());
		produto = i.getProduto();
		descricaoProduto = produto.getCodDescricao();
		/*caso o objeto selecionado não exista na lista, ele terá que ser adicionado, 
		para depois fazer na validação na hora que o método salvarNaLista ser chamado*/
		if(!modelProduto.getLista().contains(produto)) {
			modelProduto.setProduto(produto);
			modelProduto.addLista();
		}
		// pega indice do item na lista
		guardaIndexItemProd = itemPedidos.indexOf(i);
		// muda texto do botão
		txtBotaoInserir = "Alterar";
		txtTitleBotaoInserir = "Alterar item da lista";
	}
	
	public void gerarRelatorio() {
		
		String jasperPath = "/resources/relatorios/pedido.jasper";

		try {
			String filename = "EstoqueProdutos.pdf";
			if (itemPedidos.size() > 0) {
				GeradorRelatorio.PDF(null, jasperPath, itemPedidos, filename);
			} else {
				Map<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("titulo", "Pedido de produtos");
				parametros.put("subtitulo", "Detalhes do pedido.");
				jasperPath = "/resources/relatorios/sem_informacoes_parametros.jasper";
				semInformacoes.add(new SemInformacoes(null, null,
						"Nenhum registro foi encontrado.", false));
				GeradorRelatorio.PDF(parametros, jasperPath, semInformacoes, filename);
			}
		} catch (JRException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
	public List<String> filtroProduto(String txt){
		modelProduto.setComEntradaGerada(false);
		return modelProduto.filterTrazendoListas(txt, 15);
	}

	public Long getIdEntrada() {
		return idPedido;
	}

	public void setIdEntrada(Long idEntrada) {
		this.idPedido = idEntrada;
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

	public Telefone getTelefone() {
		return telefone;
	}

	public void setTelefone(Telefone telefone) {
		this.telefone = telefone;
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

	public ProdutoLazyDataModel getModelProduto() {
		return modelProduto;
	}

	public void setModelProduto(ProdutoLazyDataModel modelProduto) {
		this.modelProduto = modelProduto;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public ItemPedido getItemPedido() {
		return itemPedido;
	}

	public void setItemPedido(ItemPedido itemPedido) {
		this.itemPedido = itemPedido;
	}

	public Long getIdPedido() {
		return idPedido;
	}

	public void setIdPedido(Long idPedido) {
		this.idPedido = idPedido;
	}

	public List<ItemPedido> getItemPedidos() {
		return itemPedidos;
	}

	public void setItemPedidos(List<ItemPedido> itemPedidos) {
		this.itemPedidos = itemPedidos;
	}

	public ItemPedidoLazyDataModel getModelItemPedido() {
		return modelItemPedido;
	}

	public void setModelItemPedido(ItemPedidoLazyDataModel modelItemPedido) {
		this.modelItemPedido = modelItemPedido;
	}

	public List<EnumSimNaoGenerico> getListaEnumSiNaoGenerico() {
		return listaEnumSiNaoGenerico;
	}

	public void setListaEnumSiNaoGenerico(List<EnumSimNaoGenerico> listaEnumSiNaoGenerico) {
		this.listaEnumSiNaoGenerico = listaEnumSiNaoGenerico;
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

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public boolean isDisabledCamposFormProduto() {
		return disabledCamposFormProduto;
	}

	public void setDisabledCamposFormProduto(boolean disabledCamposFormProduto) {
		this.disabledCamposFormProduto = disabledCamposFormProduto;
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
