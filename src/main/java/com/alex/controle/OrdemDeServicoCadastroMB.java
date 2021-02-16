package com.alex.controle;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;

import com.alex.DAO.AparelhoDAO;
import com.alex.DAO.ClienteDAO;
import com.alex.DAO.EmailDAO;
import com.alex.DAO.ItemEntradaDAO;
import com.alex.DAO.ModeloDAO;
import com.alex.DAO.OrdemDeServicoDAO;
import com.alex.DAO.ServicoDAO;
import com.alex.DAO.UsuarioDAO;
import com.alex.controle.criptografia.CriptografiaBase64;
import com.alex.modelo.Aparelho;
import com.alex.modelo.ClasseAuxiliarEmail;
import com.alex.modelo.Cliente;
import com.alex.modelo.Email;
import com.alex.modelo.ItemEntrada;
import com.alex.modelo.ItemProdSaida;
import com.alex.modelo.ItemProduto;
import com.alex.modelo.ItemServico;
import com.alex.modelo.Modelo;
import com.alex.modelo.OrdemDeServico;
import com.alex.modelo.Produto;
import com.alex.modelo.Servico;
import com.alex.modelo.Telefone;
import com.alex.modelo.Usuario;
import com.alex.modelo.enums.EnumSimNaoGenerico;
import com.alex.modelo.enums.Estado;
import com.alex.modelo.enums.FormaPagamento;
import com.alex.modelo.enums.MaoDeObra;
import com.alex.modelo.enums.QuatMesesGarantia;
import com.alex.modelo.enums.TipoTelefone;
import com.alex.modelo.validacao.Validacoes;
import com.alex.relatorios.GeradorRelatorio;
import com.alex.servico.AparelhoLazyDataModel;
import com.alex.servico.ClienteLazyDataModel;
import com.alex.servico.ItemProdutoLazyDataModel;
import com.alex.servico.ItemServicoLazyDataModel;
import com.alex.servico.ModeloLazyDataModel;
import com.alex.servico.ProdutoLazyDataModel;
import com.alex.servico.ServicoLazyDataModel;
import com.alex.util.CarregaArquivo;
import com.alex.util.Dominio;
import com.alex.util.EncriptaDecriptaDES;
import com.alex.util.FacesUtil;
import com.alex.util.GeradorChave;
import com.alex.util.ManipData;
import com.alex.util.NomeAplicacao;
import com.alex.util.OperacoesComBigDecimal;

