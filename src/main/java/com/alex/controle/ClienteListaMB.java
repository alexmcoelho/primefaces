package com.alex.controle;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;

import com.alex.modelo.Cliente;
import com.alex.servico.ClienteLazyDataModel;
import com.alex.servico.ServicoGenerico;

@Named
@ViewScoped
public class ClienteListaMB extends ComplementoMB implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tipoPesquisa;
	
	@Inject
	private ClienteLazyDataModel model;

	@PostConstruct
	public void init() {
		tipoPesquisa = "nome";
	}

	@Override
	public String getTitulo() {
		return "Pesquisa cliente";
	}

	@Override
	public String getLinkBreadCrumb() {
		return "/pages/cliente/lista-cliente.jsf";
	}
	
	public void preparaExclusao(Cliente c) {
		// pode ser que este objeto entre em conflito com outra função, caso isso
		// aconteca criar um metodo
		// que instancie este obj novamente
		model.setCliente(c);
	}

	public void excluir() {
		model.getObjDAO().excluir(model.getCliente().getId());
		if (model.getObjDAO().isControle()) {
			model.limpaParametros = false;
			pesquisar();
		}
	}
	
	public void pesquisar() {
		
		if(model.limpaParametros) {
			model.limpaParametros();
		}
		else {
			model.limpaParametros = true;
		}
		
		if (tipoPesquisa.equals("todos")) {
			model.parametros.put("todos", tipoPesquisa);
		} else if (tipoPesquisa.equals("codigo")) {
			model.parametros.put("id", model.getCliente().getId());
		} else if (tipoPesquisa.equals("nome")) {
			model.parametros.put("nome", ServicoGenerico.montaPesquisaInteligente(model.getCliente().getNome(), true));
		}
		
		/*model = new LazyDataModel<Cliente>() {

			private static final long serialVersionUID = 1L;

			@Override
			public List<Cliente> load(int first, int pageSize, String sortField, SortOrder sortOrder,
					Map<String, Object> filters) {
				if (tipoPesquisa.equals("todos")) {
					clientes = clienteDAO.listAll(first, pageSize);
				} else if (tipoPesquisa.equals("codigo")) {
					filters.put("id", cliente.getId());
					clienteDAO.setFiltros(filters);
					clientes = clienteDAO.porId(first, pageSize);
				} else if (tipoPesquisa.equals("nome")) {
					filters.put("nome", cliente.getNome().toLowerCase() + "%"); //como tem a validaçao no front-end não precisa validar a String antes de chamar o método toLowerCase
					clienteDAO.setFiltros(filters);
					clientes = clienteDAO.porNomeSemelhante(first, pageSize);
				}

				setRowCount(clienteDAO.getQuantLinhas());

				return clientes;
			}

		};*/
		
	}

	public String getTipoPesquisa() {
		return tipoPesquisa;
	}

	public void setTipoPesquisa(String tipoPesquisa) {
		this.tipoPesquisa = tipoPesquisa;
	}

	public LazyDataModel<Cliente> getModel() {
		return model;
	}

}
