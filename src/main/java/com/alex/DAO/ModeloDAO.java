package com.alex.DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.alex.modelo.Modelo;

public class ModeloDAO extends ControleDaoGenerico<Modelo, Long> implements Serializable{

	private static final long serialVersionUID = 1L;

	public ModeloDAO() {
		super(Modelo.class);
	}
	
	public List<Modelo> listAll(int primeiroReg, int quantRegistros){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Modelo");
		
		return manager.createQuery("FROM Modelo ORDER BY modelo ASC", Modelo.class)
				.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Modelo> porId(int primeiroReg, int quantRegistros){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Modelo WHERE id = :id");
		
		query = manager.createQuery("FROM Modelo WHERE id = :id", Modelo.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Modelo> porModeloSemelhante(int primeiroReg, int quantRegistros){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Modelo WHERE LOWER(modelo) LIKE :modelo");
		
		query = manager.createQuery("FROM Modelo WHERE LOWER(modelo) LIKE :modelo ORDER BY modelo ASC", Modelo.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Modelo> porMarcaAparelho(int primeiroReg, int quantRegistros){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Modelo WHERE LOWER(aparelho.marca) LIKE :marcaAparelho");
		
		query = manager.createQuery("FROM Modelo WHERE LOWER(aparelho.marca) LIKE :marcaAparelho ORDER BY modelo ASC", Modelo.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Modelo> listaFiltradoPorAparelho(Long id) {
		List<Modelo> lista = new ArrayList<>();

		String consulta;
		try {
			consulta = "FROM Modelo s WHERE s.aparelho.id = ?1";
			Query query = manager.createQuery(consulta);
			query.setParameter(1, id);
			lista = query.getResultList();

			if (lista.isEmpty()) {
				lista = new ArrayList<Modelo>();
			}
		} catch (Exception e) {
			System.out.println("Imprimi erro: " + e);
		} finally {
			// manager.close();
		}

		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<Modelo> filtrarPorAparelhoEModelo(int primeiroReg, int quantRegistros) {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Modelo s WHERE s.aparelho.id = :idAparelho AND LOWER(s.modelo) LIKE :modelo");
		
		List<Modelo> lista = new ArrayList<>();

		String consulta;
		try {
			consulta = "FROM Modelo s WHERE s.aparelho.id = :idAparelho AND LOWER(s.modelo) like :modelo";
			Query query = manager.createQuery(consulta);
			for (String chave : filtros.keySet()) {
				query.setParameter(chave, filtros.get(chave));
			}
			lista = query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();

			if (lista.isEmpty()) {
				lista = new ArrayList<Modelo>();
			}
		} catch (Exception e) {
			System.out.println("Imprimi erro: " + e);
		} finally {
			// manager.close();
		}

		return lista;
	}
	
	public List<Modelo> filtrarPorModelo(String modelo, int quantRegistros) {
		return manager.createQuery("FROM Modelo "
				+ "WHERE modelo = :modelo", Modelo.class)
				.setParameter("modelo", modelo)
				.setMaxResults(quantRegistros).getResultList();
	}

	public boolean isControle() {
		return controle;
	}

	public void setControle(boolean controle) {
		this.controle = controle;
	}

	@Override
	public Modelo getEntidade() {
		return this.entidade;
	}

}
