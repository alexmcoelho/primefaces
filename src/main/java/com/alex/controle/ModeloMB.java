package com.alex.controle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.omnifaces.util.Faces;

import com.alex.DAO.UsuarioDAO;
import com.alex.modelo.TipoUsuario;
import com.alex.modelo.Usuario;
import com.alex.util.NomeAplicacao;

@Named
@ViewScoped
public class ModeloMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private Usuario usuario;

	@Inject
	private Usuario us;

	@Inject
	private TipoUsuario tipoUsuario;

	@Inject
	private UsuarioDAO usuarioDAO;

	private boolean mensagem;

	private String url;

	private String[] urlCortada;

	private FacesContext context = FacesContext.getCurrentInstance();

	private HttpServletRequest request;

	// private Map<String, Object> requestCookieMap;

	@PostConstruct
	public void init() {
		// rotas que todos tem acesso
		listaRotasUsuarioOperario.add("/pages/ordemservico");
		listaRotasUsuarioOperario.add("/pages/saida");
		listaRotasUsuarioOperario.add("/pages/estoque");
		listaRotasUsuarioOperario.add("/pages/produto");
		listaRotasUsuarioOperario.add("/pages/cliente");
		listaRotasUsuarioOperario.add("/pages/modelo");
		listaRotasUsuarioOperario.add("/pages/aparelho");
		listaRotasUsuarioOperario.add("/pages/servico");
		listaRotasUsuarioOperario.add("/pages/fornecedor");
		listaRotasUsuarioOperario.add("/pages/pedido");
		listaRotasUsuarioOperario.add("/pages/categoria");
		// requestCookieMap =
		// FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap();
	}

	private List<String> listaRotasUsuarioOperario = new ArrayList<>();

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isMensagem() {
		return mensagem;
	}

	public void setMensagem(boolean mensagem) {
		this.mensagem = mensagem;
	}

	public void verificarSessao() {
		mensagem = true;
		try {
			if (Faces.getRequestCookie("id") != null && Faces.getRequestCookie("nome") != null) {
				usuarioDAO.getEntidade().setId(Long.parseLong(Faces.getRequestCookie("id")));
				usuarioDAO.getEntidade().setNome(Faces.getRequestCookie("nome"));

			}

			us = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usuario");
			
			if (us == null) {
				us = usuarioDAO.getEntidade() != null && usuarioDAO.getEntidade().getId() != null ? 
						usuarioDAO.porIDExtra(usuarioDAO.getEntidade().getId()) : null;
				if(us != null) {
					us.setSenha(null);
					FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usuario", us);
				}
				
			}

			request = (HttpServletRequest) context.getExternalContext().getRequest();
			url = request.getRequestURI();

			if(url.contains(NomeAplicacao.nome)) {
				urlCortada = url.split(NomeAplicacao.nome);
			}

			if (urlCortada.length <= 2 && us == null) { // se estiver na primeira pagina e não estiver logado
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Por favor! Faça seu login.", ""));
				context.getExternalContext().redirect(NomeAplicacao.nome + "/login.jsf");
			} else {
				String[] urlSegundoCorte = urlCortada[1].split("/");
				url = "/" + urlSegundoCorte[1] + "/" + urlSegundoCorte[2];
				if (us == null || !temPermissao(us, url)) {
					mensagem = false;
				}
				// if acrescentado apenas para o usuário ter acesso ao estoque ->
				if (request.getRequestURI() != null
						&& request.getRequestURI().contains("pages/relatorios/estoque-relatorio")
						|| request.getRequestURI() != null
								&& request.getRequestURI().contains("pages/relatorios/impressao-etiquetas")) {
					mensagem = true;
				}
			}

			if (!mensagem && us != null) {
				context.addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_WARN, "Por favor! Faça seu login.", ""));
				context.getExternalContext().redirect(NomeAplicacao.nome + "/index.jsf");
			} else {
				usuario = us;
			}
		} catch (Exception e) {
			// log para guardar registro de algum erro
		}
		// return retorno;
	}

	public boolean contemNaLista(String rota) {
		if (listaRotasUsuarioOperario.contains(rota)) {
			return true;
		}
		return false;
	}

	public boolean temPermissao(Usuario us, String rota) {

		tipoUsuario = (TipoUsuario) context.getExternalContext().getSessionMap().get("tipoUsuario");
		if (tipoUsuario == null) {
			// a princípio retorna a primeira posição, pois o usuário inicialmente será
			// apenas um Tipo de usuário
			tipoUsuario = us.getTipoUsuarios().get(0);
			context.getExternalContext().getSessionMap().put("tipoUsuario", tipoUsuario);
		}

		if (tipoUsuario.getTipo().equalsIgnoreCase("Operador") && contemNaLista(rota)) {
			return true;
		} else if (!tipoUsuario.getTipo().equalsIgnoreCase("Operador")) {
			return true;
		}
		return false;
	}

}
