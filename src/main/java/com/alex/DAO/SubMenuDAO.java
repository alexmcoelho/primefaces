package com.alex.DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.alex.modelo.SubMenu;

public class SubMenuDAO extends ControleDaoGenerico<SubMenu, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public SubMenuDAO() {
		super(SubMenu.class);
	}

	public List<SubMenu> listAll() {
		return manager.createQuery("FROM SubMenu", SubMenu.class).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SubMenu> listaSubmenuFiltradoPorMenu(Long id) {
		List<SubMenu> lista = new ArrayList<>();

		String consulta;
		try {
			consulta = "FROM SubMenu s WHERE s.id = ?1";
			Query query = manager.createQuery(consulta);
			query.setParameter(1, id);
			lista = query.getResultList();

			if (lista.isEmpty()) {
				lista = new ArrayList<SubMenu>();
			}
		} catch (Exception e) {
			System.out.println("Imprimi erro: " + e);
		} finally {
			// manager.close();
		}

		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubMenu> listaSubmenuFiltradoPorMenuNulo() {
		List<SubMenu> lista = new ArrayList<>();

		String consulta;
		try {
			consulta = "FROM SubMenu s WHERE s.menu.id = null";
			Query query = manager.createQuery(consulta);
			lista = query.getResultList();

			if (lista.isEmpty()) {
				lista = new ArrayList<SubMenu>();
			}
		} catch (Exception e) {
			System.out.println("Imprimi erro: " + e);
		} finally {
			// manager.close();
		}

		return lista;
	}
	
	@SuppressWarnings("unchecked")
	public List<SubMenu> listaSubmenuFiltradoPorMenu2(Long id) {
		List<SubMenu> lista = new ArrayList<>();

		String consulta;
		try {
			consulta = "FROM SubMenu s WHERE s.menu.id = ?1";
			Query query = manager.createQuery(consulta);
			query.setParameter(1, id);
			lista = query.getResultList();

			if (lista.isEmpty()) {
				lista = new ArrayList<SubMenu>();
			}
		} catch (Exception e) {
			System.out.println("Imprimi erro: " + e);
		} finally {
			// manager.close();
		}

		return lista;
	}

	@Override
	public SubMenu getEntidade() {
		return this.entidade;
	}

}
