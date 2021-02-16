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
import com.alex.DAO.CategoriaDAO;
import com.alex.modelo.Categoria;
import com.alex.util.FacesUtil;

public class CategoriaLazyDataModel extends LazyDataModel<Categoria> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<Categoria> objOptional = null;

	private Long idObj;

	private List<Categoria> lista = new ArrayList<Categoria>();

	@Inject
	private CategoriaDAO objDAO;

	@Inject
	private Categoria categoria;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	public boolean limpaParametros = true;

	@Override
	public List<Categoria> load(int first, int pageSize, String sortField, SortOrder sortOrder,
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
			} else if (chaves.contains("descricao")) {
				lista = objDAO.porDescricaoSemelhante();
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
	public Categoria getRowData(String rowKey) {
		lista = (List<Categoria>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				categoria = objOptional.get();
			}
		}

		return objOptional.orElse(new Categoria());

	}

	@Override
	public Object getRowKey(Categoria obj) {
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

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public CategoriaDAO getObjDAO() {
		return objDAO;
	}

	public void setObjDAO(CategoriaDAO objDAO) {
		this.objDAO = objDAO;
	}

}
