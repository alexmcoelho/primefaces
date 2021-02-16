package com.alex.DAO;

import java.io.Serializable;
import java.util.List;

import com.alex.modelo.Categoria;

public class CategoriaDAO extends ControleDaoGenerico<Categoria, Long> implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public CategoriaDAO() {
		super(Categoria.class);
	}
	public List<Categoria> listAllSelectOneMenu(){
		return manager.createQuery("FROM Categoria ORDER BY descricao", Categoria.class)
				.getResultList();
	}

	public List<Categoria> listAll(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Categoria");
		
		return manager.createQuery("FROM Categoria ORDER BY descricao", Categoria.class)
				.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> porId(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Categoria WHERE id = :id");
		
		query = manager.createQuery("FROM Categoria WHERE id = :id", Categoria.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();	
	}
	
	@SuppressWarnings("unchecked")
	public List<Categoria> porDescricaoSemelhante(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Categoria WHERE LOWER(descricao) like :descricao");
		
		query = manager.createQuery("FROM Categoria WHERE LOWER(descricao) like :descricao", Categoria.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@Override
	public Categoria getEntidade() {
		return this.entidade;
	}
	
}
