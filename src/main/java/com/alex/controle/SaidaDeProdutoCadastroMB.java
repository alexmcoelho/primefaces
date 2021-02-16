package com.alex.controle;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;

import com.alex.DAO.ClienteDAO;
import com.alex.DAO.ItemEntradaDAO;
import com.alex.DAO.ItemProdSaidaDAO;
import com.alex.DAO.SaidaDeProdutosDAO;
import com.alex.DAO.UsuarioDAO;
import com.alex.exception.ValidaCNPJ;
import com.alex.exception.ValidaCPF;
import com.alex.modelo.Cliente;
import com.alex.modelo.ItemEntrada;
import com.alex.modelo.ItemProdSaida;
import com.alex.modelo.ItemProduto;
import com.alex.modelo.OrdemDeServico;
import com.alex.modelo.Produto;
import com.alex.modelo.SaidaDeProdutos;
import com.alex.modelo.Telefone;
import com.alex.modelo.Usuario;
import com.alex.modelo.enums.FormaPagamento;
import com.alex.modelo.enums.QuatMesesGarantia;
import com.alex.modelo.enums.TipoTelefone;
import com.alex.modelo.validacao.Validacoes;
import com.alex.relatorios.GeradorRelatorio;
import com.alex.servico.ClienteLazyDataModel;
import com.alex.servico.ItemProdSaidaLazyDataModel;
import com.alex.servico.ProdutoLazyDataModel;
import com.alex.util.CarregaArquivo;
import com.alex.util.Dominio;
import com.alex.util.FacesUtil;
import com.alex.util.OperacoesComBigDecimal;

