package com.alex.util;

import javax.faces.context.FacesContext;

public class NomeAplicacao {
	
	public static String nome = retornaNome();
	//public static String nome = "/gestaovidal";
	
	public static String retornaNome() {
		if(nome == null) {
			if(Dominio.retornaDominio(FacesContext.getCurrentInstance()).contains("vidalcell")) {
				return "/gestaovidal";
			}
			else if(Dominio.retornaDominio(FacesContext.getCurrentInstance()).contains(".tk")) {
				return "/gestao";
			}
			return "/gestaocomsistema";
		}
		return nome;
	}
}
