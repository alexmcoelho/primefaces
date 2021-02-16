package com.alex.DAO;

import java.io.Serializable;
import java.util.List;

import com.alex.modelo.Menu;

public class MenuDAO extends ControleDaoGenerico<Menu, Long> implements Serializable{

	public MenuDAO() {
		super(Menu.class);
	}

	private static final long serialVersionUID = 1L;
	
	public List<Menu> listAll(){
		return manager.createQuery("SELECT * FROM Menu", Menu.class).getResultList();
	}

	@Override
	public Menu getEntidade() {
		return this.entidade;
	}

}
