package com.alex.DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.alex.exception.NegocioException;
import com.alex.modelo.ItemEntrada;
import com.alex.modelo.ItemPedido;
import com.alex.modelo.enums.TipoOperacaoCRUD;

public class ItemPedidoDAO extends ControleDaoGenerico<ItemPedido, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public ItemPedidoDAO() {
		super(ItemPedido.class);
	}

	List<ItemPedido> lista = new ArrayList<>();

	/*Salva sem iniciar transação, pois aonde esse método foi chamado já foi iniciado a transação.
	E também não tem mensagem caso tenha sucesso ao salvar registro, só irá aparecer mensagem de sucesso no método que chama
	o método abaixo*/
	public void salvar(ItemPedido itemPedido) {
		try {
			// tirando do estoque
			itemPedido = manager.merge(itemPedido);
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.INSERT_UPDATE);
		}

	}

	public List<ItemEntrada> listAll() {
		return manager.createQuery("FROM ItemPedido", ItemEntrada.class).getResultList();
	}

	// lista filtrado por entrada
	public List<ItemEntrada> listaFiltradoPorEntrada(Long id) {
		return manager.createQuery("FROM ItemPedido WHERE pedido.id = ?1", ItemEntrada.class)
				.setParameter(1, id).getResultList();

	}
	
	public ItemEntrada retornaValorDaUltimaEntradaProd(Long idProduto) {
		List<ItemEntrada> lista = manager.createQuery("FROM ItemPedido WHERE produto.id = ?1 ORDER BY id DESC", ItemEntrada.class)
				.setParameter(1, idProduto).getResultList();
		if(!lista.isEmpty() && lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}


	public int getQuantEntrada(Long idProduto) {
		Query q = manager.createQuery(
				"SELECT COALESCE(sum(i.quant), 0) as quantidade FROM " + 
						"ItemEntrada i WHERE produto_id = " + idProduto);
		Number result = (Number) q.getSingleResult();
		
		return Integer.parseInt(result.toString());
	}

	@Override
	public ItemPedido getEntidade() {
		return this.entidade;
	}

	public List<ItemPedido> getLista() {
		return lista;
	}

	public void setLista(List<ItemPedido> lista) {
		this.lista = lista;
	}

}
