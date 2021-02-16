package com.alex.servico;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.alex.DAO.ItemEntradaDAO;
import com.alex.DAO.ItemProdSaidaDAO;
import com.alex.DAO.ItemProdutoDAO;
import com.alex.DAO.ProdutoDAO;
import com.alex.modelo.ItemEntrada;
import com.alex.modelo.ItemProdSaida;
import com.alex.modelo.ItemProduto;
import com.alex.modelo.OrdemDeServico;
import com.alex.modelo.Produto;
import com.alex.util.FacesUtil;
import com.alex.util.OperacoesComBigDecimal;

public class ProdutoLazyDataModel extends LazyDataModel<Produto> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
    private ServicoAutocomplete servicoAutocomplete;

	private Optional<Produto> objOptional = null;

	private Long idObj;

	private List<Produto> lista = new ArrayList<Produto>();
	
	private List<ItemEntrada> itemEntradas = new ArrayList<ItemEntrada>();
	
	private List<ItemProdSaida> itemProdSaidas = new ArrayList<ItemProdSaida>();
	
	private List<ItemProduto> itemProdutos = new ArrayList<ItemProduto>();

	private List<Produto> listaAux = new ArrayList<Produto>();

	List<Produto> produtosInseridosNoPedido = new ArrayList<>();

	@Inject
	private ProdutoDAO objDAO;

	@Inject
	private Produto produto;

	public Map<String, Object> parametros = new HashMap<String, Object>();

	public List<String> chaves = new ArrayList<String>();

	private int contador = 0;

	public boolean limpaParametros = true;

	@Inject
	private ItemEntradaDAO itemEntradaDAO;

	@Inject
	private ItemProdSaidaDAO itemProdSaidaDAO;

	@Inject
	private ItemProdutoDAO itemProdutoDAO;
	
	private boolean pesquisaPorCodigo = true;
	
	private boolean comEntradaGerada;

	@SuppressWarnings("unchecked")
	@Override
	public List<Produto> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {

			objDAO.setFiltros(parametros);
			objDAO.setPrimeiroReg(objDAO.getQuantLinhas() != 0 ? first : 0);
			objDAO.setQuantRegistros(pageSize);

			if(chaves.contains("todos") && (chaves.contains("buscaValorPagoVendaLucro") 
					|| chaves.contains("buscaQuant"))) {
				lista = objDAO.listAllTrazendoListas(first, pageSize);
				lista = calculaValorPagoVendaLucro(lista);
				lista = calculaQuant(lista);
			}
			else if(chaves.contains("id") && (chaves.contains("buscaValorPagoVendaLucro") 
					|| chaves.contains("buscaQuant"))) {
				lista = objDAO.porIdTrazendoListas();
				lista = calculaValorPagoVendaLucro(lista);
				lista = calculaQuant(lista);
			}
			else if(chaves.contains("descricao") && (chaves.contains("buscaValorPagoVendaLucro") 
					|| chaves.contains("buscaQuant"))) {
				lista = objDAO.porDescricaoSemelhanteTrazendoListas();
				lista = calculaValorPagoVendaLucro(lista);
				lista = calculaQuant(lista);
			}
			else if(chaves.contains("descricaoCategoria") && (chaves.contains("buscaValorPagoVendaLucro") 
					|| chaves.contains("buscaQuant"))) {
				lista = objDAO.porDescricaoCategoriaSemelhanteTrazendoListas();
				lista = calculaValorPagoVendaLucro(lista);
				lista = calculaQuant(lista);
			}
			else if (chaves.contains("todos")) {
				lista = objDAO.listAll(first, pageSize);
			} else if (chaves.contains("id")) {
				lista = objDAO.porId();
			} else if (chaves.contains("descricaoComEntradaGerada")) {
				lista = objDAO.porDescricaoSemelhanteComEntradaGerada();
			}else if(chaves.contains("idComEntradaGerada")) {
				lista = objDAO.porIdComEntradaGerada();
			} else if (chaves.contains("descricao")) {
				lista = objDAO.porDescricaoSemelhante();
			} else if (chaves.contains("listaDeProdutos")) {
				listaAux = (List<Produto>) parametros.get("listaDeProdutos");
				objDAO.setQuantLinhas(listaAux.size());
				first = objDAO.getQuantLinhas() == 0 || first == objDAO.getQuantLinhas() ? 0 : first;
				contador = 0;
				lista.clear();
				for (Produto p : listaAux) {
					if (contador >= first && contador <= objDAO.getQuantLinhas()) {
						lista.add(p);
					}
					contador++;
				}
			}

			if (chaves.contains("buscaValorPagoVendaLucro")) {

				
			}

			// calcula quant
			if (chaves.contains("buscaQuant")) {
				
			}

			lista.forEach(p -> {
				p.setIncluidaNoPedido(false);
			});

			produtosInseridosNoPedido.forEach(pQueEstaNoPedido -> {
				lista.forEach(p -> {
					if (pQueEstaNoPedido.getId().equals(p.getId())) {
						p.setIncluidaNoPedido(true);
					}
				});
			});

			if (objDAO.getQuantLinhas() <= 0) {
				FacesUtil.addWarnMessage(FacesUtil.mensagemPadraoRegistroNaoEncontrado(), "");
			}

			setRowCount(objDAO.getQuantLinhas());

		} else {
			setRowCount(0);
			lista.clear();
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Produto getRowData(String rowKey) {
		lista = (List<Produto>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				produto = objOptional.get();
			}
		}

		return objOptional.orElse(new Produto());

	}

	@Override
	public Object getRowKey(Produto obj) {
		return obj != null ? obj.getId() : null;
	}

	public void aplicaChavesParametrosUtilizados() {
		if (chaves != null) {
			chaves.clear();
		}

		for (String chave : parametros.keySet()) {
			if (parametros.get(chave) != null) {
				chaves.add(chave);
			}
		}
	}

	public void limpaParametros() {
		objDAO.setQuantLinhas(0);
		for (String chave : parametros.keySet()) {
			parametros.put(chave, null);
		}
	}

	public List<Produto> calculaValorPagoVendaLucro(List<Produto> list) {
		AtomicReference<ItemEntrada> i = new AtomicReference<ItemEntrada>(null);
		list.forEach(p -> {
			
			i.set(itemEntradaDAO.retornaValorDaUltimaEntradaProd(p.getId()));

			if (i.get() != null || p.getValorInformado() != null) {

				p.setValorPago(i.get() != null ? i.get().getValorUnit() : BigDecimal.ZERO);

				if (p.getValorInformado() != null) {
					p.setValorSugerido(p.getValorInformado());
				}						
				else {
					// INÍCIO - calcula valor sugerido
					p.setValorSugerido(i.get().getValorUnit());
					p.setPercentualLucro(OperacoesComBigDecimal.divisao(p.getPercentualLucro(),
							BigDecimal.TEN.multiply(BigDecimal.TEN)));
					p.setValorSugerido(OperacoesComBigDecimal.soma(
							OperacoesComBigDecimal.multiplicacao(p.getValorSugerido(), p.getPercentualLucro()),
							i.get().getValorUnit()));
					
					p.setPercentualLucro(OperacoesComBigDecimal.multiplicacao(p.getPercentualLucro(),
							BigDecimal.TEN.multiply(BigDecimal.TEN)));
					// FIM - calcula valor sugerido
				}
				
				p.setLucro(OperacoesComBigDecimal.subtracao(p.getValorSugerido(), p.getValorPago()));

				if (p.getValorPago() == null) {
					p.setValorPago(BigDecimal.ZERO);
				}
				if (p.getValorSugerido() == null) {
					p.setValorSugerido(BigDecimal.ZERO);
				}
				if (p.getLucro() == null) {
					p.setLucro(BigDecimal.ZERO);
				}

				// se atender essa condição quer dizer que o valor de venda não foi definido
				// corretamente
				if (OperacoesComBigDecimal.numUmMaiorOuIgualNumDois(p.getValorPago(), p.getValorSugerido())) {
					p.setValorSugerido(BigDecimal.ZERO);
					p.setLucro(BigDecimal.ZERO);
				}
			}
		});
		return list;
	}
	
	public List<Produto> calculaQuant(List<Produto> list) {
		OrdemDeServico ordemDeServico = new OrdemDeServico();
		list.forEach(p -> {

			/*
			 * if (p.getItemProdSaidas() == null || p.getItemProdSaidas().size() == 0) {
			 * p.setItemProdSaidas(itemProdSaidaDAO.porIdProduto(p.getId())); }
			 * 
			 * if (p.getItemProdutos() == null || p.getItemProdutos().size() == 0) {
			 * p.setItemProdutos(itemProdutoDAO.porIdProduto(p.getId())); }
			 * 
			 * if (p.getItemEntradas() == null || p.getItemEntradas().size() == 0) {
			 * p.setItemEntradas(itemEntradaDAO.porIdProduto(p.getId())); }
			 */

			p.setQuant(p.getItemEntradas().stream().map(ItemEntrada::getQuant).reduce(0, Integer::sum));
			p.setQuant(p.getQuant()
					- (p.getItemProdSaidas().stream().map(ItemProdSaida::getQuant).reduce(0, Integer::sum)));
			
			//itens onde o estado da ordem está como ,GARANTIA_CONCLUIDA, GARANTIA_ANDAMENTO, APROVADO, FINALIZADO
			p.setItemProdutos(p.getItemProdutos().stream()
					.filter(line -> ordemDeServico.listaEnumTiraEstoque().contains(line.getOrdemDeServico().getEstado().getCod()))
		            .collect(Collectors.toList()));
			
			p.setQuant(p.getQuant()
					- (p.getItemProdutos().stream().map(ItemProduto::getQuant).reduce(0, Integer::sum)));
		});
		return list;
	}
	
	public List<String> filterTrazendoListas(String query, int quantRegistros){
		if(objDAO.getListaString() == null) {
			objDAO.setListaString(new ArrayList<String>());
		}
		else {
			objDAO.getListaString().clear();
		}
		objDAO.setQuantRegistros(quantRegistros);
		for (String chave : objDAO.getFiltros().keySet()) {
			objDAO.getFiltros().put(chave, null);
		}
		//SE QUERY ESTIVER DO TIPO 000001 - Teste, transforma em 000001
		if(query.matches("\\d+ - .*")) {
			query = query.replaceAll(" -.*", "");
		}
		//limpaParametros();
		//objDAO.setFiltros(parametros);
		if(StringUtils.isNumericSpace(query)) {
			query = query.replace(" ", "");
			if(comEntradaGerada) {
				objDAO.adicionaFiltro("idComEntradaGerada", Long.parseLong(query));
				lista = objDAO.porIdComEntradaGerada();
			}
			else {
				objDAO.adicionaFiltro("id", Long.parseLong(query));
				lista = objDAO.porIdTrazendoListas(); 
			}
		}
		else {
			if(comEntradaGerada) {
				objDAO.adicionaFiltro("descricaoComEntradaGerada", ServicoGenerico.montaPesquisaInteligente(query, true));
				lista = objDAO.porDescricaoSemelhanteComEntradaGerada();
			}
			else {
				objDAO.adicionaFiltro("descricao", ServicoGenerico.montaPesquisaInteligente(query, true));
				lista = objDAO.porDescricaoSemelhanteTrazendoListas(); 
			}
		}		
		
		lista.forEach(obj -> {
			this.itemEntradas.addAll(obj.getItemEntradas());
			this.itemProdSaidas.addAll(obj.getItemProdSaidas());
			this.itemProdutos.addAll(obj.getItemProdutos());
			objDAO.getListaString().add(obj.getCodDescricao());
		});
		return objDAO.getListaString();
    }
	
	public Produto busca(String pesquisa) {
		idObj = ServicoGenerico.retornaId(pesquisa);
		
		if(lista != null && lista.size() > 0) {
			
			lista = lista.stream()                // convert list to stream
					.filter(line -> line.getId().equals(idObj))     
					.collect(Collectors.toList());
			if(lista != null && lista.size() > 0) {
				this.itemEntradas = this.itemEntradas.stream()                
						.filter(line -> line.getProduto().getId().equals(lista.get(0).getId()))    
						.collect(Collectors.toList());
				this.itemProdSaidas = this.itemProdSaidas.stream()                
						.filter(line -> line.getProduto().getId().equals(lista.get(0).getId()))    
						.collect(Collectors.toList());
				this.itemProdutos = this.itemProdutos.stream()                
						.filter(line -> line.getProduto().getId().equals(lista.get(0).getId()))    
						.collect(Collectors.toList());
				this.lista.get(0).setItemEntradas(this.itemEntradas);
				this.lista.get(0).setItemProdSaidas(this.itemProdSaidas);
				this.lista.get(0).setItemProdutos(this.itemProdutos);
				return lista.get(0);
			}
		}
		//se a pesquisa ser feita pela segunda vez e tiver no cache, a lista será vazia, por isso tem que preencher ela de volta
		lista = objDAO.porIdComEntradaCadastrada(idObj);
		if(lista != null && lista.size() > 0) {
			this.lista.get(0).setItemEntradas(this.itemEntradas);
			this.lista.get(0).setItemProdSaidas(this.itemProdSaidas);
			this.lista.get(0).setItemProdutos(this.itemProdutos);
			return lista.get(0);
		}
		
		return null;
	}
	
	public void addLista() {
		lista.add(this.produto);
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public ProdutoDAO getObjDAO() {
		return objDAO;
	}

	public void setObjDAO(ProdutoDAO objDAO) {
		this.objDAO = objDAO;
	}

	public List<Produto> getProdutosInseridosNoPedido() {
		return produtosInseridosNoPedido;
	}

	public void setProdutosInseridosNoPedido(List<Produto> produtosInseridosNoPedido) {
		this.produtosInseridosNoPedido = produtosInseridosNoPedido;
	}

	public ItemEntradaDAO getItemEntradaDAO() {
		return itemEntradaDAO;
	}

	public ItemProdSaidaDAO getItemProdSaidaDAO() {
		return itemProdSaidaDAO;
	}

	public void setItemProdSaidaDAO(ItemProdSaidaDAO itemProdSaidaDAO) {
		this.itemProdSaidaDAO = itemProdSaidaDAO;
	}

	public ItemProdutoDAO getItemProdutoDAO() {
		return itemProdutoDAO;
	}

	public void setItemProdutoDAO(ItemProdutoDAO itemProdutoDAO) {
		this.itemProdutoDAO = itemProdutoDAO;
	}

	public void setItemEntradaDAO(ItemEntradaDAO itemEntradaDAO) {
		this.itemEntradaDAO = itemEntradaDAO;
	}

	public boolean isPesquisaPorCodigo() {
		return pesquisaPorCodigo;
	}

	public void setPesquisaPorCodigo(boolean pesquisaPorCodigo) {
		this.pesquisaPorCodigo = pesquisaPorCodigo;
	}

	public ServicoAutocomplete getServicoAutocomplete() {
		return servicoAutocomplete;
	}

	public List<ItemEntrada> getItemEntradas() {
		return itemEntradas;
	}

	public void setItemEntradas(List<ItemEntrada> itemEntradas) {
		this.itemEntradas = itemEntradas;
	}

	public List<ItemProdSaida> getItemProdSaidas() {
		return itemProdSaidas;
	}

	public void setItemProdSaidas(List<ItemProdSaida> itemProdSaidas) {
		this.itemProdSaidas = itemProdSaidas;
	}

	public List<ItemProduto> getItemProdutos() {
		return itemProdutos;
	}

	public void setItemProdutos(List<ItemProduto> itemProdutos) {
		this.itemProdutos = itemProdutos;
	}

	public boolean isComEntradaGerada() {
		return comEntradaGerada;
	}

	public void setComEntradaGerada(boolean comEntradaGerada) {
		this.comEntradaGerada = comEntradaGerada;
	}

	public List<Produto> getLista() {
		return lista;
	}

	public void setLista(List<Produto> lista) {
		this.lista = lista;
	}

}
