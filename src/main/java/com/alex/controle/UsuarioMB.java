package com.alex.controle;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.omnifaces.util.Faces;

import com.alex.DAO.UsuarioDAO;
import com.alex.modelo.DataAtualLocale;
import com.alex.modelo.Usuario;
import com.alex.servico.ForcaBrutaControl;
import com.alex.util.Dominio;
import com.alex.util.ManipuladorArquivo;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class UsuarioMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private UsuarioDAO usuarioDAO;

	@Inject
	private Usuario usuario;

	private boolean remember = false;

	private long tempoMilisegundos = 1200000;
	private int numeroTentativas = 3;
	private HttpServletRequest request;
	private String enderecoIp;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isRemember() {
		return remember;
	}

	public void setRemember(boolean remember) {
		this.remember = remember;
	}

	public String iniciarSessao() {
		String redirecionamento = null;
		Usuario us = null;
		String texto;

		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		request = (HttpServletRequest) externalContext.getRequest();

		ForcaBrutaControl control = null;

		if (externalContext.getSessionMap().get("ForcaBrutaControl") == null) {
			control = new ForcaBrutaControl(tempoMilisegundos, numeroTentativas);
			externalContext.getSessionMap().put("ForcaBrutaControl", control);
		} else {
			control = (ForcaBrutaControl) externalContext.getSessionMap().get("ForcaBrutaControl");
		}

		enderecoIp = request.getHeader("X-Real-IP") != null ? request.getHeader("X-Real-IP") : request.getRemoteHost();

		/* INICIO - salvando log de ips que tem usar essa função */
		texto = ManipuladorArquivo.lerArquivo(
				FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/configuracoes/log.csv"));
		if (Dominio.retornaDominio(FacesContext.getCurrentInstance()).contains(Dominio.contemDominio)) {
			texto += "vidalcell;login;";
		} else {
			texto += "elitte;login;";
		}
		texto += enderecoIp + ";" + DataAtualLocale.data() + ";";
		texto += "login:" + usuario.getLogin() + "-senha:" + usuario.getSenha();
		ManipuladorArquivo.escritor(
				FacesContext.getCurrentInstance().getExternalContext().getRealPath("/resources/configuracoes/log.csv"),
				texto);
		/* FIM - salvando log de ips que tem usar essa função */

		if (!control.podeLoggar(enderecoIp)) {
			FacesContext.getCurrentInstance()
					.addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN,
									"Por motivos de segurança seu IP está bloqueado, aguarde "
											+ control.minutosRestantes(enderecoIp) + " minutos e tente novamente.",
									""));
			return "";
		}

		try {
			us = usuarioDAO.iniciarSessao(usuario);
			if (us != null) {
				// ARMAZENAR SESSÃO JSF
				redirecionamento = "/index?faces-redirect=true";
				us.setSenha(null);
				FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", us);
				// ARMAZENA NOS COOKIES
				salvarCookie("id", "" + us.getId(), -1);
				salvarCookie("nome", us.getNome(), -1);
				// INICIO - salva login nos cookies
				/*
				 * FacesContext facesContext = FacesContext.getCurrentInstance();
				 * HttpServletResponse response = (HttpServletResponse)
				 * facesContext.getExternalContext().getResponse(); if(this.remember) { Cookie
				 * ckUser = new Cookie("user", us.getUsuario()); ckUser.setMaxAge(3600);
				 * response.addCookie(ckUser); Cookie ckUSenha = new Cookie("senha",
				 * us.getSenha()); ckUSenha.setMaxAge(3600); response.addCookie(ckUSenha); }
				 */
				// FIM - salva login nos cookies
			} else {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Usuário ou senha inválido(s)", ""));
				control.registraFalha(enderecoIp);
				return null;
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "Erro ao tentar realizar login", ""));
			control.registraFalha(enderecoIp);
			return null;
		}
		return redirecionamento;
	}

	public void verificaLogin() {
		Usuario us = checkCookie();
		if (us != null) {
			if (usuarioDAO.iniciarSessao(us) != null) {
				usuario = us;
				redirect("index.jsf");
			} else {
				redirect("login.jsf");
			}
		}
	}

	public static void salvarCookie(String nome, String valor, int i) {
		Faces.addResponseCookie(nome, valor, i);
	}

	public static void removerCookie(String nome, String caminhoHTTP) {
		Faces.removeResponseCookie(nome, caminhoHTTP);
	}

	public void redirect(String page) {
		try {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.getExternalContext().redirect(page);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public Usuario checkCookie() {
		Usuario usuario = null;
		// guardar nos cookies
		FacesContext facesContext = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		Cookie[] cookies = request.getCookies();
		String user = "";
		String senha = "";
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equalsIgnoreCase("user")) {
					user = cookie.getValue();
				}
				if (cookie.getName().equalsIgnoreCase("senha")) {
					senha = cookie.getValue();
				}
			}
		}
		if (!user.isEmpty() && !senha.isEmpty()) {
			usuario = new Usuario();
			usuario.setLogin(user);
			usuario.setSenha(senha);
		}
		return usuario;
	}

	public Usuario mostrarUsuarioLogado() {
		FacesContext context = FacesContext.getCurrentInstance();
		Usuario us = (Usuario) context.getExternalContext().getSessionMap().get("usuario");
		return us;
	}

	public String mostraNome() {
		if (mostrarUsuarioLogado() != null) {
			String nome[] = mostrarUsuarioLogado().getNome().split(" ");
			return nome[0];
		}
		return null;
	}

	public String mostrarIniciaisNome() {
		if (mostraNome() != null) {
			return mostraNome().substring(0, 1);
		}
		return null;
	}

	public String encerrarSessao() {
		String controle;
		FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

		// INICIO - tira dos cookies
		
		controle = Dominio.retornaDominio(FacesContext.getCurrentInstance());
		if(controle.contains("localhost")) {
			String[] vetorUrl = controle.split(NomeAplicacao.nome);
			vetorUrl[0] = vetorUrl[0].replaceAll("\\d", "");
			vetorUrl[0] = vetorUrl[0].replace(":", "");
			controle = vetorUrl[0];
		}
		
		if(Faces.getRequestCookie("id") != null && Faces.getRequestCookie("nome") != null) {
			removerCookie("id", controle);
			removerCookie("nome", controle);
			removerCookie("id", "www" + controle);
			removerCookie("nome", "www" + controle);
		}
		
		controle = "/login?faces-redirect=true";
		return controle;
	}

}
