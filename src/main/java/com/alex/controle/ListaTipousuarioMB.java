package com.alex.controle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.DAO.TipoUsuarioDAO;
import com.alex.modelo.TipoUsuario;
import com.alex.util.FacesUtil;

@Named
@ViewScoped
public class ListaTipousuarioMB implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private TipoUsuarioDAO tipousuarioDAO;
	
	private List<TipoUsuario> tipoUsuarios = new ArrayList<>();
	
	private List<TipoUsuario> tipoUsuariosSelecionas = new ArrayList<>();
	
	@PostConstruct
	public void inicializar(){
		tipoUsuarios = tipousuarioDAO.listAll();
	}
	
	public void excluirSelecionadas(){
		for (TipoUsuario tarefa : tipoUsuariosSelecionas) {
			tipousuarioDAO.excluir(tarefa.getId());
			tipoUsuarios.remove(tarefa);
		}
		
		FacesUtil.addInfoMessage("Tipo(s) excluída(s) com sucesso! ", "");
	}

	public List<TipoUsuario> getTipoUsuarios() {
		return tipoUsuarios;
	}

	public void setTipoUsuarios(List<TipoUsuario> tipoUsuarios) {
		this.tipoUsuarios = tipoUsuarios;
	}

	public List<TipoUsuario> getTipoUsuariosSelecionas() {
		return tipoUsuariosSelecionas;
	}

	public void setTipoUsuariosSelecionas(List<TipoUsuario> tipoUsuariosSelecionas) {
		this.tipoUsuariosSelecionas = tipoUsuariosSelecionas;
	}

	

}
