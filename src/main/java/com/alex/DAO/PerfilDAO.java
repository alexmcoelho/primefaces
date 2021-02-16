package com.alex.DAO;

import java.io.Serializable;
import java.util.List;

import com.alex.modelo.Perfil;

public class PerfilDAO extends ControleDaoGenerico<Perfil, Long> implements Serializable{

	private static final long serialVersionUID = 1L;

	public PerfilDAO() {
		super(Perfil.class);
	}
	
	public List<Perfil> listAll(){
		return manager.createQuery("FROM Perfil", Perfil.class).getResultList();
	}
	
	public Perfil porTipo(String p) {
		Perfil perfil = new Perfil();
		List<Perfil> lista = manager.createQuery("from Perfil where LOWER(tipoPerfil) = :tipoPerfil", Perfil.class)
				.setParameter("tipoPerfil", p.toLowerCase())
				.getResultList();		
		if(lista.size() > 0) {
			
			perfil = lista.get(0);
		}
		return perfil;
	}

	@Override
	public Perfil getEntidade() {
		return this.entidade;
	}
	
}
