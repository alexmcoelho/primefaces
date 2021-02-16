package com.alex.controle;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.DAO.DetalhesFaturaDAO;
import com.alex.DAO.OrdemDeServicoDAO;
import com.alex.DAO.SaidaDeProdutosDAO;
import com.alex.modelo.ClasseAuxiliarDetalhesFaturas;
import com.alex.modelo.DetalhesFatura;
import com.alex.modelo.OrdemDeServico;
import com.alex.modelo.SaidaDeProdutos;
import com.alex.modelo.enums.Estado;
import com.alex.util.FacesUtil;
import com.alex.util.ManipData;
import com.alex.util.OperacoesComBigDecimal;

@Named
@ViewScoped
public class FaturaOrdemCadastroMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;

	@Inject
	private DetalhesFatura detalhesFatura;

	@Inject
	private DetalhesFatura detalhesFaturaSelecionado;

	@Inject
	private DetalhesFaturaDAO detalhesFaturaDAO;

	@Inject
	private SaidaDeProdutosDAO saidaDeProdutosDAO;

	@Inject
	private OrdemDeServicoDAO ordemDeServicoDAO;

	@Inject
	private OrdemDeServico ordemDeServico;

	@Inject
	private SaidaDeProdutos saidaDeProduto;

	private Long idSaidaOuOrdem;

	private String tipoCadastro;

	private boolean aVista = false;

	private boolean temEntrada;

	private int quantParcelas = 0;

	private int diaVencimento = 0;

	private List<DetalhesFatura> detalhesFaturas = new ArrayList<>();

	private Date dataHoje;

	private Calendar calendar = Calendar.getInstance();

	private int guardaIndex = -1;

	private BigDecimal valorCobrado = new BigDecimal("0.00");

	private BigDecimal valorQueRestaPagar = new BigDecimal("0.00");

	private BigDecimal valorTotal = new BigDecimal("0.00");

	private BigDecimal valorDescontado;

	private String textoValorPagar = "Valor total à cobrar: ";

	private String textoValorPago = "Valor que está sendo cobrado: ";

	private String textoRestaCobrar;

	private String textoPagar;

	// ira ser usada na divisão do valor do servico pela quantidade parcelas, tambem
	// no calculo de diferenca entre
	// valorCobrado e ordemDeServico.getValor()
	private BigDecimal quantidade;

	private Long idOrdem;

	private Long idSaida;

	@Inject
	private ClasseAuxiliarDetalhesFaturas classeAuxiliarDetalhesFaturas;

	public void inicializar() {
		if (idOrdem != null) {
			inicializacaoParcelasOrdemDeServico();
		} else if (idSaida != null) {
			inicializacaoParcelasSaidaDeProdutos();
		}
	}

	public void inicializacaoParcelasOrdemDeServico() {
		saidaDeProduto = null;
		ordemDeServico = ordemDeServicoDAO.objporEstadoAndId(idOrdem, Estado.FINALIZADO);
		
		if(ordemDeServico != null) {
			
			detalhesFaturas = new ArrayList<>(ordemDeServico.getDetalhesFaturas());
			// seta Valor total
			classeAuxiliarDetalhesFaturas.setValorTotal(ordemDeServico.getValor());
			
			// seta Valor total cobrado
			classeAuxiliarDetalhesFaturas.setValorTotalCobrado(ordemDeServico.getDetalhesFaturas().stream()
					.map(DetalhesFatura::getValorParcela).reduce(BigDecimal.ZERO, BigDecimal::add));
			
			// seta Valor pago até o agora
			classeAuxiliarDetalhesFaturas
			.setValorPago(ordemDeServico.getDetalhesFaturas().stream().filter(o -> o.isBaixada() == true)
					.map(DetalhesFatura::getValorParcela).reduce(BigDecimal.ZERO, BigDecimal::add));
			
			// seta Valor que resta à pagar
			if (classeAuxiliarDetalhesFaturas.getValorTotalCobrado().equals(BigDecimal.ZERO)) {
				classeAuxiliarDetalhesFaturas.setValorRestante(OperacoesComBigDecimal.subtracao(
						classeAuxiliarDetalhesFaturas.getValorTotal(), classeAuxiliarDetalhesFaturas.getValorPago()));
			} else {
				classeAuxiliarDetalhesFaturas.setValorRestante(
						OperacoesComBigDecimal.subtracao(classeAuxiliarDetalhesFaturas.getValorTotalCobrado(),
								classeAuxiliarDetalhesFaturas.getValorPago()));
			}
			
			// acrescenta o que falta no txt Valores correspondentes
			classeAuxiliarDetalhesFaturas.setTxtValorCorrespondente(classeAuxiliarDetalhesFaturas
					.getTxtValorCorrespondente().append("Ordem de Serviço Nº ").append(ordemDeServico.getId()));
			
			// seta o nome do cliente
			classeAuxiliarDetalhesFaturas.setTxtNomeCliente(
					classeAuxiliarDetalhesFaturas.getTxtNomeCliente().append(ordemDeServico.getCliente().getNome()));
			
			// filtrando a lista, deixando só as parcelas que ainda não foram baixadas
			detalhesFaturas = detalhesFaturas.stream() // convert list to stream
					.filter(line -> !line.isBaixada()) // we dont like nameConstraint
					.collect(Collectors.toList());
		}

		idOrdem = null;
	}

	public void inicializacaoParcelasSaidaDeProdutos() {
		ordemDeServico = null;
		if (idSaida != null) {

			saidaDeProduto = saidaDeProdutosDAO.porID(idSaida);
			
			if(saidaDeProduto != null) {
				detalhesFaturas = new ArrayList<>(saidaDeProduto.getDetalhesFaturas());
				// seta Valor total
				classeAuxiliarDetalhesFaturas.setValorTotal(saidaDeProduto.getValor());
				
				// seta Valor total cobrado
				classeAuxiliarDetalhesFaturas.setValorTotalCobrado(saidaDeProduto.getDetalhesFaturas().stream()
						.map(DetalhesFatura::getValorParcela).reduce(BigDecimal.ZERO, BigDecimal::add));
				
				// seta Valor pago até o agora
				classeAuxiliarDetalhesFaturas
				.setValorPago(saidaDeProduto.getDetalhesFaturas().stream().filter(o -> o.isBaixada() == true)
						.map(DetalhesFatura::getValorParcela).reduce(BigDecimal.ZERO, BigDecimal::add));
				
				// seta Valor que resta à pagar
				if (classeAuxiliarDetalhesFaturas.getValorTotalCobrado().equals(BigDecimal.ZERO)) {
					classeAuxiliarDetalhesFaturas.setValorRestante(OperacoesComBigDecimal.subtracao(
							classeAuxiliarDetalhesFaturas.getValorTotal(), classeAuxiliarDetalhesFaturas.getValorPago()));
				} else {
					classeAuxiliarDetalhesFaturas.setValorRestante(
							OperacoesComBigDecimal.subtracao(classeAuxiliarDetalhesFaturas.getValorTotalCobrado(),
									classeAuxiliarDetalhesFaturas.getValorPago()));
				}
				
				// acrescenta o que falta no txt Valores correspondentes
				classeAuxiliarDetalhesFaturas.setTxtValorCorrespondente(classeAuxiliarDetalhesFaturas
						.getTxtValorCorrespondente().append("Venda Nº ").append(saidaDeProduto.getId()));
				
				// seta o nome do cliente
				classeAuxiliarDetalhesFaturas.setTxtNomeCliente(
						classeAuxiliarDetalhesFaturas.getTxtNomeCliente().append(saidaDeProduto.getCliente().getNome()));
				
				// filtrando a lista, deixando só as parcelas que ainda não foram baixadas
				detalhesFaturas = detalhesFaturas.stream() // convert list to stream
						.filter(line -> !line.isBaixada()) // we dont like nameConstraint
						.collect(Collectors.toList());
			}

			idSaida = null;
		}
	}

	public void atualizaValorAVista() {
		if (aVista) {
			detalhesFatura.setValorParcela(OperacoesComBigDecimal.subtracao(
					classeAuxiliarDetalhesFaturas.getValorTotal(), classeAuxiliarDetalhesFaturas.getValorPago()));
		} else {
			detalhesFatura.setValorParcela(BigDecimal.valueOf(0.00));
		}
	}

	@Override
	public String caminhoCancelar() {
		return "/index?faces-redirect=true";
	}

	public int getTamanhoLista() {
		return detalhesFaturas.size();
	}

	public void aplicar() {
		dataHoje = new Date();
		// para calcula valor das parcelas
		String quant = "" + quantParcelas;
		quantidade = new BigDecimal(quant);
		Long id = 1L;
		detalhesFaturas = new ArrayList<>();

		valorTotal = OperacoesComBigDecimal.subtracao(classeAuxiliarDetalhesFaturas.getValorTotal(),
				classeAuxiliarDetalhesFaturas.getValorPago());
		valorCobrado = classeAuxiliarDetalhesFaturas.getValorTotalCobrado();

		if (aVista == true) {
			// caso entre aqui o valor da parcela já estará setado
			detalhesFatura.setDescricao("Parcela única");
			detalhesFatura.setOrdemDeServico(ordemDeServico);
			detalhesFatura.setSaidaDeProdutos(saidaDeProduto);
			detalhesFatura.setId(id);// atribui um id temporario, mas na hora de inserir ele tera que ser null
			detalhesFaturas.add(detalhesFatura);
			classeAuxiliarDetalhesFaturas.setValorTotalCobrado(OperacoesComBigDecimal
					.soma(detalhesFatura.getValorParcela(), classeAuxiliarDetalhesFaturas.getValorPago()));
			detalhesFatura = new DetalhesFatura();
		} else {
			// vai gerar o valor que resta a pagar (seja ele o valor total ou nao) - somando
			// a cada valorParcela gerado
			valorCobrado = BigDecimal.valueOf(0.00);
			valorDescontado = BigDecimal.valueOf(0.00);
			if (temEntrada == true) {
				detalhesFatura.setDescricao("Entrada");
				detalhesFatura.setId(id);// atribui um id temporario, na hora de inserir no ele tera que ser null
				valorCobrado = valorCobrado.add(detalhesFatura.getValorParcela(), MathContext.DECIMAL128).setScale(2,
						RoundingMode.HALF_EVEN);
				valorDescontado = valorTotal.subtract(valorCobrado, MathContext.DECIMAL128).setScale(2,
						RoundingMode.HALF_EVEN);
				detalhesFatura.setOrdemDeServico(ordemDeServico);
				detalhesFatura.setSaidaDeProdutos(saidaDeProduto);
				detalhesFaturas.add(detalhesFatura);
				detalhesFatura = new DetalhesFatura();
			} else {
				valorDescontado = valorTotal;
			}
			BigDecimal valorParcelas = BigDecimal.valueOf(0.00);
			BigDecimal valorUltimaParcela = BigDecimal.valueOf(0.00);
			valorParcelas = valorDescontado.divide(quantidade, MathContext.DECIMAL128).setScale(2,
					RoundingMode.HALF_EVEN);

			// se valorDescontado for maior que valorParcelas * quantParcelas, quer dizer
			// que a divisão não deu um valor exato
			// entao para deixar o valor exato o valor da ultima parcela será =
			// valorParcelas + (valorDescontado - valorParcelas * quantidade)
			if (valorDescontado.compareTo(valorParcelas.multiply(quantidade, MathContext.DECIMAL128).setScale(2,
					RoundingMode.HALF_EVEN)) != 1) {
				valorUltimaParcela = valorParcelas
						.add(valorDescontado.subtract(valorParcelas.multiply(quantidade, MathContext.DECIMAL128)
								.setScale(2, RoundingMode.HALF_EVEN)), MathContext.DECIMAL128)
						.setScale(2, RoundingMode.HALF_EVEN);
			} else {
				valorUltimaParcela = valorParcelas;
			}

			for (int i = 0; i < quantParcelas - 1; i++) {
				detalhesFatura.setDescricao("Parcela " + (i + 1));
				detalhesFatura.setValorParcela(valorParcelas);

				// INICIO - Manip data
				calendar.setTime(ManipData.acrescenta30Dias(dataHoje, diaVencimento));
				detalhesFatura.setDataVencimento(ManipData.checaFDS(calendar).getTime());
				dataHoje = calendar.getTime();
				// FIM - Manip data
				detalhesFatura.setOrdemDeServico(ordemDeServico);
				detalhesFatura.setSaidaDeProdutos(saidaDeProduto);
				id = id + 1;
				detalhesFatura.setId(id);// atribui um id temporário, na hora de inserir ele sera null
				detalhesFaturas.add(detalhesFatura);
				valorCobrado = valorCobrado.add(detalhesFatura.getValorParcela(), MathContext.DECIMAL128).setScale(2,
						RoundingMode.HALF_EVEN);
				detalhesFatura = new DetalhesFatura();
			}

			// gravando a última parcela
			detalhesFatura.setDescricao("Parcela " + quantParcelas);
			detalhesFatura.setValorParcela(valorUltimaParcela);
			BigDecimal totalTemp = detalhesFaturas.stream()
					.map(DetalhesFatura::getValorParcela).reduce(BigDecimal.ZERO, BigDecimal::add)
					.add(valorUltimaParcela);
			if(OperacoesComBigDecimal.numUmMaiorQueNumDois(valorTotal, totalTemp)) {
				totalTemp = OperacoesComBigDecimal.subtracao(totalTemp, valorUltimaParcela);
				detalhesFatura.setValorParcela(OperacoesComBigDecimal.subtracao(valorTotal, totalTemp));
			}
			// INICIO - Manip data
			calendar.setTime(ManipData.acrescenta30Dias(dataHoje, diaVencimento));
			detalhesFatura.setDataVencimento(ManipData.checaFDS(calendar).getTime());
			dataHoje = calendar.getTime();
			// FIM - Manip data
			detalhesFatura.setOrdemDeServico(ordemDeServico);
			detalhesFatura.setSaidaDeProdutos(saidaDeProduto);
			detalhesFatura.setId(id + 1);// atribui um id temporario, na hora de inserir ele sera null
			detalhesFaturas.add(detalhesFatura);
			valorCobrado = valorCobrado.add(detalhesFatura.getValorParcela(), MathContext.DECIMAL128).setScale(2,
					RoundingMode.HALF_EVEN);
			detalhesFatura = new DetalhesFatura();

		}
		quantParcelas = 0;
		diaVencimento = 0;

		// seta Valor total cobrado
		classeAuxiliarDetalhesFaturas.setValorTotalCobrado(OperacoesComBigDecimal.soma(
				detalhesFaturas.stream().map(DetalhesFatura::getValorParcela).reduce(BigDecimal.ZERO, BigDecimal::add),
				classeAuxiliarDetalhesFaturas.getValorPago()));

		// seta Valor que resta à pagar
		if (classeAuxiliarDetalhesFaturas.getValorTotalCobrado().equals(BigDecimal.ZERO)) {
			classeAuxiliarDetalhesFaturas.setValorRestante(OperacoesComBigDecimal.subtracao(
					classeAuxiliarDetalhesFaturas.getValorTotal(), classeAuxiliarDetalhesFaturas.getValorPago()));
		} else {
			classeAuxiliarDetalhesFaturas.setValorRestante(
					OperacoesComBigDecimal.subtracao(classeAuxiliarDetalhesFaturas.getValorTotalCobrado(),
							classeAuxiliarDetalhesFaturas.getValorPago()));
		}
	}
	
	public Double recebeBigDecimalDevolveDouble(BigDecimal valor) {
		Double valorD = valor.doubleValue();
		DecimalFormat formatador = new DecimalFormat("0.00");
		String valorS = formatador.format(valorD);
		valorS = valorS.replaceAll(",", ".");
		valorD = Double.parseDouble(valorS);
		return valorD;
	}

	public BigDecimal comparaValorTotal(BigDecimal valorTotal) {
		/*
		 * Ex. do que faz: se valorTotal-valorCobrado=0.01 ou 0.02 faz troca, igualando
		 * o valorCobrado com o valorTotal, o que acontece na expressão: valorCobrado =
		 * ordemDeServico.getValor()
		 */
		quantidade = new BigDecimal("0.02");
		valorTotal = new BigDecimal("0.00");

		double result1 = recebeBigDecimalDevolveDouble(
				valorCobrado.subtract(valorTotal, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN));
		double result2 = recebeBigDecimalDevolveDouble(
				valorTotal.subtract(valorCobrado, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN));

		if ((result1 <= 0.02 && result1 >= 0.00) || (result2 <= 0.02 && result2 >= 0.00)) {
			valorCobrado = valorTotal;
		}
		return valorCobrado;
	}

	public void alterarNaLista() {
		if (guardaIndex != -1) {
			this.detalhesFaturas.set(guardaIndex, detalhesFaturaSelecionado);
			valorCobrado = new BigDecimal("0.00");

			detalhesFaturaSelecionado = new DetalhesFatura();

			// seta Valor total cobrado
			classeAuxiliarDetalhesFaturas.setValorTotalCobrado(
					OperacoesComBigDecimal.soma(detalhesFaturas.stream().map(DetalhesFatura::getValorParcela)
							.reduce(BigDecimal.ZERO, BigDecimal::add), classeAuxiliarDetalhesFaturas.getValorPago()));

			// seta Valor que resta à pagar
			if (classeAuxiliarDetalhesFaturas.getValorTotalCobrado().equals(BigDecimal.ZERO)) {
				classeAuxiliarDetalhesFaturas.setValorRestante(OperacoesComBigDecimal.subtracao(
						classeAuxiliarDetalhesFaturas.getValorTotal(), classeAuxiliarDetalhesFaturas.getValorPago()));
			} else {
				classeAuxiliarDetalhesFaturas.setValorRestante(
						OperacoesComBigDecimal.subtracao(classeAuxiliarDetalhesFaturas.getValorTotalCobrado(),
								classeAuxiliarDetalhesFaturas.getValorPago()));
			}
		}
	}

	public void selecionarDetalhe(DetalhesFatura d) {
		detalhesFaturaSelecionado = new DetalhesFatura();
		detalhesFaturaSelecionado.setId(d.getId());
		detalhesFaturaSelecionado.setDescricao(d.getDescricao());
		detalhesFaturaSelecionado.setDataVencimento(d.getDataVencimento());
		detalhesFaturaSelecionado.setValorParcela(d.getValorParcela());
		detalhesFaturaSelecionado.setBaixada(d.isBaixada());
		detalhesFaturaSelecionado.setOrdemDeServico(d.getOrdemDeServico());
		detalhesFaturaSelecionado.setSaidaDeProdutos(d.getSaidaDeProdutos());
		// pega indice do item na lista
		guardaIndex = detalhesFaturas.indexOf(d);
	}

	public void salvar() {

		if (detalhesFaturas.size() > 0) {
			if (detalhesFaturas.get(0).getOrdemDeServico() != null
					|| detalhesFaturas.get(0).getSaidaDeProdutos() != null) {
				classeAuxiliarDetalhesFaturas.setValorTotal(classeAuxiliarDetalhesFaturas.getValorTotal().setScale(2, RoundingMode.HALF_EVEN));
				if (OperacoesComBigDecimal.numUmMaiorOuIgualNumDois(classeAuxiliarDetalhesFaturas.getValorTotal(),
						classeAuxiliarDetalhesFaturas.getValorTotalCobrado())) {
					/*
					 * coloca todos os id = null, pois é o banco que vai gerar isso automaticamente
					 * foi colocado um id automatico, para poder alterar as parcelas geradas
					 */
					DetalhesFatura d = new DetalhesFatura();
					for (int i = 0; i < detalhesFaturas.size(); i++) {
						d = detalhesFaturas.get(i);
						d.setId(null);
						detalhesFaturas.set(i, d);
						d = new DetalhesFatura();
					}
					if (detalhesFaturaDAO.salvar(detalhesFaturas) != null) {
						FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoSalvar(), "");
						quantParcelas = 0;
						diaVencimento = 0;
						detalhesFaturas = new ArrayList<>();
						detalhesFatura = new DetalhesFatura();
						ordemDeServico = new OrdemDeServico();
						saidaDeProduto = new SaidaDeProdutos();
						temEntrada = false;
						valorCobrado = BigDecimal.ZERO;
						valorQueRestaPagar = BigDecimal.ZERO;
						classeAuxiliarDetalhesFaturas.iniciaObj();
					} else {
						/* Caso dê alogo errado ao salvar seta ids novamente */
						for (int i = 0; i < detalhesFaturas.size(); i++) {
							d = detalhesFaturas.get(i);
							d.setId(Long.valueOf(i));
							detalhesFaturas.set(i, d);
							d = new DetalhesFatura();
						}
						FacesUtil.addWarnMessage(FacesUtil.mensagemPadraoErroAoSalvar(), "");
					}
				} else {
					FacesUtil.addWarnMessage("O Valor cobrado não pode ser maior que o Valor total!", "");
				}

			} else {
				FacesUtil.addWarnMessage(
						"Nenhuma ordem de serviço está indexada a página! Volte a página anterior e a selecione novamente.",
						"");
			}

		} else {
			FacesUtil.addWarnMessage("Antes de salvar é necessário APLICAR a forma de pagamento!", "");
		}

	}

	public void excluir() {
		detalhesFaturaDAO.excluir(detalhesFatura.getId());
	}

	public void valorParcelaInferiorAoTotal(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {

		BigDecimal s = (BigDecimal) value;

		if (OperacoesComBigDecimal.numUmMaiorQueNumDois(s, OperacoesComBigDecimal.subtracao(
				classeAuxiliarDetalhesFaturas.getValorTotal(), classeAuxiliarDetalhesFaturas.getValorPago()))) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção com o valor!",
					"O valor informado é maior que o restante à pagar!"));
		}

		if (OperacoesComBigDecimal.numUmMaiorQueNumDois(BigDecimal.ONE, s)) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção com o valor!",
					"Não pode ser inferior à 1.0!"));
		}

	}

	public DetalhesFatura getDetalhesFatura() {
		return detalhesFatura;
	}

	public void setDetalhesFatura(DetalhesFatura detalhesFatura) {
		this.detalhesFatura = detalhesFatura;
	}

	public Long getIdSaidaOuOrdem() {
		return idSaidaOuOrdem;
	}

	public void setIdSaidaOuOrdem(Long idSaidaOuOrdem) {
		this.idSaidaOuOrdem = idSaidaOuOrdem;
	}

	public boolean isaVista() {
		return aVista;
	}

	public void setaVista(boolean aVista) {
		this.aVista = aVista;
	}

	public boolean isTemEntrada() {
		return temEntrada;
	}

	public void setTemEntrada(boolean temEntrada) {
		this.temEntrada = temEntrada;
	}

	public int getQuantParcelas() {
		return quantParcelas;
	}

	public void setQuantParcelas(int quantParcelas) {
		this.quantParcelas = quantParcelas;
	}

	public int getDiaVencimento() {
		return diaVencimento;
	}

	public void setDiaVencimento(int diaVencimento) {
		this.diaVencimento = diaVencimento;
	}

	public List<DetalhesFatura> getDetalhesFaturas() {
		return detalhesFaturas;
	}

	public void setDetalhesFaturas(List<DetalhesFatura> detalhesFaturas) {
		this.detalhesFaturas = detalhesFaturas;
	}

	public OrdemDeServico getOrdemDeServico() {
		return ordemDeServico;
	}

	public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
		this.ordemDeServico = ordemDeServico;
	}

	public DetalhesFatura getDetalhesFaturaSelecionado() {
		return detalhesFaturaSelecionado;
	}

	public void setDetalhesFaturaSelecionado(DetalhesFatura detalhesFaturaSelecionado) {
		this.detalhesFaturaSelecionado = detalhesFaturaSelecionado;
	}

	public BigDecimal getValorCobrado() {
		return valorCobrado;
	}

	public void setValorCobrado(BigDecimal valorCobrado) {
		this.valorCobrado = valorCobrado;
	}

	public String getTextoValorPagar() {
		return textoValorPagar;
	}

	public void setTextoValorPagar(String textoValorPagar) {
		this.textoValorPagar = textoValorPagar;
	}

	public String getTextoValorPago() {
		return textoValorPago;
	}

	public void setTextoValorPago(String textoValorPago) {
		this.textoValorPago = textoValorPago;
	}

	public String getTextoRestaCobrar() {
		return textoRestaCobrar;
	}

	public void setTextoRestaCobrar(String textoRestaCobrar) {
		this.textoRestaCobrar = textoRestaCobrar;
	}

	public String getTextoPagar() {
		return textoPagar;
	}

	public void setTextoPagar(String textoPagar) {
		this.textoPagar = textoPagar;
	}

	public BigDecimal getValorQueRestaPagar() {
		return valorQueRestaPagar;
	}

	public void setValorQueRestaPagar(BigDecimal valorQueRestaPagar) {
		this.valorQueRestaPagar = valorQueRestaPagar;
	}

	public String getTipoCadastro() {
		return tipoCadastro;
	}

	public void setTipoCadastro(String tipoCadastro) {
		this.tipoCadastro = tipoCadastro;
	}

	public Long getIdOrdem() {
		return idOrdem;
	}

	public void setIdOrdem(Long idOrdem) {
		this.idOrdem = idOrdem;
	}

	public Long getIdSaida() {
		return idSaida;
	}

	public void setIdSaida(Long idSaida) {
		this.idSaida = idSaida;
	}

	public ClasseAuxiliarDetalhesFaturas getClasseAuxiliarDetalhesFaturas() {
		return classeAuxiliarDetalhesFaturas;
	}

	public void setClasseAuxiliarDetalhesFaturas(ClasseAuxiliarDetalhesFaturas classeAuxiliarDetalhesFaturas) {
		this.classeAuxiliarDetalhesFaturas = classeAuxiliarDetalhesFaturas;
	}

}
