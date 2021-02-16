package com.alex.util;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//toda vez que tiver essa url o webfilter será chamado
@WebFilter(urlPatterns = "/*")
public class ControleAcesso implements Filter, Serializable {

	private static final long serialVersionUID = 2284408363911121776L;

	/*@Inject
	private TipoUsuario tipoUsuario;

	@Inject
	private UsuarioDAO usuarioDAO;*/

	//desativado
	public ControleAcesso() {
		//listaRotasUsuarioOperario.add("/pages/ordemservico");
		//listaRotasUsuarioOperario.add("/pages/saida");
	}

	//private List<String> listaRotasUsuarioOperario = new ArrayList<>();

	//private boolean sendRedirect = false;
	private String url = "";
	private HttpServletResponse res;
	private HttpServletRequest req;
	private String[] urlCortada;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		res = (HttpServletResponse) response;
		req = (HttpServletRequest) request;
		//Usuario us = (Usuario) req.getSession().getValue("usuario");
		url = req.getRequestURL().toString();
		
		if(url.contains(".xhtml")) {
			urlCortada = url.split(".xhtml");
			url = urlCortada[0] + ".jsf";
			res.sendRedirect(url);
			//sendRedirect = true;
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/*public boolean contemNaLista(String rota) {
		if (listaRotasUsuarioOperario.contains(rota)) {
			return true;
		}
		return false;
	}*/

	/*public boolean temPermissao(Usuario us, String[] urlCortada) {
		String url = null;
		
		String[] urlSegundoCorte = urlCortada[1].split("/");
		if (urlSegundoCorte.length > 2) {
			url = "/" + urlSegundoCorte[1] + "/" + urlSegundoCorte[2];
		}
		
		tipoUsuario = usuarioDAO.porID(us != null ? us.getId() : null).getTipoUsuarios().get(0);
		
		if (tipoUsuario!=null &&tipoUsuario.getTipo().equalsIgnoreCase("Operador") && contemNaLista(url)) {
			return true;
		} else if (tipoUsuario!=null && !tipoUsuario.getTipo().equalsIgnoreCase("Operador")) {
			return true;
		}
		return false;
	}*/

}
