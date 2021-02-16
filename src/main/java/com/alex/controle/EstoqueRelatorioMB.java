package com.alex.controle;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.DAO.PedidoDAO;
import com.alex.modelo.ItemPedido;
import com.alex.modelo.Pedido;
import com.alex.modelo.Produto;
import com.alex.modelo.SemInformacoes;
import com.alex.modelo.TipoUsuario;
import com.alex.modelo.Usuario;
import com.alex.modelo.enums.EnumSimNaoGenerico;
import com.alex.relatorios.GeradorRelatorio;
import com.alex.servico.ProdutoLazyDataModel;
import com.alex.servico.ServicoGenerico;

import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class EstoqueRelatorioMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private PedidoDAO pedidoDAO;

	@Inject
	private Pedido pedido;

	@Inject
	private ItemPedido itemPedido;

	@Inject
	private Usuario usuario;

	List<Produto> produtos = new ArrayList<>();

	List<Produto> produtosInseridosNoPedido = new ArrayList<>();

	private List<ItemPedido> itemPedidos = new ArrayList<>();

	List<SemInformacoes> semInformacoes = new ArrayList<>();

	private String descricao;

	private Long id;

	@Inject
	private ProdutoLazyDataModel model;

	private String tipoPesquisa;

	private boolean visivelBotaoIncluirNoPedido;

	private boolean temPermissao = false;

	@PostConstruct
	public void init() {
		tipoPesquisa = "descricao";
		visivelBotaoIncluirNoPedido = false;

		pedido = pedidoDAO.retornaUltimoRegistroQueAindaNaoFoiConcluido();
		if (pedido != null) {
			itemPedidos = pedido.getItemPedidos();
			itemPedidos.forEach(i -> {
				produtosInseridosNoPedido.add(i.getProduto());
			});
			model.setProdutosInseridosNoPedido(produtosInseridosNoPedido);
		}
		usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
		TipoUsuario t = new TipoUsuario();
		t.setId(1L);
		if (usuario.getTipoUsuarios().contains(t)) {
			temPermissao = true;
		}
	}

	@Override
	public String getTitulo() {
		return "Emissão do relatório de estoque";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/relatorios/estoque-relatorio.jsf";
	}
	
	//não foi necessário criar uma instancia para categoria, pois a variavel descricao está sendo utilizada para os tipos de pesquisa: descricaCategoria descricao do produto
	public void criaInstanciaCategoria() {
		descricao = "";
	}

	public void pesquisar() {

		if (model.limpaParametros) {
			model.limpaParametros();
		} else {
			model.limpaParametros = true;
		}

		if (tipoPesquisa.equals("todos")) {
			model.parametros.put("todos", tipoPesquisa);
		} else if (tipoPesquisa.equals("codigo")) {
			model.parametros.put("id", id);
		} else if (tipoPesquisa.equals("descricao")) {
			model.parametros.put("descricao", ServicoGenerico.montaPesquisaInteligente(descricao, true));
		} else if (tipoPesquisa.equals("categoria")) {
			model.parametros.put("descricaoCategoria", ServicoGenerico.montaPesquisaInteligente(descricao, true));
		}

		model.parametros.put("buscaValorPagoVendaLucro", "buscaValorPagoVendaLucro");
		model.parametros.put("buscaQuant", "buscaQuant");
	}

	public void gerarRelatorio() {
		
		pegaValoresDaTela();
		
		// filtra
		if (tipoPesquisa.equals("todos")) {
			produtos = model.getObjDAO().listAll();
		} else if (tipoPesquisa.equals("codigo")) {
			produtos = model.getObjDAO().porId(id);
		} else if (tipoPesquisa.equals("descricao")) {
			produtos = model.getObjDAO()
					.porDescricaoSemelhanteTrazendoListas(ServicoGenerico.montaPesquisaInteligente(descricao, true));
		} else if (tipoPesquisa.equals("categoria")) {
			produtos = model.getObjDAO()
					.porDescricaoCategoriaSemelhanteTrazendoListas(ServicoGenerico.montaPesquisaInteligente(descricao, true));
		}

		String jasperPath = "/resources/relatorios/estoque_produtos.jasper";

		produtos = model.calculaValorPagoVendaLucro(produtos);
		produtos = model.calculaQuant(produtos);

		if (temPermissao) {
			jasperPath = "/resources/relatorios/estoque_produtos_admin.jasper";
		}

		try {
			String filename = "EstoqueProdutos.pdf";
			if (produtos.size() > 0) {
				GeradorRelatorio.PDF(null, jasperPath, produtos, filename);
			} else {
				jasperPath = "/resources/relatorios/sem_informacoes.jasper";
				semInformacoes.add(new SemInformacoes("Estoque de produtos", "Quantidade total de produtos em estoque.",
						"Nenhum registro foi encontrado.", false));
				GeradorRelatorio.PDF(null, jasperPath, semInformacoes, filename);
			}
		} catch (JRException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void pegaValoresDaTela() {

		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("pesquisa_input") != null) {
			tipoPesquisa = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("pesquisa_input");
		}

		// inputs que vem atualizados
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("codigo") != null) {
			id = Long.parseLong(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("codigo"));
		}
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("descricao") != null) {
			descricao = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("descricao");
		}
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("categoria") != null) {
			descricao = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("categoria");
		}

	}
	
	public void trataCampos() {
		if(tipoPesquisa.equals("codigo")) {
			descricao = null;
		}
		else if(tipoPesquisa.equals("categoria") || tipoPesquisa.equals("descricao")) {
			id = null;
		}
	}

	public void selecionarProduto(Produto p) {
		model.setProduto(new Produto());
		model.getProduto().setId(p.getId());
		model.getProduto().setDescricao(p.getDescricao());
		itemPedido = new ItemPedido();
		itemPedido.setProduto(model.getProduto());
		itemPedido.setQuant(1);
		itemPedido.setUsuario(usuario);
		itemPedido.setData(new Date());
	}

	public void incluirNoPedido() {
		itemPedidos.add(itemPedido);
		atualizaProdutosPedidos();
		visivelBotaoIncluirNoPedido = true;
		/*
		 * pedido.setConcluido(EnumSimNaoGenerico.NAO); itemPedidos.add(itemPedido);
		 * pedidoDAO.salvar(pedido, itemPedidos);
		 */
	}

	public void retirarDoPedido() {
		itemPedidos.removeIf(x -> x.getProduto().getId().equals(itemPedido.getProduto().getId()));
		visivelBotaoIncluirNoPedido = true;
		atualizaProdutosPedidos();
	}

	public void atualizaProdutosPedidos() {
		if (produtosInseridosNoPedido != null) {
			produtosInseridosNoPedido.clear();
		}
		itemPedidos.forEach(i -> {
			produtosInseridosNoPedido.add(i.getProduto());
		});
		model.setProdutosInseridosNoPedido(produtosInseridosNoPedido);
		model.limpaParametros = false;
		pesquisar();
	}

	public void salvarNoPedido() {

		pedido = pedidoDAO.retornaUltimoRegistroQueAindaNaoFoiConcluido();
		if (pedido != null) {
			pedido.setItemPedidos(itemPedidos);
			pedidoDAO.salvar(pedido, itemPedidos);
			if (pedidoDAO.isControle()) {
				visivelBotaoIncluirNoPedido = false;
			}
		} else { // caso não encontre um pedido que não foi finalizado, insere um novo
			pedidoDAO.getEntidade().setConcluido(EnumSimNaoGenerico.NAO);
			pedidoDAO.salvar(pedidoDAO.getEntidade(), itemPedidos);
			if (pedidoDAO.isControle()) {
				visivelBotaoIncluirNoPedido = false;
			}
		}

	}

	public void gerarRelatorioAction() {

		produtos = model.getObjDAO().listAll();

		String filename = "names.pdf";
		String jasperPath = "/resources/relatorios/estoque_produtos.jasper";
		try {
			GeradorRelatorio.PDF(null, jasperPath, produtos, filename);
		} catch (JRException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProdutoLazyDataModel getModel() {
		return model;
	}

	public ItemPedido getItemPedido() {
		return itemPedido;
	}

	public void setItemPedido(ItemPedido itemPedido) {
		this.itemPedido = itemPedido;
	}

	public boolean isVisivelBotaoIncluirNoPedido() {
		return visivelBotaoIncluirNoPedido;
	}

	public void setVisivelBotaoIncluirNoPedido(boolean visivelBotaoIncluirNoPedido) {
		this.visivelBotaoIncluirNoPedido = visivelBotaoIncluirNoPedido;
	}

	public boolean isTemPermissao() {
		return temPermissao;
	}

	public void setTemPermissao(boolean temPermissao) {
		this.temPermissao = temPermissao;
	}

}
