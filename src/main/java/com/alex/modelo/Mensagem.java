package com.alex.modelo;

import java.io.Serializable;

//modelo que será usado para as mensagens que não conseguem ser encontradados no ValidationMessage
//como por exemplo quando é usado <f:validateDoubleRange minimum="0.01" />
public class Mensagem implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String chave;
	private String valor;
	
	public Mensagem() {
		super();
	}

	public Mensagem(String chave, String valor) {
		super();
		this.chave = chave;
		this.valor = valor;
	}
	
	public String getChave() {
		return chave;
	}
	public void setChave(String chave) {
		this.chave = chave;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chave == null) ? 0 : chave.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mensagem other = (Mensagem) obj;
		if (chave == null) {
			if (other.chave != null)
				return false;
		} else if (!chave.equals(other.chave))
			return false;
		return true;
	}
	
}
