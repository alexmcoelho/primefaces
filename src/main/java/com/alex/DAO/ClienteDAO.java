package com.alex.DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.alex.modelo.Cliente;

public class ClienteDAO extends ControleDaoGenerico<Cliente, Long> implements Serializable{

	public ClienteDAO() {
		super(Cliente.class);
	}

	private static final long serialVersionUID = 1L;
	
	public List<Cliente> listAll(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Cliente"); 
		
		return manager.createQuery("FROM Cliente ORDER BY nome", Cliente.class)
				.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Cliente> porId(){
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Cliente WHERE id = :id");
		
		query = manager.createQuery("FROM Cliente WHERE id = :id ORDER BY nome ASC", Cliente.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();	
	}
	
	public List<Cliente> porId(Long id, int quantRegistros){
		return manager.createQuery("FROM Cliente WHERE id = :id ORDER BY nome ASC", Cliente.class)
				.setParameter("id", id)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	public List<Cliente> listAllLast(){
		return manager.createQuery("FROM Cliente ORDER BY id DESC", Cliente.class).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Cliente> porNomeSemelhante() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Cliente WHERE LOWER(nome) LIKE :nome");
		
		query = manager.createQuery("FROM Cliente WHERE LOWER(nome) LIKE :nome ORDER BY nome ASC", Cliente.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public Cliente pesquisaPorCpfOuCnpj(String cpfCnpj) {
		Cliente cliente = null;
		List<Cliente> lista = new ArrayList<>();
        String consulta;
        try {
            consulta = "FROM Cliente c WHERE c.cpfCnpj = ?1 ORDER BY nome ASC";
            Query query = manager.createQuery(consulta);
            query.setParameter(1, cpfCnpj);
            lista = query.getResultList();
            if(lista.size() > 0){
                cliente = lista.get(0);
            }
        } catch (Exception e) {
            
            System.out.println("Imprimi erro: " + e);
        }finally{
            //manager.close();
        }
        return cliente;
	}
	
	/*
	 * pesquisa cliente a partir de um CPF/CNPJ (validação)
	 */
	public List<Cliente> porCpfCnpj(String cpfCnpj, Long id) {
		if (id != null) {
			return manager.createQuery("FROM Cliente WHERE cpfCnpj = ?1 AND id != ?2", Cliente.class)
					.setParameter(1, cpfCnpj).setParameter(2, id).getResultList();
		}
		return manager.createQuery("FROM Cliente WHERE cpfCnpj = ?1", Cliente.class)
				.setParameter(1, cpfCnpj).getResultList();
	}

	@Override
	public Cliente getEntidade() {
		return this.entidade;
	}

}
