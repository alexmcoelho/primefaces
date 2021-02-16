package com.alex.DAO;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Hibernate;

import com.alex.modelo.Produto;

public class ProdutoDAO extends ControleDaoGenerico<Produto, Long> implements Serializable{

	private static final long serialVersionUID = 1L;

	public ProdutoDAO() {
		super(Produto.class);
	}
	
	public Produto porIDConverter(String id) {
		return manager.find(Produto.class, Long.parseLong(id));
	}
	
	public Produto porID(Long id) {
		List<Produto> lista = manager.createQuery("SELECT p FROM Produto p JOIN FETCH p.categoria WHERE p.id = ?1", Produto.class)
				.setParameter(1, id).getResultList();
		if(lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}
	
	//filtra todos produtos que foi cadastrado entrada
	public List<Produto> listAllComEntradasCadastradas(){
		return manager.createQuery("SELECT DISTINCT p FROM Produto p JOIN FETCH p.itemEntradas itens " + 
				"ORDER BY p.descricao ASC", Produto.class).getResultList();
	}

	//filtra todos produtos não impporta se foi gerado entrada ou não
	public List<Produto> listAll(int primeiroReg, int quantRegistros){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Produto");
		
		//para iniciar com categoria
		return manager.createQuery("SELECT p FROM Produto p JOIN FETCH p.categoria "
				+ "ORDER BY p.descricao ASC", Produto.class)
				.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	//filtra todos produtos não impporta se foi gerado entrada ou não (trazendo listas para calculos)
	public List<Produto> listAllTrazendoListas(int primeiroReg, int quantRegistros){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Produto");
		
		//para iniciar com categoria
		return manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "JOIN FETCH p.categoria "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "LEFT JOIN p.itemEntradas entrada "
				+ "ORDER BY p.descricao ASC", Produto.class)
				.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	//filtra todos produtos não impporta se foi gerado entrada ou não (sem paginação)
    public List<Produto> listAll(){
    	return manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "LEFT JOIN p.itemEntradas entrada "
				+ "ORDER BY p.descricao ASC", Produto.class).getResultList();
    }
	
	//filtra todos produtos não impporta se foi gerado entrada ou não na ordem descrecente
	public List<Produto> listAllLast(){
		return manager.createQuery("FROM Produto ORDER BY id DESC", Produto.class).getResultList();
	}
	
	//filtra todos produtos que foi cadastrado entrada na ordem descrecente
	public List<Produto> listAllLastComEntradasCadastradas(){
		return manager.createQuery("SELECT DISTINCT p FROM Produto p JOIN FETCH p.itemEntradas itens " + 
				"ORDER BY descricao ASC", Produto.class).getResultList();
	}
	
	/* pesquisa interessante de estudar
	 * public List<Produto> porDescricaoSemelhante(String descricao) {
		List<Produto> lista = new ArrayList<>();
		Produto p = new Produto();
		List<Object[]> results = manager.createQuery("SELECT p.id, p.descricao, p.percentualLucro "
				+ "FROM Produto p INNER JOIN ItemEntrada i "
				+ "ON p.id=i.produto.id "
				+ "WHERE LOWER(p.descricao) LIKE :descricao "
				+ "GROUP BY p.id")
				.setParameter("descricao", "" + descricao.toLowerCase() + "%")
				.getResultList();
		
		for (Object[] result : results) {
			p.setId((Long) result[0]);
			p.setDescricao((String) result[1]);
			p.setPercentualLucro((BigDecimal) result[2]);
			lista.add(p);
			p = new Produto();
		}
		
		return lista;
	}*/
	
	@SuppressWarnings("unchecked")
	public List<Produto> porId() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Produto WHERE id = :id");

		query = manager.createQuery("SELECT p FROM Produto p JOIN FETCH p.categoria "
				+ "WHERE p.id = :id ORDER BY p.descricao ASC", Produto.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}

		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Produto> porIdTrazendoListas() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Produto WHERE id = :id");

		query = manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "JOIN FETCH p.categoria "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "LEFT JOIN p.itemEntradas entrada "
				+ "WHERE p.id = :id ORDER BY p.descricao ASC", Produto.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}

		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	//filtra produtos pela descrição não impporta se foi gerado entrada ou não
	@SuppressWarnings("unchecked")
	public List<Produto> porDescricaoSemelhante() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Produto WHERE LOWER (descricao) LIKE :descricao");

		query = manager.createQuery("SELECT p FROM Produto p JOIN FETCH p.categoria "
				+ "WHERE LOWER(p.descricao) LIKE :descricao ORDER BY p.descricao ASC", Produto.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}

		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	//filtra produtos pela descrição não impporta se foi gerado entrada ou não (trazendo listas para calculos)
	@SuppressWarnings("unchecked")
	public List<Produto> porDescricaoSemelhanteTrazendoListas() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Produto WHERE LOWER (descricao) LIKE :descricao");

