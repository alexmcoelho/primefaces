package com.alex.DAO;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.alex.exception.NegocioException;
import com.alex.modelo.ItemPedido;
import com.alex.modelo.Pedido;
import com.alex.modelo.enums.TipoOperacaoCRUD;
import com.alex.util.FacesUtil;
import com.alex.util.Transacional;

public class PedidoDAO extends ControleDaoGenerico<Pedido, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public PedidoDAO() {
		super(Pedido.class);
	}

	@Inject
	private ItemPedidoDAO itemPedidoDAO;
	
	@Transacional
	public void salvar(Pedido pedido, List<ItemPedido> itemPedidos) {

		try {
			//só filtra itens da lista que tenham id diferente de nulo
			if(pedido.getItemPedidos() != null && pedido.getItemPedidos().size() > 0) {
				pedido.setItemPedidos(pedido.getItemPedidos().stream()        
						.filter(line -> line.getId() != null)     
						.collect(Collectors.toList()));
			}
			pedido = manager.merge(pedido);
			manager.flush();
			this.entidade = pedido;
			// INICIO - para dar baixa
			for (ItemPedido i : itemPedidos) {
				i.setPedido(pedido);
				// irá inserir na tabela de qualquer forma o itemproSaida para depois possa
				// aparecer na consulta
				itemPedidoDAO.salvar(i);
				// FIM - para dar baixa
			}
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.INSERT_UPDATE);
		}
		FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoSalvar(), "");
	}
	
	@SuppressWarnings("unchecked")
	public List<Pedido> listAll() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Pedido");
		
		query = manager.createQuery("FROM Pedido ORDER BY id DESC", Pedido.class);
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Pedido> listaPorConcluido() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Pedido WHERE concluido = :concluido");
		
		query = manager.createQuery("FROM Pedido WHERE concluido = :concluido ORDER BY id DESC", Pedido.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Pedido> intervaloEntreDatas() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Pedido WHERE data BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM Pedido WHERE data BETWEEN :dataInicio AND :dataFim ORDER BY data DESC", Pedido.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	public Pedido retornaUltimoRegistroQueAindaNaoFoiConcluido() {
		query = manager.createQuery("FROM Pedido WHERE concluido = 0 ORDER BY id DESC", Pedido.class);
		
		return query.setMaxResults(1).getResultList() != null && query.setMaxResults(1).getResultList().size() > 0 ?
				(Pedido) query.setMaxResults(1).getResultList().get(0) : null;
	}

	@Override
	public Pedido getEntidade() {
		return this.entidade;
	}

}
