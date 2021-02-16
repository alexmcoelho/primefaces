package com.alex.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ClasseAuxiliarTotaisFaturas implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private BigDecimal recebidas = BigDecimal.ZERO;
	private BigDecimal receber = BigDecimal.ZERO;
	private BigDecimal vencidas = BigDecimal.ZERO;
	private BigDecimal total = BigDecimal.ZERO;
	
	public ClasseAuxiliarTotaisFaturas() {
	}
	
	public ClasseAuxiliarTotaisFaturas(BigDecimal recebidas, BigDecimal receber, BigDecimal vencidas,
			BigDecimal total) {
		super();
		this.recebidas = recebidas;
		this.receber = receber;
		this.vencidas = vencidas;
		this.total = total;
	}
	public BigDecimal getRecebidas() {
		return recebidas;
	}
	public void setRecebidas(BigDecimal recebidas) {
		this.recebidas = recebidas;
	}
	public BigDecimal getReceber() {
		return receber;
	}
	public void setReceber(BigDecimal receber) {
		this.receber = receber;
	}
	public BigDecimal getVencidas() {
		return vencidas;
	}
	public void setVencidas(BigDecimal vencidas) {
		this.vencidas = vencidas;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	
	public void newObj() {
		recebidas = BigDecimal.ZERO;
		receber = BigDecimal.ZERO;
		vencidas = BigDecimal.ZERO;
		total = BigDecimal.ZERO;
	}

}
