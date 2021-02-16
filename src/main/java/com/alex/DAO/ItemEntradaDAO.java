package com.alex.DAO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.alex.exception.NegocioException;
import com.alex.modelo.ItemEntrada;
import com.alex.modelo.ItemProdSaida;
import com.alex.modelo.enums.TipoOperacaoCRUD;

public class ItemEntradaDAO extends ControleDaoGenerico<ItemEntrada, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public ItemEntradaDAO() {
		super(ItemEntrada.class);
	}

	List<ItemEntrada> lista = new ArrayList<>();

	/*Salva sem iniciar transação, pois aonde esse método foi chamado já foi iniciado a transação.
	E também não tem mensagem caso tenha sucesso ao salvar registro, só irá aparecer mensagem de sucesso no método que chama
	o método abaixo*/
	public void salvar(ItemEntrada itemProduto) {
		try {
			// tirando do estoque
			itemProduto = manager.merge(itemProduto);
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.INSERT_UPDATE);
		}

	}

	public List<ItemEntrada> listAll() {
		return manager.createQuery("FROM ItemEntrada", ItemEntrada.class).getResultList();
	}

	// lista filtrado por entrada
	public List<ItemEntrada> listaFiltradoPorEntrada(Long id) {
		return manager.createQuery("FROM ItemEntrada WHERE entradaDeProdutos.id = ?1", ItemEntrada.class)
				.setParameter(1, id).getResultList();

	}
	
	public ItemEntrada retornaValorDaUltimaEntradaProd(Long idProduto) {
		List<ItemEntrada> lista = manager.createQuery("FROM ItemEntrada WHERE produto.id = ?1 ORDER BY id DESC", ItemEntrada.class)
				.setParameter(1, idProduto).getResultList();
		if(!lista.isEmpty() && lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	public BigDecimal getValorTotal(Long idSaida) {
		lista = listaFiltradoPorEntrada(idSaida);
		BigDecimal valor = new BigDecimal("0.00");
		for (ItemEntrada itemEntrada : lista) {
			valor = valor.add(itemEntrada.getSubTotal(), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);
		}
		return valor;
	}
	
	//filtra por produto
	public List<ItemEntrada> porIdProduto(Long id) {
		return manager.createQuery("FROM ItemEntrada WHERE produto.id = :id", ItemEntrada.class)
			.setParameter("id", id)
			.getResultList();			
	}

	public int getQuantEntrada(Long idProduto) {
		Query q = manager.createQuery(
				"SELECT COALESCE(sum(i.quant), 0) as quantidade FROM " + 
						"ItemEntrada i WHERE produto_id = " + idProduto);
		Number result = (Number) q.getSingleResult();
		
		return Integer.parseInt(result.toString());
	}
	
	//verifica se já existe entrada com o imei informado, tem que pegar todos os itens pois um celular pode ser revendido
	public boolean verificaImeiJaEstaCadastrado(Long idEntrada, String imei) {
		List<ItemProdSaida> listaItensSaidas;
		
		listaItensSaidas = manager.createQuery("FROM ItemProdSaida "
				+ "WHERE imei = :imei ", ItemProdSaida.class)
			.setParameter("imei", imei)
			.getResultList();
		
		List<ItemEntrada> listaEntrada;
		
		if(idEntrada != null) {
			listaEntrada = manager.createQuery("FROM ItemEntrada "
					+ "WHERE entradaDeProdutos.id <> :idEntrada "
					+ "AND imei = :imei ", ItemEntrada.class)
					.setParameter("idEntrada", idEntrada)
					.setParameter("imei", imei)
					.getResultList();			
		}
		else {
			listaEntrada = manager.createQuery("FROM ItemEntrada "
					+ "WHERE imei = :imei ", ItemEntrada.class)
					.setParameter("imei", imei)
					.getResultList();	
		}
		int listaEntradaSize = listaEntrada != null ? listaEntrada.size() : 0;
		//se tiver registro retorna true
		return listaItensSaidas != null && listaItensSaidas.size() != listaEntradaSize;
	}

	@Override
	public ItemEntrada getEntidade() {
		return this.entidade;
	}
	
	public void setEntidade(ItemEntrada i) {
		this.entidade = i;
	}

	public List<ItemEntrada> getLista() {
		return lista;
	}

	public void setLista(List<ItemEntrada> lista) {
		this.lista = lista;
	}

}
