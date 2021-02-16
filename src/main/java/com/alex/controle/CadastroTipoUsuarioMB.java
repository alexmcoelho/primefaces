package com.alex.controle;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import com.alex.DAO.TipoUsuarioDAO;
import com.alex.modelo.TipoUsuario;

@Named 
@ViewScoped
public class CadastroTipoUsuarioMB implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private TipoUsuario tipousuario = new TipoUsuario();
	
	@Inject
	private TipoUsuarioDAO tipousuarioDAO;
	
	private Long idTipoUsuario;
	
	public void inicializar(){
		if(idTipoUsuario != null){
			tipousuario = tipousuarioDAO.porID(idTipoUsuario);
		}
	}
	
	//vai retornar uma String porque assim que salvar vai retornar para lista
	
	public String salvar(){
		tipousuarioDAO.salvar(tipousuario);
		return "/pages/usuario/lista-tipo.xhtml?faces-redirect=true";
	}
	
	
	public String excluir(){
		tipousuarioDAO.excluir(tipousuario.getId());
		return "lista-tipo.xhtml?Faces-redirect=true";
	}
	
	public TipoUsuario getTipousuario() {
		return tipousuario;
	}

	public void setTipousuario(TipoUsuario tipousuario) {
		this.tipousuario = tipousuario;
	}

	public Long getIdTipoUsuario() {
		return idTipoUsuario;
	}

	public void setIdTipoUsuario(Long idTipoUsuario) {
		this.idTipoUsuario = idTipoUsuario;
	}

}