		query = manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "JOIN FETCH p.categoria "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "LEFT JOIN p.itemEntradas entrada "
				+ "WHERE LOWER(p.descricao) LIKE :descricao ORDER BY p.descricao ASC", Produto.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}

		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	//filtra produtos pela categoria não impporta se foi gerado entrada ou não
	@SuppressWarnings("unchecked")
	public List<Produto> porDescricaoCategoriaSemelhante() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Produto WHERE LOWER(categoria.descricao) LIKE :descricaoCategoria");

		query = manager.createQuery("SELECT p FROM Produto p JOIN FETCH p.categoria "
				+ "WHERE LOWER(p.categoria.descricao) LIKE :descricaoCategoria ORDER BY p.descricao ASC", Produto.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}

		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	//filtra produtos pela categoria não impporta se foi gerado entrada ou não (trazendo listas para calculos)
	@SuppressWarnings("unchecked")
	public List<Produto> porDescricaoCategoriaSemelhanteTrazendoListas() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Produto WHERE LOWER(categoria.descricao) LIKE :descricaoCategoria");

		query = manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "JOIN FETCH p.categoria "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "LEFT JOIN p.itemEntradas entrada "
				+ "WHERE LOWER(p.categoria.descricao) LIKE :descricaoCategoria ORDER BY p.descricao ASC", Produto.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}

		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	public List<Produto> porDescricaoCategoriaSemelhanteTrazendoListas(String descricao) {
		return manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "LEFT JOIN p.itemEntradas entrada "
				+ "WHERE LOWER(p.categoria.descricao) LIKE ?1 ORDER BY p.descricao ASC", Produto.class)
				.setParameter(1, descricao).getResultList();
	}
	
	public List<Produto> porDescricaoSemelhanteTrazendoListas(String descricao) {
		return manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "LEFT JOIN p.itemEntradas entrada "
				+ "WHERE LOWER(p.descricao) LIKE ?1 ORDER BY p.descricao ASC", Produto.class)
				.setParameter(1, descricao).getResultList();
	}
	
	//filtra produtos com entradas de produtos cadastrados pelas iniciais da descrição do produto
	@SuppressWarnings("unchecked")
	public List<Produto> porDescricaoSemelhanteComEntradaGerada(){
		
		//o que ajudou a fazer o método https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT p) FROM Produto p "
				+ "JOIN p.itemEntradas itens "
				+ "WHERE LOWER (p.descricao) LIKE :descricaoComEntradaGerada");
		
		query = manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "JOIN FETCH p.itemEntradas entrada "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "WHERE LOWER (p.descricao) LIKE :descricaoComEntradaGerada ORDER BY p.descricao ASC", Produto.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		List<Produto> lista = query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
		
		lista.forEach(obj -> {
			obj.getItemProdutos().forEach(obj2 -> {
				Hibernate.initialize(obj2.getOrdemDeServico());
			});
		});
		
		return lista;
		
	}
	
	//filtra produtos com entradas de produtos cadastrados pelas iniciais da descrição do produto
	@SuppressWarnings("unchecked")
	public List<Produto> porIdComEntradaGerada(){
		
		//o que ajudou a fazer o método https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT p) FROM Produto p JOIN p.itemEntradas itens " + 
				"WHERE p.id = :idComEntradaGerada");
		
		query = manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "JOIN FETCH p.itemEntradas entrada "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "WHERE p.id = :idComEntradaGerada", Produto.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		List<Produto> lista = query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
		
		lista.forEach(obj -> {
			obj.getItemProdutos().forEach(obj2 -> {
				Hibernate.initialize(obj2.getOrdemDeServico());
			});
		});
		
		return lista;
		
	}
	
	//filtra produtos pelo id não impporta se foi gerado entrada ou não
	public List<Produto> porId(Long id) {
		return manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "LEFT JOIN p.itemEntradas entrada "
				+ "WHERE p.id = ?1", Produto.class)
					.setParameter(1, id).getResultList();
	}
	
	//TESTAR MAIS ESSE MÉTODO
	//filtra produtos com entradas de produtos cadastrados pelas iniciais da descrição do produto
	public List<Produto> porIdComEntradaCadastrada(Long id) {
		//o que ajudou a fazer o método https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		return manager.createQuery("SELECT DISTINCT p FROM Produto p "
				+ "JOIN FETCH p.itemEntradas entrada "
				+ "LEFT JOIN p.itemProdutos os "
				+ "LEFT JOIN p.itemProdSaidas saida "
				+ "WHERE p.id = ?1 ORDER BY p.descricao ASC", Produto.class)
				.setParameter(1, id).getResultList();	
	}
	
	/*
	 * pesquisa produtos a partir de um IMEI (validação)
	 */
	public List<Produto> porImei(String imei, Long id) {
		if (id != null) {
			return manager.createQuery("FROM Produto WHERE imei = ?1 AND id != ?2", Produto.class)
					.setParameter(1, imei).setParameter(2, id).getResultList();
		}
		return manager.createQuery("FROM Produto WHERE imei = ?1", Produto.class)
				.setParameter(1, imei).getResultList();
	}

	@Override
	public Produto getEntidade() {
		return this.entidade;
	}
	
}