import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class SaidaDeProdutoCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;

	@Inject
	private SaidaDeProdutos saidaDeProdutos;

	@Inject
	private SaidaDeProdutosDAO saidaDeProdutosDAO;

	@Inject
	private ClienteDAO clienteDAO;

	@Inject
	private ItemProdSaidaDAO itemProdSaidaDAO;

	@Inject
	private ItemEntradaDAO itemEntradaDAO;

	@Inject
	private Cliente cliente;

	@Inject
	private Usuario usuario;

	@Inject
	private Produto produto;

	@Inject
	private ItemProdSaida itemProduto;

	@Inject
	private UsuarioDAO usuarioDAO;
	
	@Inject
	private ItemProdSaidaDAO iItemProdSaidaDAO;

	private Long idSaida;

	private String pessoa = "Física";

	private List<Usuario> usuarios = new ArrayList<>();

	private List<Cliente> clientesFiltrados;

	private List<ItemProdSaida> itemProdSaidas = new ArrayList<>();

	private List<Produto> produtos = new ArrayList<>();

	RequestContext request = RequestContext.getCurrentInstance();

	private String nome;// nome cliente

	private String descricaoProduto;
	
	private Long idProduto;

	private boolean mostrarFormProduto = false;

	private String txtBotaoInserir = "Adicionar";

	private int guardaIndexItemProd = -1;// pq tem o indice 0

	private String txtTitleBotaoInserir = "Adicionar à lista de peças";

	private BigDecimal totalSaida = new BigDecimal("0.00");

	// verifica se o valor informado pelo usuário é menor do que foi registrado na
	// última entrada desse produto
	private BigDecimal ultimoValorEntrada = new BigDecimal("0.00");

	@Inject
	private Produto selected;

	private List<QuatMesesGarantia> listaEnumGarantia = Arrays.asList(QuatMesesGarantia.values());

	private List<FormaPagamento> listaEnumFormaPagamento = Arrays.asList(FormaPagamento.values());
	
	private List<ItemProdSaida> listaSaidaDeProdutos = new ArrayList<ItemProdSaida>();

	private int guardaIndexItemTelefone = -1;

	@Inject
	private Telefone telefone;

	@Inject
	private Telefone telefoneAlterar;

	private List<Telefone> telefones = new ArrayList<Telefone>();

	private List<TipoTelefone> listaEnumTipoTelefone = Arrays.asList(TipoTelefone.values());
	
	private boolean disabledCamposForm = false;
	
	private Date dataNascimentoAux;
	
	@Inject
	private ProdutoLazyDataModel modelProduto;
	
	@Inject
	private ClienteLazyDataModel modelCliente;
	
	@Inject
	private ItemProdSaidaLazyDataModel modelItemProdSaida;
	
	private boolean desabilitaBotoesPrincipais;
	
	private String nomeCliente;
	
	private List<String> imeisCelularesQueEntraram;
	
	private List<String> imeisCelularesQueSairam;
	
	private String mensagemCasoNaoFoiInformadoImei = "Não foi informado o IMEI";
	
	
	public void inicializar() {

		if (idSaida != null) {
			saidaDeProdutos = saidaDeProdutosDAO.porID(idSaida);
			saidaDeProdutos = saidaDeProdutos == null ? new SaidaDeProdutos() : saidaDeProdutos;
			
			if (saidaDeProdutos.getId() != null) {
				
				itemProdSaidas = saidaDeProdutos.getItemProdSaidas();
				modelItemProdSaida.parametros.put("listaDeItens", itemProdSaidas);
				
				saidaDeProdutos.setValor(itemProdSaidaDAO.getValorTotal(saidaDeProdutos.getId()));
				totalSaida = saidaDeProdutos.getValor();
				nomeCliente = saidaDeProdutos.getCliente().getCodDescricao();
				
				filtros.put("parametro01", idSaida);
				filtros.put("entidadeId", "venda");
			}
			idSaida = null;
		}
	}

	@PostConstruct
	public void init() {
		produto.setDescricao("");
		itemProduto.setQuantMesesGarantia(QuatMesesGarantia.TRES_MESES);
		saidaDeProdutos.setData(new Date());
		// lista somente usuários que são vendedores, pois é uma venda
		usuarios = usuarioDAO.porTipoDoPerfil("Vendedor");
		
		AtomicReference<Integer> i = new AtomicReference<Integer>(0);
		usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
		if(usuario != null) {
			usuarios.forEach(o -> {
				if(o.getId().equals(usuario.getId())) {
					i.set(i.get() + 1);
				}
			});
		}
		
		desabilitaBotoesPrincipais = true;
		if(i.get() == 0) {
			usuario = new Usuario();
			desabilitaBotoesPrincipais = false;
		}
		
		saidaDeProdutos.setUsuario(usuario);

		itemProduto.setQuant(1); // iniciar com a quantidade igual a 1
		itemProdSaidas = new ArrayList<>();
		telefone.setTipoTelefone(TipoTelefone.CELULAR);
		telefone.setDdd("047");
	}
	
	@Override
	public String getTitulo() {
		return "Venda de produto";
	}

	@Override
	public String getLinkBreadCrumb() {
		if(filtros.get("parametro01") != null) {
			return "/pages/saida/cadastro-saida-produto.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/saida/cadastro-saida-produto.jsf";
	}

	@Override
	public String caminhoCancelar() {
		return "/pages/saida/lista-saida-produto?faces-redirect=true";
	}
	
	public void fechaModalImpressao(CloseEvent event) {
		saidaDeProdutos = new SaidaDeProdutos();
		saidaDeProdutos.setData(new Date());
		usuario = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
		itemProduto = new ItemProdSaida();
		itemProdSaidas = new ArrayList<>();
		idSaida = null;
		totalSaida = BigDecimal.ZERO;
		modelItemProdSaida.limpaParametros();
		nomeCliente = null;

		RequestContext.getCurrentInstance()
				.update(Arrays.asList("form-cadastro"));
	}
	
	public void abreModalCliente() {
		cliente = new Cliente();
		saidaDeProdutos.setCliente(cliente);
	}
	
	public void abreModalItem() {
		disabledCamposForm = true;
	}
	
	public void fechaModalItem(CloseEvent event) { 
		disabledCamposForm = false;
		request = RequestContext.getCurrentInstance();
		request.update("form-add-produto");
		request.execute("$(function(){PrimeFaces.focus('form-add-produto:quant-prod');});");
	}
	
	public void fechaModalPesquisaItemTelefone(CloseEvent event) { 
		request = RequestContext.getCurrentInstance();
		disabledCamposForm = false;
		telefone = new Telefone();
		telefone.setTipoTelefone(TipoTelefone.CELULAR);
		telefone.setDdd("047");
		txtBotaoInserir = "Adicionar";
		Collection<String> updates = Arrays.asList("form-cliente", "form-add-telefone");
		request.update(updates);
		request.execute("$(function(){PrimeFaces.focus('form-cliente:email');});");
	}
	
	//modal telefone
	public void abreModalItemTelefone() {
		cliente.setNome(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("form-cliente:nome-cliente"));
		if(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("form-cliente:pessoa")
				.equalsIgnoreCase("Física")) {
			cliente.setCpfCnpj(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("form-cliente:cpf"));
		}
		else {
			cliente.setCpfCnpj(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("form-cliente:cnpj"));
		}
		cliente.setDataNascimento(dataNascimentoAux); //setado em um dos métodos: acaoDataSelecionada ou onblurData
		cliente.setEmail(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("form-cliente:email"));
		cliente.setEndereco(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("form-cliente:endereco"));
		disabledCamposForm = true;
		
		if("Adicionar".equals(txtBotaoInserir)) {
			guardaIndexItemTelefone = -1;
		}
	}
	
	public void acaoDataSelecionada(SelectEvent e){
		dataNascimentoAux = (Date) e.getObject();
	}
	
	public void onblurData() {
		dataNascimentoAux = cliente.getDataNascimento();
	}

	public void calculaTotal() {
		totalSaida = new BigDecimal("0.00");
		for (ItemProdSaida item : itemProdSaidas) {
			totalSaida = totalSaida.add(item.getSubTotal(), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);
		}
	}

	public void mostrarFormProd() {
		mostrarFormProduto = true;
	}

	// Nao retorna string, pois nao tera necessidade de voltar para pagina com a
	// lista de orcamentos
	public String salvar() {
		// saidaDeProdutos.setResponsavel(responsavel);

		// Verifica se lista de itens eh igual a zero
		if ((itemProdSaidas.isEmpty() || itemProdSaidas == null || itemProdSaidas.size() == 0)) {
			FacesUtil.addWarnMessage("A lista de itens não pode ser vazia!", "");
			return null;
		}
		saidaDeProdutos.setItemProdSaidas(itemProdSaidas);
		saidaDeProdutosDAO.salvar(saidaDeProdutos, itemProdSaidas);
		if (saidaDeProdutosDAO.isControle()) {
			RequestContext.getCurrentInstance().execute("PF('preparaImpressao').show();");
		}
		return null;
	}
	
	public void imprimirComprovante() {
		try {
			String filename = "ComprovanteDeVenda.pdf";
			String jasperPath = "";

			jasperPath = "/resources/relatorios/comp_venda.jasper";
			
			if (saidaDeProdutos.getId() == null) {
				saidaDeProdutos.setId(saidaDeProdutosDAO.getEntidade().getId());
			}
			listaSaidaDeProdutos = iItemProdSaidaDAO.listaFiltradoPorSaida(saidaDeProdutos.getId());
			
			SaidaDeProdutos s = new SaidaDeProdutos();			
			
			s.setId(Long.parseLong("" + Integer.valueOf(saidaDeProdutos.getId().toString()) + 
					Integer.valueOf(saidaDeProdutos.getId().toString())));
			s.setCliente(listaSaidaDeProdutos.get(0).getSaidaDeProdutos().getCliente());
			s.setData(saidaDeProdutos.getData());
			ItemProdSaida i;
			
			for (ItemProdSaida item : listaSaidaDeProdutos) {
				i = new ItemProdSaida();
				i.setSaidaDeProdutos(s);
				i.setProduto(item.getProduto());
				i.setValorUnit(item.getValorUnit());
				i.setQuant(item.getQuant());
				s.addItensProdutos(i);
			}
			
			listaSaidaDeProdutos.addAll(s.getItemProdSaidas());
			
			Map<String, Object> parametros;
			Date data = new Date();
			Locale local = new Locale("pt", "BR");
			DateFormat formato = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy'.'", local);
			//de acordo com a url, será enviado a mensagem para o arquivo jasper
			if(Dominio.retornaDominio(FacesContext.getCurrentInstance()).contains(Dominio.contemDominio)) {
				parametros =  CarregaArquivo.carregaConfiguracao(Dominio.contemDominio);
				parametros.put("caminhoImgLogo",
						FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images/logo_vidalcell.png"));
			}
			else {
				parametros =  CarregaArquivo.carregaConfiguracao("elitte");
				parametros.put("caminhoImgLogo",
						FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images/logo.png"));
			}
			
			if(parametros.get("cidade") != null) {
				parametros.put("cidade", parametros.get("cidade") + ", " + formato.format(data)); //cidade com data
			}
			
			parametros.put("saidaDeProdutosId", saidaDeProdutos.getId());
			
			GeradorRelatorio.PDF(parametros, jasperPath, listaSaidaDeProdutos, filename);

		} catch (JRException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void excluir() {
		saidaDeProdutosDAO.excluir(saidaDeProdutos.getId());
		if (saidaDeProdutosDAO.isControle()) {
			saidaDeProdutos = new SaidaDeProdutos();
			usuario = null;
			itemProduto = new ItemProdSaida();
			itemProdSaidas = new ArrayList<>();
			idSaida = null;
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
			telefone = new Telefone();
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

	public void salvarCliente() {

		if (ValidaCPF.isCPF(ValidaCPF.tiraPontoeTraco(cliente.getCpfCnpj())) == true
				|| ValidaCNPJ.isCNPJ(ValidaCNPJ.tiraPontoeTraco(cliente.getCpfCnpj())) == true) {
			if (telefones != null && telefones.size() > 0) {
				cliente.setTelefones(telefones);
				clienteDAO.salvar(cliente);

				if (clienteDAO.isControle()) {
					saidaDeProdutos.setCliente(clienteDAO.getEntidade());
					telefones.clear();
					request.addCallbackParam("sucesso", true);
					nomeCliente = clienteDAO.getEntidade().getCodDescricao();
					modelCliente.setCliente(clienteDAO.getEntidade());
					modelCliente.addLista();
					RequestContext.getCurrentInstance().execute("PF('addCliente').hide();");
				}
			} else {
				FacesUtil.addWarnMessage("Deve ser informado ao menos um telefone para contato!", "");
			}
		} else {
			FacesUtil.addWarnMessage("CPF ou CNPJ inválido!", "");
		}
	}

	public void ordemSelecionado(SelectEvent event) {
		SaidaDeProdutos cliente = (SaidaDeProdutos) event.getObject();
		saidaDeProdutos = cliente;
	}

	public void onRowSelectOrdem(SelectEvent event) {
		SaidaDeProdutos s = (SaidaDeProdutos) event.getObject();
		saidaDeProdutos = s;
	}
	
	public void selecionarCliente(SelectEvent event) {
		cliente = modelCliente.busca(nomeCliente);
		saidaDeProdutos.setCliente(cliente);
		nomeCliente = null;
	}
	
	public void setaListaDeImeisCasoSejaUmCelular() {
		//retorna todas as entradas do produto
		List<ItemEntrada> listaImeisQueEntraram = itemEntradaDAO.porIdProduto(produto.getId());
		imeisCelularesQueEntraram = listaImeisQueEntraram.stream()
			.map(obj -> obj.getImei() == null ? mensagemCasoNaoFoiInformadoImei : obj.getImei())
			.collect(Collectors.toList());
		
		List<ItemProdSaida> listaImeisQueSairam = itemProdSaidaDAO.porIdProdutoAndIdSaida(produto.getId(), saidaDeProdutos.getId());
		
		imeisCelularesQueSairam = listaImeisQueSairam.stream()
			.map(obj -> obj.getImei() == null ? mensagemCasoNaoFoiInformadoImei : obj.getImei())
			.collect(Collectors.toList());
		
		imeisCelularesQueSairam.forEach(stringImei -> {
			if(imeisCelularesQueEntraram.contains(stringImei) && !stringImei.equals(mensagemCasoNaoFoiInformadoImei)) {
				int indice = imeisCelularesQueEntraram.indexOf(stringImei);
				imeisCelularesQueEntraram.remove(indice);
			}
		});
		
	}
			
	public void setaValorUnitario(SelectEvent event) {	
		
		produto = modelProduto.busca(descricaoProduto);
		
		ItemEntrada i = null;
		i = itemEntradaDAO.retornaValorDaUltimaEntradaProd(produto.getId());
		
		if (i != null) {
			
            setUltimoValorEntrada(i.getValorUnit());

			if (produto.getValorInformado() != null) {
				produto.setValorSugerido(produto.getValorInformado());
			}			
			else {
				// INÍCIO - calcula valor sugerido
				produto.setValorSugerido(i.getValorUnit());
				produto.setPercentualLucro(OperacoesComBigDecimal.divisao(produto.getPercentualLucro(),
						BigDecimal.TEN.multiply(BigDecimal.TEN)));
				produto.setValorSugerido(OperacoesComBigDecimal.soma(
						OperacoesComBigDecimal.multiplicacao(produto.getValorSugerido(), produto.getPercentualLucro()),
						i.getValorUnit()));
				
				produto.setPercentualLucro(OperacoesComBigDecimal.multiplicacao(produto.getPercentualLucro(),
						BigDecimal.TEN.multiply(BigDecimal.TEN)));
				// FIM - calcula valor sugerido
			}
			
			if (produto.getValorSugerido() == null) {
				produto.setValorSugerido(BigDecimal.ZERO);
			}

			// se atender essa condição quer dizer que o valor de venda não foi definido
			// corretamente
			if (OperacoesComBigDecimal.numUmMaiorOuIgualNumDois(produto.getValorPago(), produto.getValorSugerido())) {
				produto.setValorSugerido(BigDecimal.ZERO);
				produto.setLucro(BigDecimal.ZERO);
			}
			
			itemProduto.setValorUnit(produto.getValorSugerido());
		}
		else {
			itemProduto.setValorUnit(BigDecimal.ZERO);
		}
		itemProduto.setProduto(produto);
		
		if(produto.getCategoria() != null && !produto.getCategoria().getDescricao().toLowerCase().contains("celular")) {
			itemProduto.setImei(null);
		}
		else {
			setaListaDeImeisCasoSejaUmCelular();
		}
	}

	public void validaValorRecomendadoVenda(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {

		BigDecimal s = (BigDecimal) value;
		
		BigDecimal valor = new BigDecimal("0.00", MathContext.DECIMAL128);
		valor = s.setScale(2, RoundingMode.HALF_EVEN);
		
		if (OperacoesComBigDecimal.numUmMaiorQueNumDois(this.ultimoValorEntrada, valor)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção com o valor!",
					"O valor informado é menor do que a última entrada desse produto!"));
		}
	}
	
	public List<String> filtroCliente(String txt){
		return modelCliente.filtro(txt, 15);
	}
	
	public List<String> filtroProduto(String txt){
		modelProduto.setComEntradaGerada(true);
		return modelProduto.filterTrazendoListas(txt, 15);
	}

	public void pesquisar() {
		modelCliente.limpaParametros();
		modelCliente.parametros.put("nome", nome != null ? nome.toLowerCase() + "%" : null);
	}

	public void pesquisarProduto() {
		modelProduto.limpaParametros();
		if(modelProduto.isPesquisaPorCodigo())
			modelProduto.parametros.put("idComEntradaGerada", idProduto);
		else
			modelProduto.parametros.put("descricaoComEntradaGerada", descricaoProduto != null ? descricaoProduto.toLowerCase() + "%" : null);
	}

	public String salvarNaListaDeItens() {
		
		produto = modelProduto.busca(descricaoProduto);
		itemProduto.setProduto(produto);
		
		if(itemProduto.getImei() != null && itemProduto.getImei().equals(mensagemCasoNaoFoiInformadoImei)) {
			FacesUtil.addWarnMessage("Este produto foi dado entrada sem informar o IMEI, vá até a Entrada de produtos e informe o IMEI para o mesmo!", "");
			return null;
		}

		if (Validacoes.verificaProdutoSaida(itemProduto.getProduto(), itemProduto.getImei(), guardaIndexItemProd, itemProdSaidas)) {
			FacesUtil.addWarnMessage("Esse produto já foi informado para essa Venda!", "");
			return null;
		}
		if (itemProduto.getProduto().getDescricao() == null) {
			return null;
		}
		if(retornaQuantEstoque() < itemProduto.getQuant()) {
			FacesUtil.addWarnMessage("Não tem essa quantidade suficiente em estoque!", "");
			return null;
		}
		if (guardaIndexItemProd == -1) {
			this.itemProdSaidas.add(itemProduto);
			request.addCallbackParam("sucessoAddProduto", true);
		} else {
			this.itemProdSaidas.set(guardaIndexItemProd, itemProduto);
			guardaIndexItemProd = -1;
			txtBotaoInserir = "Adicionar";
			txtTitleBotaoInserir = "Adicionar à lista de peças";
			request.addCallbackParam("sucessoAddProduto", true);
		}
		modelItemProdSaida.parametros.put("listaDeItens", itemProdSaidas);
		calculaTotal();
		RequestContext.getCurrentInstance().execute("PF('addProduto').hide();");
		return null;

	}
	
	public int retornaQuantEstoque() {
		
		if (itemProduto.getProduto().getItemEntradas() == null || itemProduto.getProduto().getItemEntradas().size() == 0) {
			itemProduto.getProduto().setItemEntradas(modelProduto.getItemEntradaDAO().porIdProduto(itemProduto.getProduto().getId()));
		}
		
		if (itemProduto.getProduto().getItemProdSaidas() == null || itemProduto.getProduto().getItemProdSaidas().size() == 0) {
			//verifica se já tem itens na saida de produtos, no caso verifica se é uma venda
			if(saidaDeProdutos.getId() != null) {
				itemProduto.getProduto().setItemProdSaidas(modelProduto.getItemProdSaidaDAO()
						.porIdProdutoSemDeterminadaSaida(itemProduto.getProduto().getId(), saidaDeProdutos.getId()));
			}
			else {
				itemProduto.getProduto().setItemProdSaidas(modelProduto.getItemProdSaidaDAO().porIdProduto(itemProduto.getProduto().getId()));
			}
		}
		else if(saidaDeProdutos.getId() != null) {
				itemProduto.getProduto().setItemProdSaidas(
						itemProduto.getProduto().getItemProdSaidas().stream()
							.filter(line -> !line.getSaidaDeProdutos().getId().equals(saidaDeProdutos.getId()))     
							.collect(Collectors.toList())
						);
		}

		if (itemProduto.getProduto().getItemProdutos() == null || itemProduto.getProduto().getItemProdutos().size() == 0) {
			itemProduto.getProduto().setItemProdutos(modelProduto.getItemProdutoDAO().porIdProduto(itemProduto.getProduto().getId()));
		}
		
		itemProduto.getProduto().setQuant(itemProduto.getProduto().getItemEntradas().stream().map(ItemEntrada::getQuant).reduce(0, Integer::sum));
		itemProduto.getProduto().setQuant(itemProduto.getProduto().getQuant()
				- (itemProduto.getProduto().getItemProdSaidas().stream().map(ItemProdSaida::getQuant).reduce(0, Integer::sum)));
		
		//itens onde o estado da ordem está como ,GARANTIA_CONCLUIDA, GARANTIA_ANDAMENTO, APROVADO, FINALIZADO
		OrdemDeServico ordemDeServico = new OrdemDeServico();
		itemProduto.getProduto().setItemProdutos(itemProduto.getProduto().getItemProdutos().stream()
				.filter(line -> ordemDeServico.listaEnumTiraEstoque().contains(line.getOrdemDeServico().getEstado().getCod()))
	            .collect(Collectors.toList()));
		
		itemProduto.getProduto().setQuant(itemProduto.getProduto().getQuant()
				- (itemProduto.getProduto().getItemProdutos().stream().map(ItemProduto::getQuant).reduce(0, Integer::sum)));
		
		return itemProduto.getProduto().getQuant();
	}

	public void preparaInclusao() {
		itemProduto = new ItemProdSaida();
		produto = new Produto();
		itemProduto.setQuant(1);
		itemProduto.setQuantMesesGarantia(QuatMesesGarantia.TRES_MESES);
		descricaoProduto = null;
		guardaIndexItemProd = -1;
		txtBotaoInserir = "Adicionar";
		// RequestContext.getCurrentInstance().execute("PF('addProduto').show();");
	}

	public void removerNaListaDeItens(ItemProdSaida i) {
		// pega indice do item na lista
		if(i.getId() != null) {
			itemProdSaidas.removeIf(x -> i.getId().equals(x.getId()));
		}
		else {
			itemProdSaidas.removeIf(x -> x.getProduto().getId().equals(i.getProduto().getId())); 
		}
		modelItemProdSaida.parametros.put("listaDeItens", itemProdSaidas);
		calculaTotal();
	}

	public void alterarNaListaDeItens(ItemProdSaida i) {
		itemProduto = new ItemProdSaida();
		produto = new Produto();
		itemProduto.setId(i.getId());
		itemProduto.setSaidaDeProdutos(saidaDeProdutos);
		itemProduto.setProduto(i.getProduto());
		itemProduto.setQuantMesesGarantia(i.getQuantMesesGarantia());
		itemProduto.setQuant(i.getQuant());
		itemProduto.setValorUnit(i.getValorUnit());
		itemProduto.setImei(i.getImei());
		produto = itemProduto.getProduto();
		descricaoProduto = produto.getCodDescricao();
		/*caso o objeto selecionado não exista na lista, ele terá que ser adicionado, 
		para depois fazer na validação na hora que o método salvarNaLista ser chamado*/
		if(!modelProduto.getLista().contains(produto)) {
			modelProduto.setProduto(produto);
			modelProduto.addLista();
		}
		setaListaDeImeisCasoSejaUmCelular();
		setUltimoValorEntrada(itemEntradaDAO.retornaValorDaUltimaEntradaProd(produto.getId()).getValorUnit());
		// pega indice do item na lista
		guardaIndexItemProd = itemProdSaidas.indexOf(i);
		// muda texto do botão
		txtBotaoInserir = "Alterar";
		txtTitleBotaoInserir = "Alterar item da lista";
		calculaTotal();

	}

	public SaidaDeProdutos getSaidaDeProdutos() {
		return saidaDeProdutos;
	}

	public void setSaidaDeProdutos(SaidaDeProdutos saidaDeProdutos) {
		this.saidaDeProdutos = saidaDeProdutos;
	}

	public Long getIdSaida() {
		return idSaida;
	}

	public void setIdSaida(Long idSaida) {
		this.idSaida = idSaida;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getPessoa() {
		return pessoa;
	}

	public void setPessoa(String pessoa) {
		this.pessoa = pessoa;
	}

	public List<Cliente> getClientesFiltrados() {
		return clientesFiltrados;
	}

	public void setClientesFiltrados(List<Cliente> clientesFiltrados) {
		this.clientesFiltrados = clientesFiltrados;
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

	public List<ItemProdSaida> getItemProdSaidas() {
		return itemProdSaidas;
	}

	public void setItemProdSaidas(List<ItemProdSaida> itemProdSaidas) {
		this.itemProdSaidas = itemProdSaidas;
	}

	public ItemProdSaida getItemProduto() {
		return itemProduto;
	}

	public void setItemProduto(ItemProdSaida itemProduto) {
		this.itemProduto = itemProduto;
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public BigDecimal getUltimoValorEntrada() {
		return ultimoValorEntrada;
	}

	public void setUltimoValorEntrada(BigDecimal ultimoValorEntrada) {
		this.ultimoValorEntrada = ultimoValorEntrada;
	}

	public List<QuatMesesGarantia> getListaEnumGarantia() {
		return listaEnumGarantia;
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
	public boolean isDisabledCamposForm() {
		return disabledCamposForm;
	}

	public void setDisabledCamposForm(boolean disabledCamposForm) {
		this.disabledCamposForm = disabledCamposForm;
	}

	public List<TipoTelefone> getListaEnumTipoTelefone() {
		return listaEnumTipoTelefone;
	}

	public void setListaEnumTipoTelefone(List<TipoTelefone> listaEnumTipoTelefone) {
		this.listaEnumTipoTelefone = listaEnumTipoTelefone;
	}

	public ProdutoLazyDataModel getModelProduto() {
		return modelProduto;
	}

	public void setModelProduto(ProdutoLazyDataModel modelProduto) {
		this.modelProduto = modelProduto;
	}

	public ClienteLazyDataModel getModelCliente() {
		return modelCliente;
	}

	public void setModelCliente(ClienteLazyDataModel modelCliente) {
		this.modelCliente = modelCliente;
	}

	public ItemProdSaidaLazyDataModel getModelItemProdSaida() {
		return modelItemProdSaida;
	}

	public void setModelItemProdSaida(ItemProdSaidaLazyDataModel modelItemProdSaida) {
		this.modelItemProdSaida = modelItemProdSaida;
	}

	public boolean isDesabilitaBotoesPrincipais() {
		return desabilitaBotoesPrincipais;
	}

	public void setDesabilitaBotoesPrincipais(boolean desabilitaBotoesPrincipais) {
		this.desabilitaBotoesPrincipais = desabilitaBotoesPrincipais;
	}

	public Long getIdProduto() {
		return idProduto;
	}

	public void setIdProduto(Long idProduto) {
		this.idProduto = idProduto;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public List<String> getImeisCelularesQueEntraram() {
		return imeisCelularesQueEntraram;
	}

	public void setImeisCelularesQueEntraram(List<String> imeisCelularesQueEntraram) {
		this.imeisCelularesQueEntraram = imeisCelularesQueEntraram;
	}

}
