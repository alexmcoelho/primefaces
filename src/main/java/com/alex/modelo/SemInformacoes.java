package com.alex.modelo;

import java.io.Serializable;

public class SemInformacoes implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String titulo;
	
	private String subtitulo;
	
	private String mensagem;
	
	private boolean situacao;

	public SemInformacoes(String titulo, String subtitulo, String mensagem, boolean situacao) {
		super();
		this.titulo = titulo;
		this.subtitulo = subtitulo;
		this.mensagem = mensagem;
		this.situacao = situacao;
	}

	public SemInformacoes() {
	
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getSubtitulo() {
		return subtitulo;
	}

	public void setSubtitulo(String subtitulo) {
		this.subtitulo = subtitulo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public boolean isSituacao() {
		return situacao;
	}

	public void setSituacao(boolean situacao) {
		this.situacao = situacao;
	}
}
