package com.alex.DAO;

import java.io.Serializable;
import java.util.List;

import com.alex.modelo.Aparelho;

public class AparelhoDAO extends ControleDaoGenerico<Aparelho, Long> implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public AparelhoDAO() {
		super(Aparelho.class);
	}

	public List<Aparelho> listAll(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Aparelho");
		
		return manager.createQuery("FROM Aparelho ORDER BY marca", Aparelho.class)
				.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Aparelho> porId(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Aparelho WHERE id = :id");
		
		query = manager.createQuery("FROM Aparelho WHERE id = :id", Aparelho.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();	
	}
	
	@SuppressWarnings("unchecked")
	public List<Aparelho> porMarcaSemelhante(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Aparelho WHERE LOWER(marca) like :marca");
		
		query = manager.createQuery("FROM Aparelho WHERE LOWER(marca) like :marca", Aparelho.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	public List<Aparelho> porMarca(String marca, int quantRegistros){
		return manager.createQuery("FROM Aparelho WHERE marca = :marca", Aparelho.class)
				.setParameter("marca", marca)
				.setMaxResults(quantRegistros).getResultList();
	}

	@Override
	public Aparelho getEntidade() {
		return this.entidade;
	}
	
}