import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class OrdemDeServicoCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;

	@Inject
	private OrdemDeServico ordemDeServico;
	@Inject
	private OrdemDeServicoDAO ordemDeServicoDAO;

	@Inject
	private ClienteDAO clienteDAO;

	@Inject
	private UsuarioDAO usuarioDAO;

	@Inject
	private AparelhoDAO aparelhoDAO;

	@Inject
	private ModeloDAO modeloDAO;

	@Inject
	private ServicoDAO servicoDAO;

	@Inject
	private ItemEntradaDAO itemEntradaDAO;

	@Inject
	private Cliente cliente;

	@Inject
	private Usuario usuario;

	@Inject
	private Aparelho aparelho;

	@Inject
	private Modelo modelo;

	@Inject
	private Servico servico;

	@Inject
	private Produto produto;

	@Inject
	private ItemProduto itemProduto;

	@Inject
	private ItemServico itemServico;

	private Long idOrdem;

	private String pessoa = "Física";

	private List<Usuario> usuarios;

	private List<Aparelho> aparelhosFiltrados;

	private List<Cliente> clientesFiltrados;

	private List<Servico> servicos = new ArrayList<>();

	private List<ItemProduto> itemProdutos = new ArrayList<>();

	private List<ItemServico> itemServicos = new ArrayList<>();

	RequestContext request = RequestContext.getCurrentInstance();

	private String nomeModelo;

	private String nomeAparelho;

	private String descricaoProduto;

	private Long idProduto;

	private boolean mostrarFormProduto = false;

	private String txtBotaoInserir = "Adicionar";

	private int guardaIndexItemProd = -1;// pq tem o indice 0

	private int guardaIndexItemServ = -1;

	private String txtTitleBotaoInserir = "Adicionar à lista de peças";

	// verifica se o valor informado pelo usuário é menor do que foi registrado na
	// última entrada desse produto
	private BigDecimal ultimoValorEntrada = new BigDecimal("0.00");

	// valor mínimo da venda (indica valor)
	private BigDecimal valorMinimo = new BigDecimal("0.00");

	@Inject
	private Produto selected;

	private List<QuatMesesGarantia> listaEnumGarantia = Arrays.asList(QuatMesesGarantia.values());

	private List<Estado> listaEnumEstado = new ArrayList<Estado>();

	private List<FormaPagamento> listaEnumFormaPagamento = Arrays.asList(FormaPagamento.values());

	private List<EnumSimNaoGenerico> listaEnumSimNaoGenerico = Arrays.asList(EnumSimNaoGenerico.values());
	
	private List<MaoDeObra> listaMaoDeObra;

	@Inject
	private Telefone telefone;

	@Inject
	private Telefone telefoneAlterar;

	private int guardaIndexItemTelefone = -1;

	private List<Telefone> telefones = new ArrayList<Telefone>();

	private List<TipoTelefone> listaEnumTipoTelefone = Arrays.asList(TipoTelefone.values());

	private boolean disabledCamposForm = false;

	private Date dataNascimentoAux;

	@Inject
	private ClienteLazyDataModel modelCliente;

	@Inject
	private AparelhoLazyDataModel modelAparelho;

	@Inject
	private ModeloLazyDataModel modelModelo;

	@Inject
	private ProdutoLazyDataModel modelProduto;

	@Inject
	private ItemProdutoLazyDataModel modelItemProduto;

	@Inject
	private ServicoLazyDataModel modelServico;

	@Inject
	private ItemServicoLazyDataModel modelItemServico;

	@Inject
	private ClasseAuxiliarEmail classeAuxiliarEmail;

	@Inject
	private Dominio dominio;

	private boolean enviaEmail;

	@Inject
	private EmailDAO emailDAO;

	@Inject
	private Email objEmail;

	private boolean controleValidaIMEI = true;

	private String mensagemModalValidaIMEI;

	private String nomeServico;
	
	private String nomeCliente;

	public void inicializar() {
		
		if (idOrdem != null) {
			ordemDeServico = ordemDeServicoDAO.porID(idOrdem);
			ordemDeServico = ordemDeServico == null ? new OrdemDeServico() : ordemDeServico;
			ordemDeServicoDAO.setEntidade(ordemDeServico);
			ordemDeServicoDAO.getEntidade().setImei(ordemDeServico.getImei());

			if (ordemDeServico.getId() != null) {
				filtros.put("parametro01", ordemDeServico.getId());
				filtros.put("entidadeId", "entrada OS");

				itemProdutos = ordemDeServico.getItemProdutos();
				modelItemProduto.parametros.put("listaDeItens", itemProdutos);

				itemServicos = ordemDeServico.getItemServicos();
				modelItemServico.parametros.put("listaDeItens", itemServicos);

				valorMinimo = BigDecimal.ZERO;
				for (ItemProduto i : itemProdutos) {
					valorMinimo = OperacoesComBigDecimal.soma(valorMinimo, i.getSubTotal());
				}

				aparelho = ordemDeServicoDAO.filtraAparelho(ordemDeServico.getModelo().getId());
				
				usuario = usuarioDAO.porID(ordemDeServico.getUsuario().getId());
				usuarios = usuarios.stream()
						.filter(line -> usuario.getId().equals(line.getId()) || 
								line.getAtivo())
						.collect(Collectors.toList());
				
				modelAparelho.setAparelho(aparelho);
				modelAparelho.addLista();
				nomeAparelho = aparelho.getMarca();
				
				nomeModelo = ordemDeServico.getModelo().getModelo();
				
				nomeCliente = ordemDeServico.getCliente().getCodDescricao();

			}
		}
		idOrdem = null;
	}

	@PostConstruct
	public void init() {
		
        usuarios = usuarioDAO.porTipoDoPerfil("Técnico");
		modelAparelho.getServicoAutocomplete().setMinQueryLength(1);

		Arrays.asList(Estado.values()).forEach(o -> {
			if (!o.equals(Estado.TODOS)) {
				listaEnumEstado.add(o);
			}
		});

		if(usuarios == null || usuarios.size() == 0) {
			usuarios = usuarioDAO.porTipoDoPerfil("Técnico");
		}

		itemProduto.setQuant(1); // iniciar com a quantidade igual a 1
		itemProdutos = new ArrayList<>();
		ordemDeServico.setBateria(EnumSimNaoGenerico.SIM);
		ordemDeServico.setQuantMesesGarantia(QuatMesesGarantia.TRES_MESES);
		telefone.setTipoTelefone(TipoTelefone.CELULAR);
		telefone.setDdd("047");

	}

	@Override
	public String getTitulo() {
		return "Entrada OS";
	}

	@Override
	public String getLinkBreadCrumb() {
		if (filtros.get("parametro01") != null) {
			return "/pages/ordemservico/entrada-os.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/ordemservico/entrada-os.jsf";
	}

	@Override
	public String caminhoCancelar() {
		return "/pages/ordemservico/lista-os?faces-redirect=true";
	}

	public StringBuilder carregaArquivoHTML() {

		StringBuilder builder = new StringBuilder();

		InputStream inputstream;
		try {
			String relativeWebPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/email/index.html");
			inputstream = new BufferedInputStream(new FileInputStream(relativeWebPath));

			BufferedReader in = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				builder.append(inputLine).append("\n");
			in.close();
			inputstream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return builder;

	}

	public void setClasseAuxiliarEmail() {

		FacesContext fc = FacesContext.getCurrentInstance();
		HttpServletRequest req = (HttpServletRequest) fc.getExternalContext().getRequest();
		// Usuario us = (Usuario) req.getSession().getValue("usuario");

		// Ex: http://localhost:8080/osituporanga
		dominio.setDominio(req.getRequestURL().toString());

		if (ordemDeServico != null) {
			classeAuxiliarEmail.setNomeCliente(ordemDeServico.getCliente().getNome());
			classeAuxiliarEmail.setNomeAparelhoCompleto(ordemDeServico.getModelo().getAparelho().getMarca() + " - "
					+ ordemDeServico.getModelo().getModelo());
			classeAuxiliarEmail.setSituacao(ordemDeServico.getEstado().getDescricao());
			classeAuxiliarEmail.setDefeitoInformado(
					classeAuxiliarEmail.getDefeitoInformado().append(ordemDeServico.getDefeitoInformado()));
			classeAuxiliarEmail
					.setLaudoTecnico(classeAuxiliarEmail.getLaudoTecnico().append(ordemDeServico.getLaudoTecnico()));
			classeAuxiliarEmail
					.setObservacoes(classeAuxiliarEmail.getObservacoes().append(ordemDeServico.getObservacoes()));
			// SETAR IMAGENS NO ARRAY
			classeAuxiliarEmail.setCaminhoImagens(
					new ArrayList<>(Arrays.asList(dominio.getDominio() + "/resources/images/email/h1_2.png",
							dominio.getDominio() + "/resources/images/email/it.png",
							dominio.getDominio() + "/resources/images/email/fb.png")));
		}
	}

	public void envioEmail() throws EmailException {
		/*
		 * Para reativar bloqueio de qualquer dispositivo ->
		 * https://myaccount.google.com/lesssecureapps
		 */
		/*
		 * Exemplo foi tirado nessa documentação ->
		 * https://commons.apache.org/proper/commons-email/userguide.html
		 */
		objEmail = emailDAO.porID(1L); // apenas o primeiro interessa

		if (objEmail != null) {

			String txtHTML = carregaArquivoHTML().toString();

			HtmlEmail email = new HtmlEmail();
//			email.setSSL(true);
//			email.setTLS(true);

			email.setStartTLSEnabled(objEmail.isHabilitadoTLS()); // setar quando é email do hotmail
			email.setSSLOnConnect(objEmail.isHabilitadoSSL()); // setar quando é email do gmail
			email.setHostName(objEmail.getHostName()); // setar quando é email do hotmail
			// email.setHostName("smtp.googlemail.com"); //setar quando é email do gmail
			email.setSmtpPort(objEmail.getPorta()); // setar quando é email do hotmail
			// email.setSmtpPort(465); //setar quando é email do gmail e 587 do outlook
			objEmail.setSenha(EncriptaDecriptaDES.decifrar(objEmail.getSenha(), 10));
			objEmail.setSenha(CriptografiaBase64.decode(objEmail.getSenha()));
			email.setAuthentication(objEmail.getEmail(), objEmail.getSenha());
			email.setFrom(objEmail.getEmail());
			email.addTo(ordemDeServico.getCliente().getEmail(), ordemDeServico.getCliente().getNome());
			email.setSubject("Situação do reparo: Aparelho " + classeAuxiliarEmail.getNomeAparelhoCompleto());

			// embed the image and get the content id
			List<URL> listaURLs = new ArrayList<URL>();

			classeAuxiliarEmail.getCaminhoImagens().forEach(o -> {
				try {
					listaURLs.add(new URL(o));
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

			classeAuxiliarEmail
					.setCaminhoImagens(new ArrayList<>(Arrays.asList(email.embed(listaURLs.get(0), "Ellite INFORMA"),
							email.embed(listaURLs.get(1), "Instagran"), email.embed(listaURLs.get(2), "Facebook"))));

			txtHTML = txtHTML.replace("#nomeCliente", classeAuxiliarEmail.getNomeCliente());
			txtHTML = txtHTML.replace("#nomeAparelhoCompleto", classeAuxiliarEmail.getNomeAparelhoCompleto() + ".");
			txtHTML = txtHTML.replace("#situacao", classeAuxiliarEmail.getSituacao() + ".");
			txtHTML = txtHTML.replace("#defeitoInformado",
					classeAuxiliarEmail.getDefeitoInformado().append(".").toString());
			// Faz essa validação pois o laudo pode ser null
			if (classeAuxiliarEmail.getLaudoTecnico() != null && classeAuxiliarEmail.getLaudoTecnico().length() > 0) {
				txtHTML = txtHTML.replace("<!--#laudoTecnico-->", "<li>\r\n"
						+ "												<p style=\"text-align: justify;\"><b>Laudo técnico:</b> <br/>"
						+ classeAuxiliarEmail.getLaudoTecnico().append(".").toString() + "\r\n"
						+ "												</p>\r\n"
						+ "											</li>");
			}
			// Faz essa validação pois o a observação pode ser null
			if (classeAuxiliarEmail.getObservacoes() != null && classeAuxiliarEmail.getObservacoes().length() > 0) {
				txtHTML = txtHTML.replace("<!--#observacoes-->", "<li>\r\n"
						+ "												<p style=\"text-align: justify;\"><b>Observações:</b> <br/>"
						+ classeAuxiliarEmail.getObservacoes().append(".").toString() + "\r\n"
						+ "												</p>\r\n"
						+ "											</li>");
			}
			txtHTML = txtHTML.replace("#img00", "cid:" + classeAuxiliarEmail.getCaminhoImagens().get(0));
			txtHTML = txtHTML.replace("#img01", "cid:" + classeAuxiliarEmail.getCaminhoImagens().get(1));
			txtHTML = txtHTML.replace("#img02", "cid:" + classeAuxiliarEmail.getCaminhoImagens().get(2));

			// set the html message
			email.setHtmlMsg(txtHTML);

			// set the alternative message
			email.setTextMsg("Ellite informa,\n"
					+ "Prezado(a) Marcelo Alves, através desse e-mail viemos informar em que situação se encontra o reparo do aparelho "
					+ "" + classeAuxiliarEmail.getNomeAparelhoCompleto() + ".\n\n" + " * Situação do reparo: " + "  "
					+ classeAuxiliarEmail.getSituacao() + ".\n" + " * Defeito informado: "
					+ classeAuxiliarEmail.getDefeitoInformado().append("\n").toString()
					+ auxiliaImpressaoTxtMsg(" * Laudo técnico: ", classeAuxiliarEmail.getLaudoTecnico())
					+ auxiliaImpressaoTxtMsg(" * Observsações: ", classeAuxiliarEmail.getObservacoes()) + "\n"
					+ "Att.\n" + "Ellite telecom\n" + "Tel.: (47) 3533-5281\n"
					+ "Endereço: Rua Governador Celso Ramos, nº 186 - Bloco A - Sala 04 Ed. Torre de Frate - Centro - 88400-000 - Ituporanga-SC");

			// send the email
			email.send();
		} else {
			FacesUtil.addWarnMessage(
					"Registro salvo com sucesso! Mas é preciso ter um e-mail salvo no banco de dados para enviar e-mails aos clientes!",
					"");
		}

	}

	public String auxiliaImpressaoTxtMsg(String item, StringBuilder builder) {
		if (builder != null && builder.length() > 0) {
			return item + builder.append("\n").toString();
		}
		return "";
	}

	public void selecionarOrdem(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext()
					.redirect(NomeAplicacao.nome + "/pages/ordemservico/entrada-os.jsf?id=" + idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}

	public void abreModalCliente() {
		cliente = new Cliente();
		ordemDeServico.setCliente(cliente);
	}

	public void fechaModalItemServico(CloseEvent event) {
		disabledCamposForm = false;
		RequestContext.getCurrentInstance().update("form-add-item-servico");
	}

	public void fechaModalItem(CloseEvent event) {
		disabledCamposForm = false;
		RequestContext.getCurrentInstance().update("form-add-produto");
		RequestContext.getCurrentInstance().execute("$(function(){PrimeFaces.focus('form-add-produto:quant-prod');});");
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

	public void fechaModalImpressao(CloseEvent event) {
		ordemDeServico = new OrdemDeServico();
		ordemDeServico.setBateria(EnumSimNaoGenerico.SIM);
		enviaEmail = false;
		ordemDeServico.setQuantMesesGarantia(QuatMesesGarantia.TRES_MESES);
		usuario = new Usuario();
		servico = new Servico();
		aparelho = new Aparelho();
		itemProduto = new ItemProduto();
		itemProduto.setQuant(1);
		itemProdutos = new ArrayList<>();
		itemServicos = new ArrayList<>();
		idOrdem = null;
		modelCliente.limpaParametros();
		modelAparelho.limpaParametros();
		modelModelo.limpaParametros();
		modelProduto.limpaParametros();
		modelItemProduto.limpaParametros();
		modelItemServico.limpaParametros();
		nomeAparelho = null;
		nomeModelo = null;
		nomeCliente = null;

		RequestContext.getCurrentInstance()
				.update(Arrays.asList("form-principal", "form-aparelho", "form-modelo", "form-servico"));
		RequestContext.getCurrentInstance().execute("$(function(){PrimeFaces.focus('form-add-produto:quant-prod');});");
	}

	public void naoContinuaOrdem() {
		controleValidaIMEI = true;

		ordemDeServico = new OrdemDeServico();
		ordemDeServico.setBateria(EnumSimNaoGenerico.SIM);
		enviaEmail = false;
		ordemDeServico.setQuantMesesGarantia(QuatMesesGarantia.TRES_MESES);
		usuario = new Usuario();
		servico = new Servico();
		aparelho = new Aparelho();
		itemProduto = new ItemProduto();
		itemProduto.setQuant(1);
		itemProdutos = new ArrayList<>();
		idOrdem = null;
		modelCliente.limpaParametros();
		modelAparelho.limpaParametros();
		modelModelo.limpaParametros();
		modelProduto.limpaParametros();
		modelItemProduto.limpaParametros();
		modelServico.limpaParametros();
	}

	public void abreModalItem() {
		disabledCamposForm = true;
	}

	public void abreModalItemTelefone() {
		cliente.setNome(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("form-cliente:nome-cliente"));
		if (FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("form-cliente:pessoa")
				.equalsIgnoreCase("Física")) {
			cliente.setCpfCnpj(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("form-cliente:cpf"));
		} else {
			cliente.setCpfCnpj(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
					.get("form-cliente:cnpj"));
		}
		cliente.setDataNascimento(dataNascimentoAux); // setado em um dos métodos: acaoDataSelecionada ou onblurData
		cliente.setEmail(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("form-cliente:email"));
		cliente.setEndereco(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("form-cliente:endereco"));

		disabledCamposForm = true;

		if ("Adicionar".equals(txtBotaoInserir)) {
			guardaIndexItemTelefone = -1;
		}
	}

	public void abreModalItemParaAdicionarServico() {
		servico = new Servico();
		if(listaMaoDeObra == null) {
			listaMaoDeObra = Arrays.asList(MaoDeObra.values());
		}
		disabledCamposForm = true;
	}

	public void acaoDataSelecionada(SelectEvent e) {
		dataNascimentoAux = (Date) e.getObject();
	}

	public void onblurData() {
		dataNascimentoAux = cliente.getDataNascimento();
	}

	/*
	 * public List<Produto> pesquisaProdutos(String descricao) { produtos =
	 * produtoDAO.porDescricaoSemelhanteComEntradaGerada(descricao); if
	 * (produtos.size() == 0) {
	 * FacesUtil.addWarnMessage(FacesUtil.mensagemPadraoNaoEncontrouProduto(), "");
	 * } return produtos; }
	 */

	public void mostrarFormProd() {
		mostrarFormProduto = true;
	}

	// verifica situação antes de salvar
	public boolean verificaEstado() {
		if (ordemDeServico.getEstado() == Estado.APROVADO || ordemDeServico.getEstado() == Estado.FINALIZADO) {
			return true;
		}
		return false;
	}

	// Nao retorna string, pois nao tera necessidade de voltar para pagina com a
	// lista de orcamentos
	public String salvar() {

		if ((ordemDeServico.getEstado().equals(Estado.APROVADO) || ordemDeServico.getEstado().equals(Estado.FINALIZADO) 
				|| ordemDeServico.getEstado().equals(Estado.GARANTIA_CONCLUIDA) 
				|| ordemDeServico.getEstado().equals(Estado.SERVICO_RELIAZADO))
				&& (itemServicos == null || itemServicos.size() == 0)) {
			FacesUtil.addWarnMessage("Como o estado da Ordem está " + ordemDeServico.getEstado().getDescricao()
					+ ", é preciso informar ao menos um serviço realizado!", "");
			return null;
		}
		
		if(confereQuantPecasUtilizadasMaiorOuIgualQueQuantServico()) {
			FacesUtil.addWarnMessage("A quantidade de peças utilizadas tem que ser maior ou igual a "
					+ "quantidade de serviços realizados com produto!", "");
			return null;
		}

		if (!ordemDeServico.getImei().equals("000000-00-000000-0")
				&& "Sim".equalsIgnoreCase(ManipData
						.garantiaOS(ordemDeServicoDAO.porImei(ordemDeServico.getImei(), ordemDeServico.getId())))
				&& controleValidaIMEI && !ordemDeServico.getImei().equals(ordemDeServicoDAO.getEntidade().getImei())) {
			controleValidaIMEI = false;
			mensagemModalValidaIMEI = "O aparelho com essa identificação tem um serviço na garantia.";
			RequestContext.getCurrentInstance().update("form-continua-ordem");
			RequestContext.getCurrentInstance().execute("PF('continuaOrdem').show();");
			return null;
		}

		ordemDeServico.setUsuario(usuario);
		ordemDeServico.setItemProdutos(itemProdutos);
		ordemDeServico.setItemServicos(itemServicos);
		
		if(ordemDeServico.getChave() == null || ordemDeServico.getId() == null) {
			ordemDeServico.setChave(GeradorChave.formaKey());
		}

		ordemDeServicoDAO.salvar(ordemDeServico, itemProdutos, itemServicos);
		if (ordemDeServicoDAO.isControle()) {

			RequestContext.getCurrentInstance().execute("PF('preparaImpressao').show();");

			if (enviaEmail) {
				setClasseAuxiliarEmail();
				try {
					envioEmail();
				} catch (EmailException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public boolean confereQuantPecasUtilizadasMaiorOuIgualQueQuantServico() {
		long num = this.itemServicos.stream()
                .filter(obj -> obj.getServico().getMaoDeObra().equals(MaoDeObra.COM_PRODUTO))
                .count();
		if(itemProdutos != null && itemServicos != null &&
				itemServicos.size() > 0 && itemProdutos.size() < num) {
			return true;
		}
		return false;
	}

	public void imprimirComprovante() {
		try {
			String filename = "ComprovanteSolicitacaoDeServico.pdf";
			String jasperPath = "";
			
			List<OrdemDeServico> ordemDeServicos = new ArrayList<>();

			jasperPath = "/resources/relatorios/comp_solicitacao_servico.jasper";
			if (ordemDeServico.getValor() == null) {
				ordemDeServico.setValor(BigDecimal.ZERO);
			}
			if (ordemDeServico.getId() == null) {
				ordemDeServico.setId(ordemDeServicoDAO.getEntidade().getId());
				ordemDeServico.setModelo(ordemDeServicoDAO.getEntidade().getModelo());
			}
			imprimeAtributoseValoresPojo(ordemDeServico);
			ordemDeServicos.add(ordemDeServico);

			Map<String, Object> parametros;
			Date data = new Date();
			Locale local = new Locale("pt", "BR");
			DateFormat formato = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy'.'", local);
			//de acordo com a url, será enviado a mensagem para o arquivo jasper
			if(Dominio.retornaDominio(FacesContext.getCurrentInstance()).contains(Dominio.contemDominio)) {
				parametros =  CarregaArquivo.carregaConfiguracao(Dominio.contemDominio);
				parametros.put("acessoSituacao", "Acesse: vidalcell.gessis.com.br/situacao");
				parametros.put("caminhoImgLogo",
						FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images/logo_vidalcell.png"));
			}
			else {
				parametros =  CarregaArquivo.carregaConfiguracao("elitte");
				parametros.put("acessoSituacao", "Acesse: gessis.com.br/situacao");
				parametros.put("caminhoImgLogo",
						FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/images/logo.png"));
			}
			
			if(parametros.get("cidade") != null) {
				parametros.put("cidade", parametros.get("cidade") + ", " + formato.format(data)); //cidade com data
			}
			GeradorRelatorio.PDF(parametros, jasperPath, ordemDeServicos, filename);

		} catch (JRException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Ref:
	 * http://karanalpe.com.br/tecnologia/back-end/como-percorrer-os-atributos-de-um
	 * -pojo-em-java-reflection/ (Principal)
	 * https://www.java-tips.org/java-lang/2513-how-to-set-field-values-using-java-
	 * reflection.html
	 */
	public void imprimeAtributoseValoresPojo(Object object) {
		Class<?> classe = object.getClass();
		Field[] campos = classe.getDeclaredFields();

		// Object valorAtributo = null;
		for (Field campo : campos) {
			try {
				campo.setAccessible(true); // Necessário por conta do encapsulamento das variáveis (private)
				// se for null e ser uma String, vai receber uma String vazia
				if (campo.getGenericType().toString().contains("String")
						&& (campo.get(object) == null || campo.get(object).equals(""))) {
					campo.set(object, "");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void excluir() {
		ordemDeServicoDAO.excluir(ordemDeServico.getId());
		if (ordemDeServicoDAO.isControle()) {
			ordemDeServico = new OrdemDeServico();
			usuario = new Usuario();
			servico = new Servico();
			aparelho = new Aparelho();
			itemProduto = new ItemProduto();
			itemProdutos = new ArrayList<>();
			idOrdem = null;
		}
	}

	public void salvarServico() {
		servicoDAO.salvar(servico);
		if (servicoDAO.isControle()) {
			itemServico.setServico(servicoDAO.getEntidade());
			nomeServico = itemServico.getServico().getCodDescricao();
			modelServico.setServico(itemServico.getServico());
			modelServico.addLista();
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("servicosOrdemServico",
					servicos);
		}
	}

	// ação do botão quando ser pressionado
	public void adicionarServico() {
		servico = new Servico();
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

	public void salvarCliente() {

		if (telefones != null && telefones.size() > 0) {
			cliente.setTelefones(telefones);
			clienteDAO.salvar(cliente);

			if (clienteDAO.isControle()) {
				ordemDeServico.setCliente(clienteDAO.getEntidade());
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
	}

	public void salvarAparelho() {
		aparelhoDAO.salvar(aparelho);
		if (aparelhoDAO.isControle()) {
			aparelho = aparelhoDAO.getEntidade();
			nomeAparelho = aparelho.getMarca();
			modelAparelho.setAparelho(aparelho);
			modelAparelho.addLista();
			request.addCallbackParam("sucessoAparelho", true);
		} else {
			aparelho = new Aparelho();
			nomeAparelho = null;
		}
	}

	public void salvarModelo() {
		modelo.setAparelho(aparelho);
		modeloDAO.salvar(modelo);
		if (modeloDAO.isControle()) {
			modelo = modeloDAO.getEntidade();
			modelo.setAparelho(aparelho);
			ordemDeServico.setModelo(modelo);
			nomeModelo = modelo.getModelo();
			modelModelo.setModelo(modelo);
			modelModelo.addLista();
			request.addCallbackParam("sucessoModelo", true);
		}
	}

	public void ordemSelecionado(SelectEvent event) {
		OrdemDeServico o = (OrdemDeServico) event.getObject();
		ordemDeServico = o;
	}

	public void onRowSelectOrdem(SelectEvent event) {
		OrdemDeServico o = (OrdemDeServico) event.getObject();
		ordemDeServico = o;
	}

	public void selecionarCliente(SelectEvent event) {
		cliente = modelCliente.busca(nomeCliente);
		ordemDeServico.setCliente(cliente);
		nomeCliente = null;
	}

	public void adicionarCliente() {
		if (ordemDeServico.getCliente() != null) {
			ordemDeServico.setCliente(new Cliente());
		}
		if (cliente != null) {
			cliente = new Cliente();
		}
	}

	public void selecionarAparelho(SelectEvent event) {
		aparelho = modelAparelho.busca(nomeAparelho, false);
		nomeModelo = null;
		if (ordemDeServico.getModelo() != null) {
			ordemDeServico.setModelo(new Modelo());
		}
	}

	public void adicionarAparelho() {
		aparelho = new Aparelho();
		ordemDeServico.setModelo(new Modelo());
		nomeAparelho = null;
		nomeModelo = null;
	}

	public void selecionarModelo(SelectEvent event) {
		modelo = modelModelo.busca(nomeModelo, false);
		//m.setAparelho(aparelho); // O correto seria fazer o Hibernate,
									// mas fiz dessa maneira para evitar uma consulta com join, sendo que a
									// informação já está em memória
		ordemDeServico.setModelo(modelo);
		nomeModelo = null;
	}

	public void adicionarModelo() {
		ordemDeServico.setModelo(new Modelo());
		modelo = new Modelo();
		nomeModelo = null;
	}
	
	public List<String> filtroProduto(String txt){
		modelProduto.setComEntradaGerada(true);
		return modelProduto.filterTrazendoListas(txt, 15);
	}
	
	public List<String> filtroServico(String txt){
		return modelServico.filtro(txt, 15);
	}
	
	public List<String> filtroAparelho(String txt){
		return modelAparelho.filtro(txt, 15);
	}
	
	//filtra o que está na memória, para não ficar fazendo requisições no banco
	public List<String> filtroModelo(String txt){
		if (aparelho.getId() == null) {
			FacesUtil.addWarnMessage("O campo Aparelho não foi informado!", "");
			return null;
		} 
		return modelModelo.filtro(txt, aparelho.getId(), 15);
	}
	
	public List<String> filtroCliente(String txt){
		return modelCliente.filtro(txt, 15);
	}

	public void setaValorUnitario(SelectEvent event) {
		//O que estava na documentação -> 
		//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Item Selected", event.getObject().toString()));		
		descricaoProduto = (String) event.getObject();
		produto = modelProduto.busca(descricaoProduto);
		
		ItemEntrada i = null;
		i = itemEntradaDAO.retornaValorDaUltimaEntradaProd(produto.getId());

		if (i != null) {

			setUltimoValorEntrada(i.getValorUnit());

			if (produto.getValorInformado() != null) {
				produto.setValorSugerido(produto.getValorInformado());
			} else {
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
	}

	public String salvarNaListaDeItens() {
		
		produto = modelProduto.busca(descricaoProduto);
		itemProduto.setProduto(produto);	

		if (Validacoes.verificaProduto(itemProduto.getProduto(), guardaIndexItemProd, itemProdutos)) {
			FacesUtil.addWarnMessage("Esse produto já foi informado para essa Ordem de Serviço!", "");
			return null;
		}
		if (itemProduto.getProduto().getDescricao() == null) {
			return null;
		}
		if (retornaQuantEstoque() < itemProduto.getQuant()) {
			FacesUtil.addWarnMessage("Não tem essa quantidade suficiente em estoque!", "");
			return null;
		}
		if (guardaIndexItemProd == -1) {
			this.itemProdutos.add(itemProduto);
		} else {
			this.itemProdutos.set(guardaIndexItemProd, itemProduto);
			guardaIndexItemProd = -1;
			txtBotaoInserir = "Adicionar";
			txtTitleBotaoInserir = "Adicionar à lista de peças";
		}
		valorMinimo = new BigDecimal("0.00");
		for (ItemProduto i : itemProdutos) {
			valorMinimo = OperacoesComBigDecimal.soma(valorMinimo, i.getSubTotal());
		}
		modelItemProduto.parametros.put("listaDeItens", itemProdutos);
		/*
		 * itemProduto = new ItemProduto(); produto = new Produto();
		 * itemProduto.setQuant(1);
		 */
		RequestContext.getCurrentInstance().execute("PF('addProduto').hide();window.scrollTo(0, irParaFimDoScroll());");

		return null;
	}

	public int retornaQuantEstoque() {

		if (itemProduto.getProduto().getItemEntradas() == null
				|| itemProduto.getProduto().getItemEntradas().size() == 0) {
			itemProduto.getProduto()
					.setItemEntradas(modelProduto.getItemEntradaDAO().porIdProduto(itemProduto.getProduto().getId()));
		}

		if (itemProduto.getProduto().getItemProdSaidas() == null
				|| itemProduto.getProduto().getItemProdSaidas().size() == 0) {
			// verifica se já tem itens na saida de produtos, no caso verifica se é uma
			// venda
			itemProduto.getProduto().setItemProdSaidas(
					modelProduto.getItemProdSaidaDAO().porIdProduto(itemProduto.getProduto().getId()));
		}

		if (itemProduto.getProduto().getItemProdutos() == null
				|| itemProduto.getProduto().getItemProdutos().size() == 0) {
			if (ordemDeServico.getId() != null) {
				itemProduto.getProduto().setItemProdutos(modelProduto.getItemProdutoDAO()
						.porIdProdutoSemDeterminadaOrdem(itemProduto.getProduto().getId(), ordemDeServico.getId()));
			} else {
				itemProduto.getProduto().setItemProdutos(
						modelProduto.getItemProdutoDAO().porIdProduto(itemProduto.getProduto().getId()));
			}
		} else if (ordemDeServico.getId() != null) {
			itemProduto.getProduto()
					.setItemProdutos(itemProduto.getProduto().getItemProdutos().stream()
							.filter(line -> !line.getOrdemDeServico().getId().equals(ordemDeServico.getId()))
							.collect(Collectors.toList()));
		}

		itemProduto.getProduto().setQuant(
				itemProduto.getProduto().getItemEntradas()
				.stream().map(ItemEntrada::getQuant).reduce(0, Integer::sum));
		itemProduto.getProduto().setQuant(itemProduto.getProduto().getQuant() - (itemProduto.getProduto().getItemProdSaidas()
				.stream().map(ItemProdSaida::getQuant).reduce(0, Integer::sum)));
		
		//itens onde o estado da ordem está como ,GARANTIA_CONCLUIDA, GARANTIA_ANDAMENTO, APROVADO, FINALIZADO
		itemProduto.getProduto().setItemProdutos(itemProduto.getProduto().getItemProdutos().stream()
				.filter(line -> ordemDeServico.listaEnumTiraEstoque().contains(line.getOrdemDeServico().getEstado().getCod()))
	            .collect(Collectors.toList()));
		
		itemProduto.getProduto().setQuant(itemProduto.getProduto().getQuant() - (itemProduto.getProduto().getItemProdutos()
				.stream().map(ItemProduto::getQuant).reduce(0, Integer::sum)));

		return itemProduto.getProduto().getQuant();
	}

	public void preparaInclusao() {
		itemProduto = new ItemProduto();
		produto = new Produto();
		itemProduto.setQuant(1);
		descricaoProduto = null;
		itemProduto.setValorUnit(new BigDecimal("0.00"));
		guardaIndexItemProd = -1;
		txtBotaoInserir = "Adicionar";
		// RequestContext.getCurrentInstance().execute("PF('addProduto').show();");
	}

	public void removerNaListaDeItens(ItemProduto i) {
		if (i.getId() != null) {
			itemProdutos.removeIf(x -> i.getId().equals(x.getId()));
		} else {
			itemProdutos.removeIf(x -> x.getProduto().getId().equals(i.getProduto().getId()));
		}
		modelItemProduto.parametros.put("listaDeItens", itemProdutos);
	}

	public void alterarNaListaDeItens(ItemProduto i) {
		itemProduto = new ItemProduto();
		produto = new Produto();
		itemProduto.setId(i.getId());
		itemProduto.setOrdemDeServico(ordemDeServico);
		itemProduto.setProduto(i.getProduto());
		itemProduto.setQuant(i.getQuant());
		itemProduto.setValorUnit(i.getValorUnit());
		produto = itemProduto.getProduto();
		descricaoProduto = produto.getCodDescricao();
		/*caso o objeto selecionado não exista na lista, ele terá que ser adicionado, 
		para depois fazer na validação na hora que o método salvarNaLista ser chamado*/
		if(!modelProduto.getLista().contains(produto)) {
			modelProduto.setProduto(produto);
			modelProduto.addLista();
		}
		setUltimoValorEntrada(itemEntradaDAO.retornaValorDaUltimaEntradaProd(produto.getId()).getValorUnit());
		// pega indice do item na lista
		guardaIndexItemProd = itemProdutos.indexOf(i);
		// muda texto do botão
		txtBotaoInserir = "Alterar";
		txtTitleBotaoInserir = "Alterar item da lista";
	}

	public void salvarNaListaDeServicos() {
		
		servico = modelServico.busca(nomeServico);

		if (!Validacoes.verificaServico(servico, guardaIndexItemServ, itemServicos)) {
			itemServico.setServico(servico);
			if (itemServico.getServico().getDescricao() != null) {
				if (guardaIndexItemServ == -1) {
					this.itemServicos.add(itemServico);
				} else {
					this.itemServicos.set(guardaIndexItemServ, itemServico);
					guardaIndexItemServ = -1;
					txtBotaoInserir = "Adicionar";
					txtTitleBotaoInserir = "Adicionar à lista de peças";
				}
				modelItemServico.parametros.put("listaDeItens", itemServicos);
				RequestContext.getCurrentInstance().execute("PF('addItemServico').hide();");
			}
			
		} else {
			FacesUtil.addWarnMessage("Esse serviço já foi informado para essa Ordem de Serviço!", "");
		}

	}

	public void preparaInclusaoServicos() {
		itemServico = new ItemServico();
		servico = new Servico();
		nomeServico = null;
		guardaIndexItemServ = -1;
		txtBotaoInserir = "Adicionar";
	}

	public void removerNaListaDeServicos(ItemServico i) {
		if (i.getId() != null) {
			itemServicos.removeIf(x -> i.getId().equals(x.getId()));
		} else {
			itemServicos.removeIf(x -> x.getServico().getId().equals(i.getServico().getId()));
		}
		modelItemServico.parametros.put("listaDeItens", itemServicos);
	}

	public void alterarNaListaDeServicos(ItemServico i) {
		itemServico = new ItemServico();
		servico = new Servico();
		itemServico.setId(i.getId());
		itemServico.setOrdemDeServico(ordemDeServico);
		servico = i.getServico();
		nomeServico = servico.getCodDescricao();
		/*caso o objeto selecionado não exista na lista, ele terá que ser adicionado, 
		para depois fazer na validação na hora que o método salvarNaLista ser chamado*/
		if(!modelServico.getLista().contains(servico)) {
			modelServico.setServico(servico);
			modelServico.addLista();
		}
		// pega indice do item na lista
		guardaIndexItemServ = itemServicos.indexOf(i);
		// muda texto do botão
		txtBotaoInserir = "Alterar";
		txtTitleBotaoInserir = "Alterar item da lista";
	}

	public void validaValorRecomendadoVenda(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		BigDecimal valor = (BigDecimal) value;
		valor = OperacoesComBigDecimal.soma(valor, OperacoesComBigDecimal.valorZero);

		if (OperacoesComBigDecimal.numUmMaiorQueNumDois(this.ultimoValorEntrada, valor)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção com o valor!",
					"O valor informado é menor do que a última entrada desse produto!"));
		}

	}

	public void validaValorTotal(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		BigDecimal valorTotal = (BigDecimal) value;
		boolean avalia = true;
		
		if(ordemDeServico.getEstado() != Estado.GARANTIA_ANDAMENTO && 
				ordemDeServico.getEstado() != Estado.GARANTIA_FALTA_PECA && 
				ordemDeServico.getEstado() != Estado.GARANTIA_CONCLUIDA) {
			
			if (valorTotal != null) {
				valorMinimo = OperacoesComBigDecimal.valorZero;
				for (ItemProduto i : itemProdutos) {
					valorMinimo = OperacoesComBigDecimal.soma(valorMinimo, i.getSubTotal());
				}
				valorTotal = OperacoesComBigDecimal.soma(valorTotal, OperacoesComBigDecimal.valorZero);
	
				avalia = (!verificaEstado() && itemProdutos.size() == 0
						&& valorTotal.compareTo(new BigDecimal("0.99")) != 1)
						|| (!verificaEstado() && itemProdutos.size() == 0
								&& valorTotal.compareTo(new BigDecimal("0.99")) == 1)
						|| (verificaEstado() && OperacoesComBigDecimal.numUmMaiorOuIgualNumDois(valorTotal, valorMinimo)
								&& valorTotal.compareTo(new BigDecimal("0.99")) == 1)
						|| (itemProdutos.size() > 0
								&& OperacoesComBigDecimal.numUmMaiorOuIgualNumDois(valorTotal, valorMinimo));
			}
		
			if (!avalia) {
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção com esse valor!",
						"Valor informado é menor do que o permitido!"));
			}
			
		}
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

	public OrdemDeServico getOrdemDeServico() {
		return ordemDeServico;
	}

	public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
		this.ordemDeServico = ordemDeServico;
	}

	public Long getIdOrdem() {
		return idOrdem;
	}

	public void setIdOrdem(Long idOrdem) {
		this.idOrdem = idOrdem;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Aparelho getAparelho() {
		return aparelho;
	}

	public void setAparelho(Aparelho aparelho) {
		this.aparelho = aparelho;
	}

	public Modelo getModelo() {
		return modelo;
	}

	public void setModelo(Modelo modelo) {
		this.modelo = modelo;
	}

	public String getPessoa() {
		return pessoa;
	}

	public void setPessoa(String pessoa) {
		this.pessoa = pessoa;
	}

	public String getNomeModelo() {
		return nomeModelo;
	}

	public void setNomeModelo(String nomeModelo) {
		this.nomeModelo = nomeModelo;
	}

	public List<Cliente> getClientesFiltrados() {
		return clientesFiltrados;
	}

	public void setClientesFiltrados(List<Cliente> clientesFiltrados) {
		this.clientesFiltrados = clientesFiltrados;
	}

	public Servico getServico() {
		return servico;
	}

	public void setServico(Servico servico) {
		this.servico = servico;
	}

	public List<Servico> getServicos() {
		return servicos;
	}

	public void setServicos(List<Servico> servicos) {
		this.servicos = servicos;
	}

	public List<ItemProduto> getItemProdutos() {
		return itemProdutos;
	}

	public void setItemProdutos(List<ItemProduto> itemProdutos) {
		this.itemProdutos = itemProdutos;
	}

	public ItemProduto getItemProduto() {
		return itemProduto;
	}

	public void setItemProduto(ItemProduto itemProduto) {
		this.itemProduto = itemProduto;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
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

	public String getDescricaoProduto() {
		return descricaoProduto;
	}

	public void setDescricaoProduto(String descricaoProduto) {
		this.descricaoProduto = descricaoProduto;
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

	public BigDecimal getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo(BigDecimal valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	public List<QuatMesesGarantia> getListaEnumGarantia() {
		return listaEnumGarantia;
	}

	public void setListaEnumGarantia(List<QuatMesesGarantia> listaEnumGarantia) {
		this.listaEnumGarantia = listaEnumGarantia;
	}

	public List<Estado> getListaEnumEstado() {
		return listaEnumEstado;
	}

	public void setListaEnumEstado(List<Estado> listaEnumEstado) {
		this.listaEnumEstado = listaEnumEstado;
	}

	public List<FormaPagamento> getListaEnumFormaPagamento() {
		return listaEnumFormaPagamento;
	}

	public void setListaEnumFormaPagamento(List<FormaPagamento> listaEnumFormaPagamento) {
		this.listaEnumFormaPagamento = listaEnumFormaPagamento;
	}

	public List<EnumSimNaoGenerico> getListaEnumSimNaoGenerico() {
		return listaEnumSimNaoGenerico;
	}

	public void setListaEnumSimNaoGenerico(List<EnumSimNaoGenerico> listaEnumSimNaoGenerico) {
		this.listaEnumSimNaoGenerico = listaEnumSimNaoGenerico;
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

	public boolean isDisabledCamposForm() {
		return disabledCamposForm;
	}

	public void setDisabledCamposForm(boolean disabledCamposForm) {
		this.disabledCamposForm = disabledCamposForm;
	}

	public LazyDataModel<Cliente> getModelCliente() {
		return modelCliente;
	}

	public AparelhoLazyDataModel getModelAparelho() {
		return modelAparelho;
	}

	public void setModelAparelho(AparelhoLazyDataModel modelAparelho) {
		this.modelAparelho = modelAparelho;
	}

	public ModeloLazyDataModel getModelModelo() {
		return modelModelo;
	}

	public void setModelModelo(ModeloLazyDataModel modelModelo) {
		this.modelModelo = modelModelo;
	}

	public ProdutoLazyDataModel getModelProduto() {
		return modelProduto;
	}

	public void setModelProduto(ProdutoLazyDataModel modelProduto) {
		this.modelProduto = modelProduto;
	}

	public ItemProdutoLazyDataModel getModelItemProduto() {
		return modelItemProduto;
	}

	public void setModelItemProduto(ItemProdutoLazyDataModel modelItemProduto) {
		this.modelItemProduto = modelItemProduto;
	}

	public boolean isEnviaEmail() {
		return enviaEmail;
	}

	public void setEnviaEmail(boolean enviaEmail) {
		this.enviaEmail = enviaEmail;
	}

	public String getMensagemModalValidaIMEI() {
		return mensagemModalValidaIMEI;
	}

	public void setMensagemModalValidaIMEI(String mensagemModalValidaIMEI) {
		this.mensagemModalValidaIMEI = mensagemModalValidaIMEI;
	}

	public ServicoLazyDataModel getModelServico() {
		return modelServico;
	}

	public void setModelServico(ServicoLazyDataModel modelServico) {
		this.modelServico = modelServico;
	}

	public String getNomeServico() {
		return nomeServico;
	}

	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}

	public ItemServico getItemServico() {
		return itemServico;
	}

	public void setItemServico(ItemServico itemServico) {
		this.itemServico = itemServico;
	}

	public ItemServicoLazyDataModel getModelItemServico() {
		return modelItemServico;
	}

	public void setModelItemServico(ItemServicoLazyDataModel modelItemServico) {
		this.modelItemServico = modelItemServico;
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

	public List<MaoDeObra> getListaMaoDeObra() {
		return listaMaoDeObra;
	}

	public void setListaMaoDeObra(List<MaoDeObra> listaMaoDeObra) {
		this.listaMaoDeObra = listaMaoDeObra;
	}

}
