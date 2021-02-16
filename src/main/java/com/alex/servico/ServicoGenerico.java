package com.alex.servico;

import java.io.Serializable;

public class ServicoGenerico implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static String montaPesquisaInteligente(String pesquisa, boolean variosFiltros) {
		if(variosFiltros && pesquisa != null && pesquisa.contains(" ")) {
			if(pesquisa.substring(pesquisa.length()-1, pesquisa.length()).contains(" ")) {
				pesquisa = pesquisa.substring(0, pesquisa.length()-1);
				pesquisa = pesquisa + "%";
			}
			else {
				//pesquisa = "%" + pesquisa + "%";
				pesquisa = pesquisa + "%";
			}
			pesquisa = pesquisa.replace(" ", "%%");
			return pesquisa.toLowerCase();
		}
		return pesquisa != null ? pesquisa.toLowerCase() + "%" : null;
	}
	
	//pega string do tipo 000123 - Alto falante Auricular Iphone 5S e pega somente 000123, para depois transformar em Long
	public static Long retornaId(String txt) {
		int pos = txt.indexOf("-");
		return Long.parseLong(txt.substring(0, pos-1));
	}

}
