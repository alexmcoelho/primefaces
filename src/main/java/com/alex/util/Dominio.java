package com.alex.util;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class Dominio implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String dominio;
	
	private static HttpServletRequest req = null;
	
	public static String contemDominio = "vidalcell";

	public String getDominio() {
		return dominio;
	}

	public void setDominio(String dominio) {
		this.dominio = dominio;
		if(dominio != null && dominio.contains(NomeAplicacao.nome)) {
			this.dominio = dominio.split(NomeAplicacao.nome)[0] + NomeAplicacao.nome;
		}
	}
	
	public static String retornaDominio(FacesContext fc) {
		req = (HttpServletRequest) fc.getExternalContext().getRequest();
		// Ex: http://localhost:8080/osituporanga
		return req.getRequestURL().toString();
	}
	
}
