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

import com.alex.DAO.ClienteDAO;
import com.alex.modelo.Cliente;
import com.alex.modelo.Telefone;
import com.alex.modelo.enums.TipoTelefone;
import com.alex.modelo.validacao.Validacoes;
import com.alex.util.FacesUtil;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class ClienteCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;

	@Inject
	private Cliente cliente;
	
	@Inject
	private ClienteDAO clienteDAO;

	@Inject
	private Telefone telefone;

	@Inject
	private Telefone telefoneAlterar;

	private Long idCliente;

	private String pessoa = "Física";

	private int guardaIndexItem = -1;

	private String txtBotaoInserir = "Adicionar";

	RequestContext request = RequestContext.getCurrentInstance();

	private List<Telefone> telefones = new ArrayList<Telefone>();

	private List<TipoTelefone> listaEnumTipoTelefone = Arrays.asList(TipoTelefone.values());

	public void inicializar() {
		
		if (idCliente != null) {
			cliente = clienteDAO.porID(idCliente);
			cliente = cliente == null ? new Cliente() : cliente;
			if(cliente.getId() != null) {
				filtros.put("parametro01", idCliente);
				filtros.put("entidadeId", "cliente");
				telefones.addAll(cliente.getTelefones());
				if(cliente.getCpfCnpj().length() > 14) {
					pessoa = "Jurídica";
				}
			}
			idCliente = null;
		}
	}
	
	@Override
	public String getTitulo() {
		return "Cadastro de cliente";
	}

	@Override
	public String getLinkBreadCrumb() {
		if(filtros.get("parametro01") != null) {
			return "/pages/cliente/cadastro-cliente.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/cliente/cadastro-cliente.jsf";
	}

	@Override
	public String caminhoCancelar() {
		return "/pages/cliente/lista-cliente?faces-redirect=true";
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
		txtBotaoInserir = "Adicionar";
		guardaIndexItem = -1;
	}

	// Nao retorna string, pois nao tera necessidade de voltar para pagina com a
	// lista de orcamentos
	public void salvar() {
			if (telefones != null && telefones.size() > 0) {
				cliente.setTelefones(telefones);
				clienteDAO.salvar(cliente);

				if (clienteDAO.isControle()) {
					cliente = new Cliente();
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
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/cliente/cadastro-cliente.jsf?id=" + idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}

	public void excluir() {
		clienteDAO.excluir(cliente.getId());
		if (clienteDAO.isControle()) {
			cliente = new Cliente();
		}
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Long getIdCliente() {
		return idCliente;
	}

	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}

	public String getPessoa() {
		return pessoa;
	}

	public void setPessoa(String pessoa) {
		this.pessoa = pessoa;
	}

	public Telefone getTelefone() {
		return telefone;
	}

	public void setTelefone(Telefone telefone) {
		this.telefone = telefone;
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
