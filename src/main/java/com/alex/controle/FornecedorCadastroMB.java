package com.alex.controle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.alex.DAO.FornecedorDAO;
import com.alex.modelo.Fornecedor;
import com.alex.modelo.Telefone;
import com.alex.modelo.enums.TipoTelefone;
import com.alex.modelo.validacao.Validacoes;
import com.alex.util.FacesUtil;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class FornecedorCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;

	@Inject
	private Fornecedor fornecedor;

	@Inject
	private FornecedorDAO fornecedorDAO;

	@Inject
	private Telefone telefone;

	@Inject
	private Telefone telefoneAlterar;

	private Long idFornecedor;

	private int guardaIndexItem = -1;

	private String txtBotaoInserir = "Adicionar";

	RequestContext request = RequestContext.getCurrentInstance();

	private List<Telefone> telefones = new ArrayList<Telefone>();

	private List<TipoTelefone> listaEnumTipoTelefone = Arrays.asList(TipoTelefone.values());

	public void inicializar() {

		if (idFornecedor != null) {
			fornecedor = fornecedorDAO.porID(idFornecedor);
			fornecedor = fornecedor == null ? new Fornecedor() : fornecedor;
			if(fornecedor.getId() != null) {
				filtros.put("parametro01", idFornecedor);
				filtros.put("entidadeId", "fornecedor");
				fornecedor = fornecedor == null ? fornecedor = new Fornecedor() : fornecedor;
				telefones.addAll(fornecedor.getTelefones());
			}
			idFornecedor = null;
		}
	}
	
	@Override
	public String getTitulo() {
		return "Cadastro de fornecedor";
	}

	@Override
	public String getLinkBreadCrumb() {
		if(filtros.get("parametro01") != null) {
			return "/pages/fornecedor/cadastro-fornecedor.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/fornecedor/cadastro-fornecedor.jsf";
	}

	@Override
	public String caminhoCancelar() {
		return "/pages/fornecedor/lista-fornecedor?faces-redirect=true";
	}

	public void salvarNaListaDeItens() {

		if (!Validacoes.verificaTelefone(telefone, guardaIndexItem, telefones)) {
			// verifica se o número veio para o back do tipo 88888-888
			if (telefone.getNumero() != null && telefone.getNumero().length() == 9) {
				String[] telefoneDividioPorHifen = telefone.getNumero().split("-");
				if (telefoneDividioPorHifen.length == 2 && telefoneDividioPorHifen[1].length() == 3)
					telefone.setNumero(telefoneDividioPorHifen[0].substring(0, 4) + "-"
							+ telefoneDividioPorHifen[0].substring(4, 5) + telefoneDividioPorHifen[1]);
			}

			if (guardaIndexItem == -1) {
				this.telefones.add(telefone);
				request.addCallbackParam("sucessoAdd", true);
			} else {
				this.telefones.set(guardaIndexItem, telefone);
				guardaIndexItem = -1;
				txtBotaoInserir = "Adicionar";
			}
			telefone = new Telefone();
			RequestContext.getCurrentInstance().execute("PF('addTelefone').hide();");
		} else {
			request.addCallbackParam("sucessoAdd", false);
			FacesUtil.addWarnMessage("Esse telefone já foi informado para este cliente!", "");
		}

	}

	public void alterarNaListaDeItens(Telefone t, List<Telefone> lista) {
		telefone = new Telefone();
		telefone.setTipoTelefone(t.getTipoTelefone());
		telefone.setDdd(t.getDdd());
		telefone.setNumero(t.getNumero());
		telefone.setRamal(t.getRamal());
		// pega indice do item na lista
		guardaIndexItem = telefones.indexOf(t);
		// muda texto do botão
		txtBotaoInserir = "Alterar";
	}

	public void removerNaListaDeItens(Telefone i) {
		// pega indice do item na lista
		telefoneAlterar = new Telefone();
		telefoneAlterar.setNumero(i.getNumero());
		int index = telefones.indexOf(telefoneAlterar);
		this.telefones.remove(index);
	}
	
	public void preparaInclusao() {
		telefone = new Telefone();
		telefone.setTipoTelefone(TipoTelefone.CELULAR);
		telefone.setDdd("047");
		guardaIndexItem= -1;
		txtBotaoInserir = "Adicionar";
	}

	// Nao retorna string, pois nao tera necessidade de voltar para pagina com a
	// lista de orcamentos
	public void salvar() {
		if (telefones != null && telefones.size() > 0) {
			fornecedor.setTelefones(telefones);
			fornecedorDAO.salvar(fornecedor);

			if (fornecedorDAO.isControle()) {
				fornecedor = new Fornecedor();
				telefones.clear();
			}
		} else {
			FacesUtil.addWarnMessage("Deve ser informado ao menos um telefone para contato!", "");
		}
	}

	public void selecionarRegistro(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/fornecedor/cadastro-fornecedor.jsf?id=" + idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}

	public void excluir() {
		fornecedorDAO.excluir(fornecedor.getId());
		if (fornecedorDAO.isControle()) {
			fornecedor = new Fornecedor();
		}
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Telefone getTelefone() {
		return telefone;
	}

	public void setTelefone(Telefone telefone) {
		this.telefone = telefone;
	}

	public Long getIdFornecedor() {
		return idFornecedor;
	}

	public void setIdFornecedor(Long idFornecedor) {
		this.idFornecedor = idFornecedor;
	}

	public String getTxtBotaoInserir() {
		return txtBotaoInserir;
	}

	public void setTxtBotaoInserir(String txtBotaoInserir) {
		this.txtBotaoInserir = txtBotaoInserir;
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

}
