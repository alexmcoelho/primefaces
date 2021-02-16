package com.alex.controle;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.DAO.DetalhesFaturaDAO;
import com.alex.modelo.Cliente;
import com.alex.modelo.DetalhesFatura;
import com.alex.modelo.OrdemDeServico;
import com.alex.modelo.Recibo;
import com.alex.modelo.SaidaDeProdutos;
import com.alex.relatorios.GeradorRelatorio;
import com.alex.servico.DetalheFaturaLazyDataModel;
import com.alex.servico.ServicoGenerico;
import com.alex.util.CarregaArquivo;
import com.alex.util.CurrencyWriter;
import com.alex.util.Dominio;
import com.alex.util.EntityManagerProdutor;
import com.alex.util.FacesUtil;

import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class BaixaPagamentosMB extends ComplementoMB implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private DetalhesFaturaDAO detalhesFaturaDAO;
	
	@Inject
	private DetalhesFatura detalhesFatura;
	
	@Inject
	private OrdemDeServico ordemDeServico;
	
	@Inject
	private SaidaDeProdutos saidaDeProduto;
	
	@Inject
	private Cliente cliente;
	
	@Inject
	private DetalhesFatura detalhesFaturaSelecionado; 

	private List<SaidaDeProdutos> saidaDeProdutos = new ArrayList<>();
	
	private List<Recibo> recibos = new ArrayList<>();
	
	private String tipoFatura;
	
	private String tipoPesquisa;
	
	private String faturasBaixadas;
	
	int guardaIndex;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	private Long idOrdemOuSaida;
	
	@Inject
	private DetalheFaturaLazyDataModel model;
	
	@PostConstruct
	public void init() {
		dataFim = EntityManagerProdutor.dataFim;
		dataInicio = EntityManagerProdutor.dataInicio;
		/*
		 * Calendar c = Calendar.getInstance(); c.set(2020, 7, 16); dataFim =
		 * c.getTime();
		 */
		tipoFatura = "ordem";
		tipoPesquisa = "todos";
		faturasBaixadas = "nao";
	}
	
	@Override
	public String getTitulo() {
		return "Pesquisa fatura";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/fatura/baixa-pagamentos.jsf";
	}
	
	/* Este método pesquisa faturas de ordens de serviços e de saídas de produtos (vendas) também  */
	public void pesquisarFaturas() {
		
		if(model.limpaParametros) {
			model.limpaParametros();
		}
		else {
			model.limpaParametros = true;
		}
		
		if(!faturasBaixadas.equals("nao_sim")) {
			model.parametros.put("baixada", faturasBaixadas.equals("sim"));
		}
		
		model.parametros.put("dataInicio", dataInicio);
		model.parametros.put("dataFim", dataFim);
		
		if (tipoFatura.equalsIgnoreCase("ordem")) {
			model.parametros.put("ordem", tipoFatura);
		}
		if(tipoFatura.equalsIgnoreCase("venda")) {
			model.parametros.put("venda", tipoFatura);
		}
		
		if (tipoPesquisa.equals("numero") && idOrdemOuSaida != null) {
			model.parametros.put("id", idOrdemOuSaida);
		} 
		else if(tipoPesquisa.equals("numero_cel") && tipoFatura.equalsIgnoreCase("ordem")) {
			model.parametros.put("imei", ordemDeServico.getImei());
		} 
		else if ((tipoPesquisa.equals("cpf") || tipoPesquisa.equals("cnpj")) && cliente.getCpfCnpj() != null) {
			model.parametros.put("cpfCnpj", cliente.getCpfCnpj());
		} else if (tipoPesquisa.equals("data")) {
			model.parametros.put("dataInicio", dataInicio);
			model.parametros.put("dataFim", dataFim);
		} else if(tipoPesquisa.equals("nome") && cliente.getNome() != null) {
			model.parametros.put("nomeCliente", ServicoGenerico.montaPesquisaInteligente(cliente.getNome(), true));
		}
	}
	
	public void gerarRecibo() {
		try {
			CurrencyWriter cw = new CurrencyWriter();
	        String filename = "Recibo.pdf";
	        String jasperPath = "";	        
	        jasperPath = "/resources/relatorios/recibo.jasper";
	        recibos = new ArrayList<Recibo>();
	        recibos.add(new Recibo(detalhesFaturaSelecionado.getId(), 
	        		detalhesFaturaSelecionado.getOrdemDeServico() == null ? detalhesFaturaSelecionado.getSaidaDeProdutos().getCliente().getNome() : detalhesFaturaSelecionado.getOrdemDeServico().getCliente().getNome(), 
	        				detalhesFaturaSelecionado.getValorParcela(), 
	        				cw.write(detalhesFaturaSelecionado.getValorParcela()), 
	        				detalhesFaturaSelecionado.getOrdemDeServico() == null ? detalhesFaturaSelecionado.getDescricao() + " da Venda N º " + detalhesFaturaSelecionado.getSaidaDeProdutos().getId(): detalhesFaturaSelecionado.getDescricao() + " da Ordem de serviço N º " + detalhesFaturaSelecionado.getOrdemDeServico().getId()));
	        
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

	        GeradorRelatorio.PDF(parametros, jasperPath, recibos, filename);
			       
			
		} catch (JRException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void imprimirReciboSelecionado(DetalhesFatura d) {
		detalhesFaturaSelecionado = d;
		gerarRecibo();
	}
	
	//salva situação no banco
	public void salvaSituacao() {
		//detalhesFaturaSelecionado.setBaixada(false);
		//this.detalhesFaturas.set(guardaIndex, detalhesFaturaSelecionado);
		if(detalhesFaturaDAO.salvarUnique(detalhesFaturaSelecionado) != null) {
			if(detalhesFaturaSelecionado.isBaixada()) {
				FacesUtil.addInfoMessage("Baixa efetuada com sucesso!", "");
			}else {
				FacesUtil.addInfoMessage("Cancelamento efetuado com sucesso!", "");
			}
			model.limpaParametros = false;			
		}			
	}
	
	public void darBaixa() {
		detalhesFaturaSelecionado.setBaixada(true);
		salvaSituacao();		
	}
	
	public void cancelaBaixa() {
		detalhesFaturaSelecionado.setBaixada(false);
		salvaSituacao();
	}

	public void prepara(DetalhesFatura d) {
		//detalhesFaturaSelecionado = new DetalhesFatura();
		detalhesFaturaSelecionado = d;
	}

	public DetalhesFatura getDetalhesFatura() {
		return detalhesFatura;
	}

	public void setDetalhesFatura(DetalhesFatura detalhesFatura) {
		this.detalhesFatura = detalhesFatura;
	}

	public String getTipoFatura() {
		return tipoFatura;
	}

	public void setTipoFatura(String tipoFatura) {
		this.tipoFatura = tipoFatura;
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public String getFaturasBaixadas() {
		return faturasBaixadas;
	}

	public void setFaturasBaixadas(String faturasBaixadas) {
		this.faturasBaixadas = faturasBaixadas;
	}

	public List<SaidaDeProdutos> getSaidaDeProdutos() {
		return saidaDeProdutos;
	}

	public void setSaidaDeProdutos(List<SaidaDeProdutos> saidaDeProdutos) {
		this.saidaDeProdutos = saidaDeProdutos;
	}

	public OrdemDeServico getOrdemDeServico() {
		return ordemDeServico;
	}

	public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
		this.ordemDeServico = ordemDeServico;
	}

	public SaidaDeProdutos getSaidaDeProduto() {
		return saidaDeProduto;
	}

	public void setSaidaDeProduto(SaidaDeProdutos saidaDeProduto) {
		this.saidaDeProduto = saidaDeProduto;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	public DetalhesFatura getDetalhesFaturaSelecionado() {
		return detalhesFaturaSelecionado;
	}

	public void setDetalhesFaturaSelecionado(DetalhesFatura detalhesFaturaSelecionado) {
		this.detalhesFaturaSelecionado = detalhesFaturaSelecionado;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Long getIdOrdemOuSaida() {
		return idOrdemOuSaida;
	}

	public void setIdOrdemOuSaida(Long idOrdemOuSaida) {
		this.idOrdemOuSaida = idOrdemOuSaida;
	}

	public DetalheFaturaLazyDataModel getModel() {
		return model;
	}

	public void setModel(DetalheFaturaLazyDataModel model) {
		this.model = model;
	}

}
