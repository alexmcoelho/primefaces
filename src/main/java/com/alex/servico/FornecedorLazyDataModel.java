package com.alex.servico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.alex.DAO.FornecedorDAO;
import com.alex.modelo.Fornecedor;
import com.alex.util.FacesUtil;

public class FornecedorLazyDataModel extends LazyDataModel<Fornecedor> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<Fornecedor> objOptional = null;

	private Long idObj;

	private List<Fornecedor> lista = new ArrayList<Fornecedor>();

	@Inject
	private FornecedorDAO objDAO;

	@Inject
	private Fornecedor fornecedor;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	public boolean limpaParametros = true;

	@Override
	public List<Fornecedor> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();		

		if (chaves != null && chaves.size() > 0) {
			
			objDAO.setFiltros(parametros);
			objDAO.setPrimeiroReg(objDAO.getQuantLinhas() != 0 ? first : 0);
			objDAO.setQuantRegistros(pageSize);
			
			if (chaves.contains("todos")) {
				lista = objDAO.listAll();
			} else if (chaves.contains("id")) {
				lista = objDAO.porId();
			} else if (chaves.contains("nome")) {
				lista = objDAO.porNomeSemelhante();
			}
			
			if(objDAO.getQuantLinhas() <= 0) {
				FacesUtil.addWarnMessage(FacesUtil.mensagemPadraoRegistroNaoEncontrado(), "");
			}

			setRowCount(objDAO.getQuantLinhas());

		}
		else {
			setRowCount(0);
			lista.clear();
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Fornecedor getRowData(String rowKey) {
		lista = (List<Fornecedor>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				fornecedor = objOptional.get();
			}
		}

		return objOptional.orElse(new Fornecedor());

	}

	@Override
	public Object getRowKey(Fornecedor obj) {
		return obj != null ? obj.getId() : null;
	}
	
	public void aplicaChavesParametrosUtilizados() {
		if(chaves != null) {
			chaves.clear();
		}
		
		for (String chave : parametros.keySet()) {
			if(parametros.get(chave) != null) {
				chaves.add(chave);
			}
		}
	}

	public String retornoChaveParametroUtilizado() {
		for (String chave : parametros.keySet()) {
			if(parametros.get(chave) != null) {
				return chave;
			}
		}
		return null;
	}
	
	public void limpaParametros() {
		objDAO.setQuantLinhas(0);
		for (String chave : parametros.keySet()) {
			parametros.put(chave, null);
		}
	}
	
	public List<String> filtro(String query, int quantRegistros){
		int pos = query.indexOf("-");
		if(pos >= 0 && StringUtils.isNumericSpace(query.substring(0, pos-1)) && query.length() > (pos+1)) {
			return null;
		}
		List<String> listaString = new ArrayList<String>();
		objDAO.setQuantRegistros(quantRegistros);
		objDAO.limpaParametros();
		if(StringUtils.isNumericSpace(query)) {
			query = query.replace(" ", "");
			objDAO.adicionaFiltro("id", Long.parseLong(query));
			lista = objDAO.porId(); 
		}
		else {
			objDAO.adicionaFiltro("nome", ServicoGenerico.montaPesquisaInteligente(query, true));
			lista = objDAO.porNomeSemelhante(); 
		}
		
		lista.forEach(obj -> {
			listaString.add(obj.getCodDescricao());
		});
		return listaString;
    }
	
	public Fornecedor busca(String pesquisa) {
		idObj = ServicoGenerico.retornaId(pesquisa);
		
		if(lista != null && lista.size() > 0) {
			
			lista = lista.stream()                // convert list to stream
					.filter(line -> line.getId().equals(idObj))     
					.collect(Collectors.toList());
			if(lista != null && lista.size() > 0) {
				return lista.get(0);
			}
		}
		
		//se a pesquisa ser feita pela segunda vez e tiver no cache, a lista será vazia, por isso tem que preencher ela de volta
		lista = objDAO.porId(idObj, 1);
		if(lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		
		return null;
	}
	
	public void addLista() {
		lista.add(this.fornecedor);
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public FornecedorDAO getObjDAO() {
		return objDAO;
	}

	public void setObjDAO(FornecedorDAO objDAO) {
		this.objDAO = objDAO;
	}

}
