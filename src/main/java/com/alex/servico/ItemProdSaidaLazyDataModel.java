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

import com.alex.modelo.ItemProdSaida;

public class ItemProdSaidaLazyDataModel extends LazyDataModel<ItemProdSaida> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<ItemProdSaida> objOptional = null;

	private Long idObj;

	private List<ItemProdSaida> lista = new ArrayList<ItemProdSaida>();
	
	private List<ItemProdSaida> listaAux = new ArrayList<ItemProdSaida>();

	@Inject
	private ItemProdSaida itemProdSaida;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	private int contador = 0;
	
	private int quantLinhas;

	@SuppressWarnings("unchecked")
	@Override
	public List<ItemProdSaida> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {
			
			if (chaves.contains("listaDeItens") ) {
				listaAux = (List<ItemProdSaida>) parametros.get("listaDeItens");
				quantLinhas = listaAux.size();
				first = quantLinhas == 0 || first == quantLinhas ? 0 : first;
				contador = 0;
				lista.clear();
				for (ItemProdSaida itemProduto : listaAux) {
					if(contador >= first && contador <= quantLinhas) {
						lista.add(itemProduto);
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
	public ItemProdSaida getRowData(String rowKey) {
		lista = (List<ItemProdSaida>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				itemProdSaida = objOptional.get();
			}
		}

		return objOptional.orElse(new ItemProdSaida());

	}

	@Override
	public Object getRowKey(ItemProdSaida obj) {
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

	public ItemProdSaida getItemProdSaida() {
		return itemProdSaida;
	}

	public void setItemProdSaida(ItemProdSaida itemProdSaida) {
		this.itemProdSaida = itemProdSaida;
	}

}
