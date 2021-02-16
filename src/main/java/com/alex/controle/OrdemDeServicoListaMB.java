package com.alex.controle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.modelo.Cliente;
import com.alex.modelo.OrdemDeServico;
import com.alex.modelo.enums.Estado;
import com.alex.servico.OrdemDeServicoLazyDataModel;
import com.alex.servico.ServicoGenerico;
import com.alex.util.ManipData;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class OrdemDeServicoListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tipoPesquisa;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	private List<Estado> listaEnumEstado = new ArrayList<Estado>();
	
	@Inject
	private OrdemDeServicoLazyDataModel model;
	
	public List<Estado> getListaEnumEstado() {
		return listaEnumEstado;
	}

	public void setListaEnumEstado(List<Estado> listaEnumEstado) {
		this.listaEnumEstado = listaEnumEstado;
	}

	@PostConstruct
	public void inicializar() {
		// tarefas = tarefaDAO.listAll();
		Arrays.asList(Estado.values()).forEach(o -> {
			if (!o.equals(Estado.TODOS)) {
				listaEnumEstado.add(o);
			}
		});
		listaEnumEstado.add(Estado.TODOS); //adiciona por último
		
		tipoPesquisa = "numero_os";
		model.getOrdemDeServico().setEstado(Estado.TODOS);
		if(model.getOrdemDeServico().getCliente() == null) {
			model.getOrdemDeServico().setCliente(new Cliente());
		}
	}
	
	@Override
	public String getTitulo() {
		return "Pesquisa";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/ordemservico/lista-os.jsf";
	}
	//sobrescrevendo método da classe pai
	public String getTituloBreadCrumbString() {
		return "Pesquisa OS";
	}
	
	public void preparaExclusao(OrdemDeServico o) {
		//pode ser que este objeto entre em conflito com outra função, caso isso aconteca criar um metodo
		//que instancie este obj novamente
		model.setOrdemDeServico(o);
	}

	public void excluirOrdem() {
		model.getObjDAO().excluir(model.getOrdemDeServico().getId());
		if (model.getObjDAO().isControle()) {
			model.limpaParametros = false;
			pesquisarOrdem();
		}
	}

	public void pesquisarOrdem() {
		
		if(model.limpaParametros) {
			model.limpaParametros();
		}
		else {
			model.limpaParametros = true;
		}

		if (tipoPesquisa.equals("todos")) {
			if(model.getOrdemDeServico().getEstado().equals(Estado.TODOS)) {
				model.parametros.put("todos", tipoPesquisa);
			}
			else {
				model.parametros.put("estado", model.getOrdemDeServico().getEstado().getCod());
			}
		}
		if (tipoPesquisa.equals("numero_os") && model.getOrdemDeServico().getId() != null) {
			model.parametros.put("estado", !model.getOrdemDeServico().getEstado().equals(Estado.TODOS) ? model.getOrdemDeServico().getEstado().getCod() : null);
			model.parametros.put("id", model.getOrdemDeServico().getId());
		} else if(tipoPesquisa.equals("numero_cel") && model.getOrdemDeServico().getImei() != null) {
			model.parametros.put("estado", !model.getOrdemDeServico().getEstado().equals(Estado.TODOS) ? model.getOrdemDeServico().getEstado().getCod() : null);
			model.parametros.put("imei", model.getOrdemDeServico().getImei());
		}
		else if ((tipoPesquisa.equals("cpf") && model.getOrdemDeServico().getCliente().getCpfCnpj() != null) || 
				(tipoPesquisa.equals("cnpj") && model.getOrdemDeServico().getCliente().getCpfCnpj() != null)) {
			model.parametros.put("estado", !model.getOrdemDeServico().getEstado().equals(Estado.TODOS) ? model.getOrdemDeServico().getEstado().getCod() : null);
			model.parametros.put("cpfCnpj", model.getOrdemDeServico().getCliente().getCpfCnpj());
		} else if (tipoPesquisa.equals("data_entrada")) {
			model.parametros.put("estado", !model.getOrdemDeServico().getEstado().equals(Estado.TODOS) ? model.getOrdemDeServico().getEstado().getCod() : null);
			model.parametros.put("entrada", "entrada");
			model.parametros.put("dataInicio", dataInicio);
			model.parametros.put("dataFim", dataFim);
		} else if (tipoPesquisa.equals("data_conclusao")) {
			model.parametros.put("estado", !model.getOrdemDeServico().getEstado().equals(Estado.TODOS) ? model.getOrdemDeServico().getEstado().getCod() : null);
			model.parametros.put("conclusao", "conclusao");
			model.parametros.put("dataInicio", dataInicio);
			model.parametros.put("dataFim", dataFim);
		} else if (tipoPesquisa.equals("nome")) {
			model.parametros.put("estado", !model.getOrdemDeServico().getEstado().equals(Estado.TODOS) ? model.getOrdemDeServico().getEstado().getCod() : null);
			model.parametros.put("nomeCliente", ServicoGenerico.montaPesquisaInteligente(model.getOrdemDeServico().getCliente().getNome(), true));
		} else if (tipoPesquisa.equals("nome_responsavel")) {
			model.parametros.put("estado", !model.getOrdemDeServico().getEstado().equals(Estado.TODOS) ? model.getOrdemDeServico().getEstado().getCod() : null);
			model.parametros.put("nomeResponsavel", ServicoGenerico.montaPesquisaInteligente(model.getOrdemDeServico().getUsuario().getNome(), true));
		}
	}

	public void selecionarOrdemDeServico(OrdemDeServico o) {
		try {
			tipoPesquisa = "numero_os";
			model.getOrdemDeServico().setEstado(Estado.ORCAMENTO);
			FacesContext context = FacesContext.getCurrentInstance();
			context.getExternalContext().getSessionMap().put("ordemDeServico", o);
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/ordemservico/entrada-os.jsf");
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}
	
	//analisa se está na garantia ou não
	public String garantia(OrdemDeServico o) {
		return ManipData.garantiaOS(o);
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
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

	public OrdemDeServicoLazyDataModel getModel() {
		return model;
	}

	public void setModel(OrdemDeServicoLazyDataModel model) {
		this.model = model;
	}

}
