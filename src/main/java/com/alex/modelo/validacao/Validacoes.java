package com.alex.modelo.validacao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.alex.modelo.ItemEntrada;
import com.alex.modelo.ItemPedido;
import com.alex.modelo.ItemProdSaida;
import com.alex.modelo.ItemProduto;
import com.alex.modelo.ItemServico;
import com.alex.modelo.Produto;
import com.alex.modelo.Servico;
import com.alex.modelo.Telefone;

public class Validacoes {
	
	// verifica se o telefone já foi informado para o cliente sendo cadastrado ou
	// alterado
	public static boolean verificaTelefone(Telefone t, int guardaIndexItemTelefone, List<Telefone> telefones) {
		List<Telefone> lista = new ArrayList<>();

		if (guardaIndexItemTelefone == -1) {
			lista = telefones.stream()
					.filter(line -> comparaNum(t.getNumero(), line.getNumero()) && t.getDdd().equals(line.getDdd()))
					.collect(Collectors.toList());
		} else {
			List<Telefone> listaAux = new ArrayList<>();
			listaAux.addAll(telefones);
			listaAux.remove(guardaIndexItemTelefone);
			lista = listaAux.stream()
					.filter(line -> comparaNum(t.getNumero(), line.getNumero()) && t.getDdd().equals(line.getDdd()))
					.collect(Collectors.toList());
		}
		if (lista != null && lista.size() > 0) {
			return true;
		}
		return false;
	}

	// compara numeros e deixa passar casos como 98904-5873 -> 8904-5873
	public static boolean comparaNum(String numVeio, String numBase) {
		if (numVeio.length() == 10 && numBase.length() == 9) {
			return numVeio.substring(1, numVeio.length()).equals(numBase);
		} else if (numVeio.length() == 9 && numBase.length() == 10) {
			return numBase.substring(1, numBase.length()).equals(numVeio);
		}
		return numVeio.equals(numBase);
	}
	
	// verifica se o produto já foi informado para a lista de ItemProduto
	// alterado
	public static boolean verificaProduto(Produto p, int guardaIndexItem, List<ItemProduto> itemProdutos) {
		List<ItemProduto> lista = new ArrayList<>();

		if (guardaIndexItem == -1) {
			lista = itemProdutos.stream()
					.filter(line -> p.getId().equals(line.getProduto().getId()))
					.collect(Collectors.toList());
		} else {
			List<ItemProduto> listaAux = new ArrayList<>();
			listaAux.addAll(itemProdutos);
			listaAux.remove(guardaIndexItem);
			lista = listaAux.stream()
					.filter(line -> p.getId().equals(line.getProduto().getId()))
					.collect(Collectors.toList());
		}
		if (lista != null && lista.size() > 0) {
			return true;
		}
		return false;
	}
	
	// verifica se o produto já foi informado para a lista de ItemProdSaida
	// alterado
	public static boolean verificaProdutoSaida(Produto p, String imei, int guardaIndexItem, List<ItemProdSaida> itemProdSaidas) {
		List<ItemProdSaida> lista = new ArrayList<>();

		if (guardaIndexItem == -1) {
			lista = itemProdSaidas.stream()
					.filter(line -> (p.getId().equals(line.getProduto().getId()) && imei == null) || 
							(imei != null && imei.length() > 0 && !imei.equals("000000-00-000000-0") && imei.equals(line.getImei())))
					.collect(Collectors.toList());
		} else {
			List<ItemProdSaida> listaAux = new ArrayList<>();
			listaAux.addAll(itemProdSaidas);
			listaAux.remove(guardaIndexItem);
			lista = listaAux.stream()
					.filter(line -> (p.getId().equals(line.getProduto().getId()) && imei == null) || 
							(imei != null && imei.length() > 0 && !imei.equals("000000-00-000000-0") && imei.equals(line.getImei())))
					.collect(Collectors.toList());
		}
		if (lista != null && lista.size() > 0) {
			return true;
		}
		return false;
	}
	
	// verifica se o produto já foi informado para a lista de ItemEntrada
	// alterado
	public static boolean verificaProdutoEntrada(Produto produto, String imei, int guardaIndexItem, List<ItemEntrada> itemEntradas) {
		List<ItemEntrada> lista = new ArrayList<>();

		if (guardaIndexItem == -1) {
			lista = itemEntradas.stream()
					.filter(line -> (produto.getId().equals(line.getProduto().getId()) && imei == null) || 
							(imei != null && imei.length() > 0 && !imei.equals("000000-00-000000-0") && imei.equals(line.getImei())))
					.collect(Collectors.toList());
		} else {
			List<ItemEntrada> listaAux = new ArrayList<>();
			listaAux.addAll(itemEntradas);
			listaAux.remove(guardaIndexItem);
			lista = listaAux.stream()
					.filter(line -> (produto.getId().equals(line.getProduto().getId()) && imei == null) || 
							(imei != null && imei.length() > 0 && !imei.equals("000000-00-000000-0") && imei.equals(line.getImei())))
					.collect(Collectors.toList());
		}
		if (lista != null && lista.size() > 0) {
			return true;
		}
		return false;
	}
	
	// verifica se o produto já foi informado para a lista de ItemPedido
	// alterado
	public static boolean verificaProdutoPedido(Produto produto, int guardaIndexItem, List<ItemPedido> itemPedidos) {
		List<ItemPedido> lista = new ArrayList<>();

		if (guardaIndexItem == -1) {
			lista = itemPedidos.stream()
					.filter(line -> produto.getId().equals(line.getProduto().getId()))
					.collect(Collectors.toList());
		} else {
			List<ItemPedido> listaAux = new ArrayList<>();
			listaAux.addAll(itemPedidos);
			listaAux.remove(guardaIndexItem);
			lista = listaAux.stream()
					.filter(line -> produto.getId().equals(line.getProduto().getId()))
					.collect(Collectors.toList());
		}
		if (lista != null && lista.size() > 0) {
			return true;
		}
		return false;
	}
	
	// verifica se o produto já foi informado para a lista de ItemPedido
	// alterado
	public static boolean verificaServico(Servico servico, int guardaIndexItem, List<ItemServico> itemServicos) {
		List<ItemServico> lista = new ArrayList<>();

		if (guardaIndexItem == -1) {
			lista = itemServicos.stream()
					.filter(line -> servico.getId().equals(line.getServico().getId()))
					.collect(Collectors.toList());
		} else {
			List<ItemServico> listaAux = new ArrayList<>();
			listaAux.addAll(itemServicos);
			listaAux.remove(guardaIndexItem);
			lista = listaAux.stream()
					.filter(line -> servico.getId().equals(line.getServico().getId()))
					.collect(Collectors.toList());
		}
		if (lista != null && lista.size() > 0) {
			return true;
		}
		return false;
	}

}
