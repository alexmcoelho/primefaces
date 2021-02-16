package com.alex.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/*Essa classe não será persistida no banco será utilizada apenas no cadastro/alteração de faturas*/
public class ClasseAuxiliarDetalhesFaturas implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private BigDecimal valorTotal;
	
	private BigDecimal valorTotalCobrado;
	
	private BigDecimal valorPago;
	
	private BigDecimal valorRestante;
	
	private StringBuilder txtValorCorrespondente;
	
	private StringBuilder txtNomeCliente;

	public ClasseAuxiliarDetalhesFaturas() {
		super();
		txtValorCorrespondente = new StringBuilder();
		txtNomeCliente = new StringBuilder();
		txtValorCorrespondente.append("Valores correspondentes a ");
		txtNomeCliente.append("Cliente: ");
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public BigDecimal getValorTotalCobrado() {
		return valorTotalCobrado;
	}

	public void setValorTotalCobrado(BigDecimal valorTotalCobrado) {
		this.valorTotalCobrado = valorTotalCobrado;
	}

	public BigDecimal getValorPago() {
		return valorPago;
	}

	public void setValorPago(BigDecimal valorPago) {
		this.valorPago = valorPago;
	}

	public BigDecimal getValorRestante() {
		return valorRestante;
	}

	public void setValorRestante(BigDecimal valorRestante) {
		this.valorRestante = valorRestante;
	}

	public StringBuilder getTxtValorCorrespondente() {
		return txtValorCorrespondente;
	}

	public void setTxtValorCorrespondente(StringBuilder txtValorCorrespondente) {
		this.txtValorCorrespondente = txtValorCorrespondente;
	}

	public StringBuilder getTxtNomeCliente() {
		return txtNomeCliente;
	}

	public void setTxtNomeCliente(StringBuilder txtNomeCliente) {
		this.txtNomeCliente = txtNomeCliente;
	}
	
	public void iniciaObj() {
		if(txtValorCorrespondente != null && txtValorCorrespondente.length() > 0) {
			txtValorCorrespondente.setLength(0);
			txtValorCorrespondente.append("Valores correspondentes a ");
		}
		if(txtNomeCliente != null && txtNomeCliente.length() > 0) {
			txtNomeCliente.setLength(0);
			txtNomeCliente.append("Cliente: ");
		}
		valorTotal = BigDecimal.ZERO;
		valorTotalCobrado = BigDecimal.ZERO;
		valorPago = BigDecimal.ZERO;
		valorRestante = BigDecimal.ZERO;
	}

}
