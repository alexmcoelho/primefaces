package com.alex.controle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.alex.DAO.PerfilDAO;
import com.alex.DAO.TipoUsuarioDAO;
import com.alex.DAO.UsuarioDAO;
import com.alex.modelo.Perfil;
import com.alex.modelo.TipoUsuario;
import com.alex.modelo.Usuario;
import com.alex.util.FacesUtil;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class UsuarioCadastroMB extends ComplementoMB implements Serializable, ContratoCaminhoCancelar {

	private static final long serialVersionUID = 1L;

	@Inject
	private Usuario usuario;

	@Inject
	private PerfilDAO perfilDAO;
	
	@Inject
	private TipoUsuarioDAO tipousuarioDAO;

	@Inject
	private UsuarioDAO usuarioDAO;

	@Inject
	private TipoUsuario tipoUsuario;

	private Long idUsuario;
	
	private Boolean apareceSenha;

	private List<Perfil> perfis = new ArrayList<>();

	private List<Perfil> perfiSalvaBanco = new ArrayList<>();

	private List<TipoUsuario> tipoUsuarios = new ArrayList<>();
	
	private List<TipoUsuario> tipoUsuariosSalvar = new ArrayList<TipoUsuario>();

	RequestContext request = RequestContext.getCurrentInstance();

	private String[] selecionaPerfis;
	
	private String mensagem;
	
	private List<Perfil> listaPerfisSelecionado = new ArrayList<>();
	
	public void inicializar() {

		if (idUsuario != null) {
			usuario = usuarioDAO.porID(idUsuario);
			usuario = usuario == null ? new Usuario() : usuario;
			if(usuario.getId() != null) {
				listaPerfisSelecionado = usuario != null ? usuario.getPerfis() : null;
				//vai definir checkbox selecionados
				String[] perfisSelecionados = new String[listaPerfisSelecionado.size()];
				for (int i = 0; i < listaPerfisSelecionado.size(); i++) {
					perfisSelecionados[i] = listaPerfisSelecionado.get(i).getTipoPerfil();
				}
				tipoUsuario = usuario != null && usuario.getTipoUsuarios().size() > 0 ? usuario.getTipoUsuarios().get(0) : new TipoUsuario();
				setSelecionaPerfis(perfisSelecionados);
				filtros.put("parametro01", idUsuario);
				filtros.put("entidadeId", "usuário");
			}
			else {
				
			}
			idUsuario = null;
		}
	}

	@PostConstruct
	public void init() {
		perfis = perfilDAO.listAll();
		tipoUsuarios = tipousuarioDAO.listAll();
	}
	
	@Override
	public String getTitulo() {
		return "Cadastro de usuário";
	}

	@Override
	public String getLinkBreadCrumb() {
		if(filtros.get("parametro01") != null) {
			return "/pages/usuario/cadastro-usuario.jsf?id=" + filtros.get("parametro01");
		}
		return "/pages/usuario/cadastro-usuario.jsf";
	}
	
	@Override
	public String caminhoCancelar() {
		return "/pages/usuario/lista-usuario?faces-redirect=true";
	}

	// Nao retorna string, pois nao tera necessidade de voltar para pagina com a
	// lista de orcamentos
	public void salvar() {
		for (int i = 0; i < selecionaPerfis.length; i++) {
			perfiSalvaBanco.add(perfilDAO.porTipo(selecionaPerfis[i]));
		}
		if(tipoUsuario.getTipo().equalsIgnoreCase("Administrador")) {
			usuario.setTipoUsuarios(tipoUsuarios);
		}
		else {
			tipoUsuariosSalvar.add(tipoUsuario);
			usuario.setTipoUsuarios(tipoUsuariosSalvar);
		}
		
		if(usuario.getId() != null) {
			mensagem = usuarioDAO.alterar(usuario, perfiSalvaBanco);
		}
		else {
			//se senha for nula ou em branco, a senha ficará igual o login (padrão)
			if(usuario.getSenha() == null || "".equals(usuario.getSenha())) {
				usuario.setSenha(usuario.getLogin());
			}
			mensagem = usuarioDAO.salvar(usuario, perfiSalvaBanco);
		}
		
		if(mensagem.equalsIgnoreCase("")) {
			
			FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoSalvar(), "");
			selecionaPerfis = null;
			perfiSalvaBanco.removeAll(perfiSalvaBanco);
			usuario = new Usuario();
			tipoUsuario = new TipoUsuario();
		}
		else {
			FacesUtil.addWarnMessage(mensagem, "");
		}
	}
	
	public void salvarSenha() {
		Usuario us = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
		if(us.getId() == usuario.getId()) {
			mensagem = usuarioDAO.alterar(usuario, listaPerfisSelecionado);
			if(mensagem.equalsIgnoreCase("")) {
				
				FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoSalvar(), "");
				selecionaPerfis = null;
				perfiSalvaBanco.removeAll(perfiSalvaBanco);
				usuario = new Usuario();
				tipoUsuario = new TipoUsuario();
			}
			else {
				FacesUtil.addWarnMessage(mensagem, "");
			}
		}
		else {
			FacesUtil.addWarnMessage("Sem permissão para fazer a alteração!", "");
		}
	}
	
	public boolean contemNoArray(String tipoPerfil) {
		for (String perfil : selecionaPerfis) {
			if(perfil.equals(tipoPerfil)) {
				return true;
			}
		}
		return false;
	}
	
	public void selecionarUsuario(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
			context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/usuario/cadastro-usuario.jsf?id="+idEntidade+"&entidadeApareceSenha=false");
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}
	
	public void selecionarUsuarioAlterarSenha(ActionEvent event) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			UIParameter parameter = (UIParameter) event.getComponent().findComponent("entidadeId");
			Long idEntidade = Long.parseLong(parameter.getValue().toString());
				context.getExternalContext().redirect(NomeAplicacao.nome + "/pages/usuario/altera-senha.jsf?id="
						+idEntidade);
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
	}

	public void excluir() {
		usuarioDAO.excluir(usuario);
		if(usuarioDAO.isControle()) {
			selecionaPerfis = null;
			perfiSalvaBanco.removeAll(perfiSalvaBanco);
			usuario = new Usuario();
			tipoUsuario = new TipoUsuario();
		}
	}

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public List<Perfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<TipoUsuario> getTipoUsuarios() {
		return tipoUsuarios;
	}

	public void setTipoUsuarios(List<TipoUsuario> tipoUsuarios) {
		this.tipoUsuarios = tipoUsuarios;
	}

	public Boolean getApareceSenha() {
		return apareceSenha;
	}

	public void setApareceSenha(Boolean apareceSenha) {
		this.apareceSenha = apareceSenha;
	}
	public String[] getSelecionaPerfis() {
		return selecionaPerfis;
	}

	public void setSelecionaPerfis(String[] selecionaPerfis) {
		this.selecionaPerfis = selecionaPerfis;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}
}
