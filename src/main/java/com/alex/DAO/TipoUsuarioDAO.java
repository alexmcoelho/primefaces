package com.alex.DAO;

import java.io.Serializable;
import java.util.List;

import com.alex.modelo.TipoUsuario;

public class TipoUsuarioDAO extends ControleDaoGenerico<TipoUsuario, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public TipoUsuarioDAO() {
		super(TipoUsuario.class);
	}

	public List<TipoUsuario> listAll(){
		return manager.createQuery("FROM TipoUsuario", TipoUsuario.class).getResultList();
	}
	
	public TipoUsuario porTipo(String tipo) {
		TipoUsuario tipoUsuario = null;
		List<TipoUsuario> lista = manager.createQuery("from TipoUsuario where LOWER(tipo) = :tipo", TipoUsuario.class)
				.setParameter("tipo", "" + tipo.toLowerCase() + "%")
				.getResultList();		
		if(lista.size() > 0) {
			tipoUsuario = new TipoUsuario();
			tipoUsuario = lista.get(0);
		}
		return tipoUsuario;
	}

	@Override
	public TipoUsuario getEntidade() {
		return this.entidade;
	}

}
