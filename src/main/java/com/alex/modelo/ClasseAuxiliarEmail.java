package com.alex.modelo;

import java.io.Serializable;
import java.util.List;

public class ClasseAuxiliarEmail implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String nomeCliente;
	
	private String nomeAparelhoCompleto;
	
	private String situacao;
	
	private StringBuilder defeitoInformado;
	
	private StringBuilder laudoTecnico;
	
	private StringBuilder observacoes;
	
	private List<String> caminhoImagens; //futramente poderia criar uma classe com todos os parâmetros: src, alt, width e height

	public ClasseAuxiliarEmail() {
		super();
		defeitoInformado = new StringBuilder();
		laudoTecnico = new StringBuilder();
		observacoes = new StringBuilder();
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public String getNomeAparelhoCompleto() {
		return nomeAparelhoCompleto;
	}

	public void setNomeAparelhoCompleto(String nomeAparelhoCompleto) {
		this.nomeAparelhoCompleto = nomeAparelhoCompleto;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public StringBuilder getDefeitoInformado() {
		return defeitoInformado;
	}

	public void setDefeitoInformado(StringBuilder defeitoInformado) {
		this.defeitoInformado = defeitoInformado;
	}

	public StringBuilder getLaudoTecnico() {
		return laudoTecnico;
	}

	public void setLaudoTecnico(StringBuilder laudoTecnico) {
		this.laudoTecnico = laudoTecnico;
	}

	public StringBuilder getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(StringBuilder observacoes) {
		this.observacoes = observacoes;
	}

	public List<String> getCaminhoImagens() {
		return caminhoImagens;
	}

	public void setCaminhoImagens(List<String> caminhoImagens) {
		this.caminhoImagens = caminhoImagens;
	}

}
