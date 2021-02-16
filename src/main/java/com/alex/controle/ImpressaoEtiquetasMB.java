package com.alex.controle;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.modelo.Produto;
import com.alex.modelo.SemInformacoes;
import com.alex.modelo.TipoUsuario;
import com.alex.modelo.Usuario;
import com.alex.relatorios.GeradorRelatorio;
import com.alex.servico.ProdutoLazyDataModel;
import com.alex.servico.ServicoGenerico;
import com.alex.util.OperacoesComBigDecimal;

import net.sf.jasperreports.engine.JRException;

@Named
@ViewScoped
public class ImpressaoEtiquetasMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProdutoLazyDataModel model;

	List<Produto> produtos = new ArrayList<>();

	List<SemInformacoes> semInformacoes = new ArrayList<>();

	private String tipoPesquisa;

	private String descricao;

	private Long id;
	
	private boolean temPermissao = false;
	
	private boolean defineValor;

	public boolean isTemPermissao() {
		return temPermissao;
	}

	public void setTemPermissao(boolean temPermissao) {
		this.temPermissao = temPermissao;
	}

	@PostConstruct
	public void init() {
		tipoPesquisa = "descricao";
		TipoUsuario t = new TipoUsuario();
		Usuario u = new Usuario();
		t.setId(1L);
		u = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
		
		if (u != null &&u.getTipoUsuarios().contains(t)) {
			temPermissao = true;
		}
	}

	@Override
	public String getTitulo() {
		return "Impressão de etiquetas";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/relatorios/impressao-etiquetas.jsf";
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

	public void selecionarProduto(Produto p) {
		model.setProduto(new Produto());
		model.getProduto().setId(p.getId());
		model.getProduto().setDescricao(p.getDescricao());
		model.getProduto().setValorPago(p.getValorPago());
		model.getProduto().setPercentualLucro(p.getPercentualLucro());
		model.getProduto().setValorSugerido(p.getValorSugerido());
		model.getProduto().setValorInformado(p.getValorInformado());
		model.getProduto().setCategoria(p.getCategoria());
		model.getProduto().setImei(p.getImei());
		if(p.getValorInformado() != null) {
			defineValor = true;
			model.getProduto().setValorSugerido(null);
		}
		else {
			defineValor = false;
		}
	}

	public void salvarAlteraracao() {
		if(defineValor) {
			model.getProduto().setPercentualLucro(null);
		}
		else {
			model.getProduto().setValorInformado(null);
		}
		model.getObjDAO().salvar(model.getProduto());
		if (model.getObjDAO().isControle()) {
			model.limpaParametros = false;
			pesquisar();
		}
	}

	//valor de venda
	public void atualizaValorSugerido() {
		model.getProduto().setValorSugerido(model.getProduto().getValorPago());
		//apenas para guardar o valorSugerido para depois ser usado no calculo abaixo
		model.getProduto().setLucro(model.getProduto().getValorSugerido());
		
		model.getProduto().setPercentualLucro(OperacoesComBigDecimal.divisao(model.getProduto().getPercentualLucro(),
				BigDecimal.TEN.multiply(BigDecimal.TEN)));
		model.getProduto().setValorSugerido(OperacoesComBigDecimal.soma(
				OperacoesComBigDecimal.multiplicacao(model.getProduto().getValorSugerido(), model.getProduto().getPercentualLucro()),
				model.getProduto().getLucro()));
		
		model.getProduto().setLucro(OperacoesComBigDecimal.subtracao(model.getProduto().getValorSugerido(), 
				model.getProduto().getValorPago()));
	}

	public void gerarRelatorio() {

		pegaValoresDaTela();

		// filtra
		if (tipoPesquisa.equals("todos")) {
			produtos = model.getObjDAO().listAll();
		} else if (tipoPesquisa.equals("codigo")) {
			produtos = model.getObjDAO().porId(id);
		} else if (tipoPesquisa.equals("descricao")) {
			produtos = model.getObjDAO().porDescricaoSemelhanteTrazendoListas(ServicoGenerico.montaPesquisaInteligente(descricao, true));
		} else if (tipoPesquisa.equals("categoria")) {
			produtos = model.getObjDAO().porDescricaoCategoriaSemelhanteTrazendoListas(
					ServicoGenerico.montaPesquisaInteligente(descricao, true));
		}

		produtos = model.calculaValorPagoVendaLucro(produtos);
		produtos = model.calculaQuant(produtos);

		try {
			String filename = "Etiquetas.pdf";
			String jasperPath = "";
			if (produtos.size() > 0) {
				jasperPath = "/resources/relatorios/etiquetas2.jasper";
				Map<String, Object> parametros = new HashMap<String, Object>();
				parametros.put("caminhoTemplateStyleBarcode", FacesContext.getCurrentInstance().getExternalContext()
						.getRealPath("/resources/relatorios/barcode_center.jrtx"));
				GeradorRelatorio.PDF(parametros, jasperPath, produtos, filename);
			} else {
				jasperPath = "/resources/relatorios/sem_informacoes.jasper";
				semInformacoes.add(new SemInformacoes("Impressão de etiquetas", "Quantidade de etiquetas",
						"Nenhum registro foi encontrado.", false));
				GeradorRelatorio.PDF(null, jasperPath, semInformacoes, filename);
			}
		} catch (JRException | IOException e) { // TODO Auto-generated catch block
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

	public void setModel(ProdutoLazyDataModel model) {
		this.model = model;
	}

	public boolean isDefineValor() {
		return defineValor;
	}

	public void setDefineValor(boolean defineValor) {
		this.defineValor = defineValor;
	}

}
