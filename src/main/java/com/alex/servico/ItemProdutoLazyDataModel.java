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

import com.alex.modelo.ItemProduto;

public class ItemProdutoLazyDataModel extends LazyDataModel<ItemProduto> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<ItemProduto> objOptional = null;

	private Long idObj;

	private List<ItemProduto> lista = new ArrayList<ItemProduto>();
	
	private List<ItemProduto> listaAux = new ArrayList<ItemProduto>();

	@Inject
	private ItemProduto itemProduto;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	private int contador = 0;
	
	private int quantLinhas;

	@SuppressWarnings("unchecked")
	@Override
	public List<ItemProduto> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {
			
			if (chaves.contains("listaDeItens") ) {
				listaAux = (List<ItemProduto>) parametros.get("listaDeItens");
				quantLinhas = listaAux.size();
				first = quantLinhas == 0 || first == quantLinhas ? 0 : first;
				contador = 0;
				lista.clear();
				for (ItemProduto itemProduto : listaAux) {
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
	public ItemProduto getRowData(String rowKey) {
		lista = (List<ItemProduto>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				itemProduto = objOptional.get();
			}
		}

		return objOptional.orElse(new ItemProduto());

	}

	@Override
	public Object getRowKey(ItemProduto obj) {
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

	public ItemProduto getItemProduto() {
		return itemProduto;
	}

	public void setItemProduto(ItemProduto itemProduto) {
		this.itemProduto = itemProduto;
	}

}
