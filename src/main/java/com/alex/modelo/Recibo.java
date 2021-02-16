package com.alex.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class Recibo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long codigo;
	private String nome;
	private BigDecimal valor;
	private String valorExtenso;
	private String correpondente;
	
	public Recibo(Long codigo, String nome, BigDecimal valor, String valorExtenso, String correpondente) {
		super();
		this.codigo = codigo;
		this.nome = nome;
		this.valor = valor;
		this.valorExtenso = valorExtenso;
		this.correpondente = correpondente;
	}
	
	public Recibo() {
		super();
	}

	public Long getCodigo() {
		return codigo;
	}
	public void setCodigo(Long codigo) {
		this.codigo = codigo;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public String getValorExtenso() {
		return valorExtenso;
	}
	public void setValorExtenso(String valorExtenso) {
		this.valorExtenso = valorExtenso;
	}
	public String getCorrepondente() {
		return correpondente;
	}
	public void setCorrepondente(String correpondente) {
		this.correpondente = correpondente;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
}
