package com.alex.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import com.alex.modelo.enums.FormaPagamento;
import com.alex.modelo.validacao.TamanhoStringValid;

@Entity
@Table(name="entrada_de_produtos", uniqueConstraints = {
@UniqueConstraint(columnNames = "codigo_nota", name = "UK_CODIGO_NOTA")
})
public class EntradaDeProdutos implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "codigo_nota")
	@TamanhoStringValid(max = 50, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String codigoNota;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fornecedor_id", foreignKey = @ForeignKey(name = "FK_ENTRADA_DE_PRODUTOS_PARA_FORNECEDOR"))
	private Fornecedor fornecedor;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@Temporal(value = TemporalType.DATE)
	private Date data;
	
	@Transient
    private BigDecimal valor = new BigDecimal("0.00");
	
    @Column(name = "forma_pagamento")
	private Integer formaPagamento;
	
	@OneToMany(mappedBy = "entradaDeProdutos", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemEntrada> itemEntradas;

	@OneToMany(mappedBy = "entradaDeProdutos")
	private List<DetalhesContaAPagar> detalhesContasAPagar;
	
	public List<DetalhesContaAPagar> getDetalhesContasAPagar() {
		return detalhesContasAPagar;
	}

	public void setDetalhesContasAPagar(List<DetalhesContaAPagar> detalhesContasAPagar) {
		this.detalhesContasAPagar = detalhesContasAPagar;
	}

	public List<ItemEntrada> getItemEntradas() {
		return itemEntradas;
	}

	public void setItemEntradas(List<ItemEntrada> itemEntradas) {
		this.itemEntradas = itemEntradas;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Fornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(Fornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	public String getCodigoNota() {
		return codigoNota;
	}

	public void setCodigoNota(String codigoNota) {
		this.codigoNota = codigoNota;
	}

	public FormaPagamento getFormaPagamento() {
		return FormaPagamento.toEnum(formaPagamento);
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento.getCod();
	}

	public boolean isInclusao(){
		return id == null ? true : false;
	}
	
	public boolean isEdicao(){
		return !isInclusao();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		EntradaDeProdutos other = (EntradaDeProdutos) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
