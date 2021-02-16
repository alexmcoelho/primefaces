package com.alex.DAO;

import java.io.Serializable;
import java.util.List;

import com.alex.modelo.Email;

public class EmailDAO extends ControleDaoGenerico<Email, Long> implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public EmailDAO() {
		super(Email.class);
	}

	public List<Email> listAll(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Email");
		
		return manager.createQuery("FROM Email", Email.class)
				.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Email> porId(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Email WHERE id = :id");
		
		query = manager.createQuery("FROM Email WHERE id = :id", Email.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();	
	}

	@Override
	public Email getEntidade() {
		return this.entidade;
	}
	
}
