package com.alex.servico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.alex.DAO.EntradaDeProdutosDAO;
import com.alex.modelo.EntradaDeProdutos;
import com.alex.util.FacesUtil;

public class EntradaDeProdutosLazyDataModel extends LazyDataModel<EntradaDeProdutos> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<EntradaDeProdutos> objOptional = null;

	private Long idObj;

	private List<EntradaDeProdutos> lista = new ArrayList<EntradaDeProdutos>();

	@Inject
	private EntradaDeProdutosDAO objDAO;

	@Inject
	private EntradaDeProdutos entradaDeProduto;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	public boolean limpaParametros = true;
	
	private String descricaoProduto;

	@Override
	public List<EntradaDeProdutos> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {
			
			objDAO.setFiltros(parametros);
			objDAO.setPrimeiroReg(objDAO.getQuantLinhas() != 0 ? first : 0);
			objDAO.setQuantRegistros(pageSize);
			
			if (chaves.contains("todos")) {
				lista = objDAO.listAll();
			} 
			else if (chaves.contains("id")) {
				lista = objDAO.listaPorId();
			} 
			else if(chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.intervaloEntreDatas();
			}
			else if (chaves.contains("codigoNota")) {
				lista = objDAO.peloCodigoDaNota();
			}
			else if (chaves.contains("produto")) {
				lista = objDAO.peloProduto();
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
	public EntradaDeProdutos getRowData(String rowKey) {
		lista = (List<EntradaDeProdutos>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				entradaDeProduto = objOptional.get();
			}
		}

		return objOptional.orElse(new EntradaDeProdutos());
	}

	@Override
	public Object getRowKey(EntradaDeProdutos obj) {
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
	
	public void limpaParametros() {
		objDAO.setQuantLinhas(0);
		for (String chave : parametros.keySet()) {
			parametros.put(chave, null);
		}
	}

	public EntradaDeProdutos getEntradaDeProduto() {
		return entradaDeProduto;
	}

	public void setEntradaDeProduto(EntradaDeProdutos entradaDeProduto) {
		this.entradaDeProduto = entradaDeProduto;
	}

	public EntradaDeProdutosDAO getObjDAO() {
		return objDAO;
	}

	public void setObjDAO(EntradaDeProdutosDAO objDAO) {
		this.objDAO = objDAO;
	}

	public String getDescricaoProduto() {
		return descricaoProduto;
	}

	public void setDescricaoProduto(String descricaoProduto) {
		this.descricaoProduto = descricaoProduto;
	}

}
