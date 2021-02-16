package com.alex.DAO;

import java.io.Serializable;
import java.util.List;

import com.alex.modelo.Fornecedor;

public class FornecedorDAO extends ControleDaoGenerico<Fornecedor, Long> implements Serializable{

	private static final long serialVersionUID = 1L;

	public FornecedorDAO() {
		super(Fornecedor.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Fornecedor> porId(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Fornecedor WHERE id = :id");
		
		query = manager.createQuery("FROM Fornecedor WHERE id = :id ORDER BY nome ASC", Fornecedor.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();	
	}
	
	public List<Fornecedor> porId(Long id, int quantRegistros){
		return manager.createQuery("FROM Fornecedor WHERE id = :id ORDER BY nome ASC", Fornecedor.class)
				.setParameter("id", id)
				.setMaxResults(quantRegistros).getResultList();	
	}
	
	@SuppressWarnings("unchecked")
	public List<Fornecedor> listAll(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Fornecedor");
		
		query = manager.createQuery("FROM Fornecedor ORDER BY nome ASC", Fornecedor.class);
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	public List<Fornecedor> listAllLast(){
		return manager.createQuery("FROM Fornecedor ORDER BY id DESC", Fornecedor.class).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Fornecedor> porNomeSemelhante() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Fornecedor WHERE LOWER(nome) LIKE :nome");
		
		query = manager.createQuery("FROM Fornecedor WHERE LOWER(nome) LIKE :nome ORDER BY nome ASC", Fornecedor.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@Override
	public Fornecedor getEntidade() {
		return this.entidade;
	}

}
