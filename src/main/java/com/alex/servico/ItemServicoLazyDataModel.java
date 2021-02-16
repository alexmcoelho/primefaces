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

import com.alex.modelo.ItemServico;

public class ItemServicoLazyDataModel extends LazyDataModel<ItemServico> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<ItemServico> objOptional = null;

	private Long idObj;

	private List<ItemServico> lista = new ArrayList<ItemServico>();
	
	private List<ItemServico> listaAux = new ArrayList<ItemServico>();

	@Inject
	private ItemServico itemServico;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	private int contador = 0;
	
	private int quantLinhas;

	@SuppressWarnings("unchecked")
	@Override
	public List<ItemServico> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {
			
			if (chaves.contains("listaDeItens") ) {
				listaAux = (List<ItemServico>) parametros.get("listaDeItens");
				quantLinhas = listaAux.size();
				first = quantLinhas == 0 || first == quantLinhas ? 0 : first;
				contador = 0;
				lista.clear();
				for (ItemServico o : listaAux) {
					if(contador >= first && contador <= quantLinhas) {
						lista.add(o);
					}
					contador++;
				}
			}

			setRowCount(quantLinhas);

		}
		else {
			setRowCount(0);
			lista.clear();
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ItemServico getRowData(String rowKey) {
		lista = (List<ItemServico>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				itemServico = objOptional.get();
			}
		}

		return objOptional.orElse(new ItemServico());

	}

	@Override
	public Object getRowKey(ItemServico obj) {
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
		quantLinhas = 0;
		for (String chave : parametros.keySet()) {
			parametros.put(chave, null);
		}
	}

	public ItemServico getItemServico() {
		return itemServico;
	}

	public void setItemServico(ItemServico itemServico) {
		this.itemServico = itemServico;
	}

}
