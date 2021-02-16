package com.alex.DAO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import com.alex.exception.NegocioException;
import com.alex.modelo.ItemProdSaida;
import com.alex.modelo.SaidaDeProdutos;
import com.alex.modelo.enums.TipoOperacaoCRUD;

public class ItemProdSaidaDAO extends ControleDaoGenerico<ItemProdSaida, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public ItemProdSaidaDAO() {
		super(ItemProdSaida.class);
	}

	List<ItemProdSaida> lista = new ArrayList<>();

	/*
	 * Salva sem iniciar transação, pois aonde esse método foi chamado já foi
	 * iniciado a transação. E também não tem mensagem caso tenha sucesso ao salvar
	 * registro, só irá aparecer mensagem de sucesso no método que chama o método
	 * abaixo
	 */
	public void salvar(ItemProdSaida itemProduto) {
		try {

			itemProduto = manager.merge(itemProduto);
			this.entidade = itemProduto;
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.INSERT_UPDATE);
		}

	}

	public List<ItemProdSaida> listAll() {
		return manager.createQuery("FROM ItemProdSaida", ItemProdSaida.class).getResultList();
	}

	// filtra itens de uma venda sem faturas geradas
	public List<ItemProdSaida> itensProdSaidaSemFaturasGeradas() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		return manager
				.createQuery("SELECT i FROM ItemProdSaida i "
						+ "INNER JOIN i.saidaDeProdutos s LEFT JOIN i.saidaDeProdutos.detalhesFaturas del "
						+ "WHERE del.saidaDeProdutos IS NULL ORDER BY s.data DESC", ItemProdSaida.class)
				.getResultList();
	}

	// filtra itens de uma venda com faturas geradas
	public List<ItemProdSaida> itensProdSaidaComFaturasGeradas() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		List<SaidaDeProdutos> saidas = manager.createQuery(
				"SELECT DISTINCT s FROM SaidaDeProdutos s join fetch s.detalhesFaturas del ORDER BY s.data DESC",
				SaidaDeProdutos.class).getResultList();
		lista = new ArrayList<ItemProdSaida>();
		saidas.forEach(l -> lista.addAll(l.getItemProdSaidas()));
		return lista;
	}

	// filtra itens de uma venda sem faturas geradas pelo código da venda
	public List<ItemProdSaida> itensProdSaidaSemFaturasGeradasPorIdSaida(Long idSaida) {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		return manager
				.createQuery(
						"SELECT i FROM ItemProdSaida i "
								+ "INNER JOIN i.saidaDeProdutos s LEFT JOIN i.saidaDeProdutos.detalhesFaturas del "
								+ "WHERE del.saidaDeProdutos IS NULL AND s.id = ?1 ORDER BY s.data DESC",
						ItemProdSaida.class)
				.setParameter(1, idSaida).getResultList();
	}

	// filtra itens de uma venda com faturas geradas pelo código da venda
	public List<ItemProdSaida> itensProdSaidaComFaturasGeradasPorIdSaida(Long idSaida) {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		List<SaidaDeProdutos> saidas = manager
				.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s join fetch s.detalhesFaturas del "
						+ "WHERE s.id = ?1 ORDER BY s.data DESC", SaidaDeProdutos.class)
				.setParameter(1, idSaida).getResultList();
		lista = new ArrayList<ItemProdSaida>();
		saidas.forEach(l -> lista.addAll(l.getItemProdSaidas()));
		return lista;
	}

	// filtra itens de uma venda sem faturas geradas pelo nome do cliente
	public List<ItemProdSaida> itensProdSaidaSemFaturasGeradasPorNomeCliente(String nome) {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		return manager.createQuery(
				"SELECT i FROM ItemProdSaida i "
						+ "INNER JOIN i.saidaDeProdutos s LEFT JOIN i.saidaDeProdutos.detalhesFaturas del "
						+ "WHERE del.saidaDeProdutos IS NULL AND LOWER (s.cliente.nome) LIKE ?1 ORDER BY s.data DESC",
				ItemProdSaida.class).setParameter(1, nome).getResultList();
	}

	// filtra itens de uma venda com faturas geradas pelo nome do cliente
	public List<ItemProdSaida> itensProdSaidaComFaturasGeradasPorNomeCliente(String nome) {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		List<SaidaDeProdutos> saidas = manager
				.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s join fetch s.detalhesFaturas del "
						+ "WHERE LOWER (s.cliente.nome) LIKE ?1 ORDER BY s.data DESC", SaidaDeProdutos.class)
				.setParameter(1, nome.toLowerCase() + "%").getResultList();

		lista = new ArrayList<ItemProdSaida>();
		saidas.forEach(l -> lista.addAll(l.getItemProdSaidas()));
		return lista;
	}

	// filtra itens de uma venda sem faturas geradas pelo CPF ou CNPJ do cliente
	public List<ItemProdSaida> itensProdSaidaSemFaturasGeradasPorCpfOuCnpjCliente(String cpfCnpj) {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		return manager.createQuery(
				"SELECT i FROM ItemProdSaida i "
						+ "INNER JOIN i.saidaDeProdutos s LEFT JOIN i.saidaDeProdutos.detalhesFaturas del "
						+ "WHERE del.saidaDeProdutos IS NULL AND s.cliente.cpfCnpj = ?1 ORDER BY s.data DESC",
				ItemProdSaida.class).setParameter(1, cpfCnpj).getResultList();
	}

	// filtra itens de uma venda com faturas geradas pelo CPF ou CNPJ do cliente
	public List<ItemProdSaida> itensProdSaidaComFaturasGeradasPorCpfOuCnpjCliente(String cpfCnpj) {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		List<SaidaDeProdutos> saidas = manager
				.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s join fetch s.detalhesFaturas del "
						+ "WHERE s.cliente.cpfCnpj = ?1 ORDER BY s.data DESC", SaidaDeProdutos.class)
				.setParameter(1, cpfCnpj).getResultList();
		lista = new ArrayList<ItemProdSaida>();
		saidas.forEach(l -> lista.addAll(l.getItemProdSaidas()));
		return lista;
	}

	// filtra itens de uma venda sem faturas geradas por intervalo entre datas
	public List<ItemProdSaida> itensProdSaidaSemFaturasGeradasPorIntervaloDeDatas(Date dataInicio, Date dataFim) {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		return manager.createQuery(
				"SELECT i FROM ItemProdSaida i "
						+ "INNER JOIN i.saidaDeProdutos s LEFT JOIN i.saidaDeProdutos.detalhesFaturas del "
						+ "WHERE del.saidaDeProdutos IS NULL AND s.data BETWEEN ?1 AND ?2 ORDER BY s.data DESC",
				ItemProdSaida.class).setParameter(1, dataInicio).setParameter(2, dataFim).getResultList();
	}

	// filtra itens de uma venda com faturas geradas intervalo entre datas
	public List<ItemProdSaida> itensProdSaidaComFaturasGeradasPorIntervaloDeDatas(Date dataInicio, Date dataFim) {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		List<SaidaDeProdutos> saidas = manager
				.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s join fetch s.detalhesFaturas del "
						+ "WHERE s.data BETWEEN ?1 AND ?2 ORDER BY s.data DESC", SaidaDeProdutos.class)
				.setParameter(1, dataInicio).setParameter(2, dataFim).getResultList();

		lista = new ArrayList<ItemProdSaida>();
		saidas.forEach(l -> lista.addAll(l.getItemProdSaidas()));
		return lista;
	}

	// lista filtrado por saida
	public List<ItemProdSaida> listaFiltradoPorSaida(Long id) {
		return manager.createQuery("SELECT i FROM ItemProdSaida i "
				+ "JOIN FETCH i.saidaDeProdutos "
				+ "JOIN FETCH i.produto p "
				+ "WHERE i.saidaDeProdutos.id = ?1", ItemProdSaida.class)
				.setParameter(1, id).getResultList();
	}

	// lista filtrado por cpf cliente
	public List<ItemProdSaida> listaFiltradoPorCpfouCnpjCliente(String cpfCnpj) {
		return manager.createQuery("FROM ItemProdSaida WHERE saidaDeProdutos.cliente.cpfCnpj = ?1", ItemProdSaida.class)
				.setParameter(1, cpfCnpj).getResultList();
	}

	// lista filtrado por nome cliente
	public List<ItemProdSaida> listaFiltradoPorNomeCliente(String nome) {
		return manager.createQuery("FROM ItemProdSaida WHERE LOWER (saidaDeProdutos.cliente.nome) LIKE ?1",
				ItemProdSaida.class).setParameter(1, nome.toLowerCase() + "%").getResultList();
	}

	// lista filtro intervalo entre datas
	public List<ItemProdSaida> intervaloEntreDatas(Date dataInicio, Date dataFim) {
		return manager
				.createQuery("FROM ItemProdSaida WHERE saidaDeProdutos.data BETWEEN ?1 AND ?2", ItemProdSaida.class)
				.setParameter(1, dataInicio).setParameter(2, dataFim).getResultList();
	}

	// Retorna o total geral de uma venda
	public BigDecimal getValorTotal(Long idSaida) {
		lista = listaFiltradoPorSaida(idSaida);
		BigDecimal valor = new BigDecimal("0.00");
		for (ItemProdSaida itemProdSaida : lista) {
			valor = valor.add(itemProdSaida.getSubTotal(), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN);
		}
		return valor;
	}

	// filtra pelas iniciais do nome
	public List<ItemProdSaida> porNomeSemelhante(String nome) {
		if (nome != null && !"".equalsIgnoreCase(nome)) {
			return manager.createQuery("FROM ItemProdSaida WHERE LOWER(saidaDeProdutos.cliente.nome) LIKE :nome",
					ItemProdSaida.class).setParameter("nome", "" + nome.toLowerCase() + "%").getResultList();
		}
		return new ArrayList<ItemProdSaida>();
	}

	// filtra por produto
	public List<ItemProdSaida> porIdProduto(Long id) {
		return manager.createQuery("FROM ItemProdSaida WHERE produto.id = :id", ItemProdSaida.class)
				.setParameter("id", id).getResultList();
	}
	
	// filtra por produto, mas se tiver pesquisando dentro de Saída de produtos, não poderá ser incluida essa Saída na pesquisa
	public List<ItemProdSaida> porIdProdutoAndIdSaida(Long id, Long idSaida) {
		if(idSaida != null) {
			lista = manager.createQuery("FROM ItemProdSaida "
					+ "WHERE saidaDeProdutos.id <> :idSaida "
					+ "AND produto.id = :id ", ItemProdSaida.class)
				.setParameter("idSaida", idSaida)
				.setParameter("id", id)
				.getResultList();
		}
		else {
			lista = manager.createQuery("FROM ItemProdSaida WHERE produto.id = :id", ItemProdSaida.class)
						.setParameter("id", id).getResultList();
		}
		return lista;
	}

	// filtra por produto sem incluir a saidaDeProdutos informada
	public List<ItemProdSaida> porIdProdutoSemDeterminadaSaida(Long id, Long idSaida) {
		return manager.createQuery("FROM ItemProdSaida WHERE produto.id = :id AND saidaDeProdutos.id <> :idSaida", ItemProdSaida.class)
				.setParameter("id", id)
				.setParameter("idSaida", idSaida).getResultList();
	}

	public int getQuantSaida(Long idProduto) {
		Query q = manager.createQuery("SELECT COALESCE(sum(i.quant), 0) as quantidade FROM "
				+ "ItemProdSaida i WHERE produto_id = " + idProduto);
		Number result = (Number) q.getSingleResult();

		return Integer.parseInt(result.toString());
	}

	@Override
	public ItemProdSaida getEntidade() {
		return this.entidade;
	}

	public List<ItemProdSaida> getLista() {
		return lista;
	}

	public void setLista(List<ItemProdSaida> lista) {
		this.lista = lista;
	}

}
