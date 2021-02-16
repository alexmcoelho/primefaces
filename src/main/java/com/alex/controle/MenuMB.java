package com.alex.controle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.DAO.MenuDAO;
import com.alex.DAO.SubMenuDAO;
import com.alex.DAO.TipoUsuarioDAO;
import com.alex.DAO.UsuarioDAO;
import com.alex.modelo.Menu;
import com.alex.modelo.SubMenu;
import com.alex.modelo.TipoUsuario;
import com.alex.modelo.Usuario;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class MenuMB implements Serializable {

	private static final long serialVersionUID = 1L;
	@Inject
	private SubMenuDAO subMenuDAO;
	@Inject
	private MenuDAO menuDAO;
	@Inject
	private UsuarioDAO usuarioDAO;
	
	@Inject
	private TipoUsuarioDAO tipousuarioDAO;
	
	private String dominioSite = NomeAplicacao.nome;
	private List<SubMenu> subMenus = new ArrayList<>();
	private FacesContext context = FacesContext.getCurrentInstance();
	private StringBuilder builder = new StringBuilder();
	private Long idMenu;
	private String textoDoBuilder = null;

	public Long getIdMenu() {
		return idMenu;
	}

	public void setIdMenu(Long idMenu) {
		this.idMenu = idMenu;
	}

	public List<SubMenu> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(List<SubMenu> subMenus) {
		this.subMenus = subMenus;
	}

	public StringBuilder getBuilder() {
		return builder;
	}

	public void setBuilder(StringBuilder builder) {
		this.builder = builder;
	}

	public String getTextoDoBuilder() {
		return textoDoBuilder;
	}

	public void setTextoDoBuilder(String textoDoBuilder) {
		this.textoDoBuilder = textoDoBuilder;
	}

	@PostConstruct
	public void init() {
		textoDoBuilder = (String) context.getExternalContext().getSessionMap().get("subMenus");
		if(textoDoBuilder == null || textoDoBuilder.equals("")) {
			listaSubMenu();
			percorrelistaSubMenu();
			textoDoBuilder = builder.toString();
			context.getExternalContext().getSessionMap().put("subMenus", textoDoBuilder);
		}
	}
	
	public void listaSubMenu() {
		
		Usuario u = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
		Long idUsuario = u != null ? u.getId() : null;
		if(idUsuario != null) {
			u = null;
			u = usuarioDAO.porIDExtra(idUsuario);
			for(TipoUsuario tipoUsuario: u.getTipoUsuarios()) {
				this.subMenus.addAll(tipousuarioDAO.porID(tipoUsuario.getId()).getSubMenus());
			}
			
			//coloca na lista submenus que n�o estejam relacionados com um menu, assim o submenu virara um menu
			this.subMenus.addAll(subMenuDAO.listaSubmenuFiltradoPorMenuNulo());
			//Coloca na ordem crescente, da menor posi��o para a maior
			Collections.sort(this.subMenus);
		}
	}
	
	public void percorrelistaSubMenu() {
		for (SubMenu subMenu : subMenus) {
			if(subMenu.getMenu() == null) {
				subMenuNulo(subMenu);
			}
			else {
				if(idMenu != subMenu.getMenu().getId()) {
					builder.append("<li class='treeview'>\r\n")
							.append("						<a href='#'>\r\n")
							.append("							<i class='" + subMenu.getMenu().getIcone() + "' aria-hidden='true'></i>\r\n")
							.append("							<span> " + subMenu.getMenu().getNome() + "</span> \r\n")
							.append("							<span class='pull-right-container'><i class='fa fa-angle-left pull-right'></i></span>\r\n")
							.append("						</a>\r\n")
							.append("						\r\n")
							.append("						<ul class='treeview-menu'>\r\n");
					for (SubMenu subMenu2 : listaSubmenuFiltradoPorMenu(subMenu.getMenu().getId())) {
						builder.append("								<li><a href='" + dominioSite + subMenu2.getLink() + "'>\r\n")
							.append("										<i class='" + subMenu2.getIcone() + "' aria-hidden='true'></i>\r\n")
							.append("										<span>" + subMenu2.getNome() + "</span>\r\n")
							.append("									</a></li>\r\n");
					}
					builder.append("						</ul></li>");
				}
			}
		}
	}

	public List<SubMenu> listaSubmenuFiltradoPorMenu(Long id) {
		ArrayList<SubMenu> lista = new ArrayList<>();
		idMenu = id;
		Menu menu = new Menu();
		menu = menuDAO.porID(id);
		for (SubMenu subMenu : subMenus) {
			if(subMenu.getMenu() == menu) {
				if(!lista.contains(subMenu)) {
					lista.add(subMenu);
				}				
			}
		}
		return lista;
	}
	
	public void subMenuNulo(SubMenu subMenu) {
		builder.append("<li><a href='" + dominioSite + subMenu.getLink() + "'>\r\n")
			.append("						<i class='" + subMenu.getIcone() + "'></i>\r\n")
			.append("						<span>" + subMenu.getNome() + "</span>\r\n")
			.append("					</a></li>");
	}

}
