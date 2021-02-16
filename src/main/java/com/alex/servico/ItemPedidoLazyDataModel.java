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

import com.alex.modelo.ItemPedido;

public class ItemPedidoLazyDataModel extends LazyDataModel<ItemPedido> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<ItemPedido> objOptional = null;

	private Long idObj;

	private List<ItemPedido> lista = new ArrayList<ItemPedido>();
	
	private List<ItemPedido> listaAux = new ArrayList<ItemPedido>();

	@Inject
	private ItemPedido itemEntrada;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	private int contador = 0;
	
	private int quantLinhas;

	@SuppressWarnings("unchecked")
	@Override
	public List<ItemPedido> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {
			
			if (chaves.contains("listaDeItens") ) {
				listaAux = (List<ItemPedido>) parametros.get("listaDeItens");
				quantLinhas = listaAux.size();
				first = quantLinhas == 0 || first == quantLinhas ? 0 : first;
				contador = 0;
				lista.clear();
				for (ItemPedido itemProduto : listaAux) {
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
	public ItemPedido getRowData(String rowKey) {
		lista = (List<ItemPedido>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				itemEntrada = objOptional.get();
			}
		}

		return objOptional.orElse(new ItemPedido());

	}

	@Override
	public Object getRowKey(ItemPedido obj) {
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

	public ItemPedido getItemEntrada() {
		return itemEntrada;
	}

	public void setItemEntrada(ItemPedido itemEntrada) {
		this.itemEntrada = itemEntrada;
	}

}
