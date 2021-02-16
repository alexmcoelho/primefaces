package com.alex.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

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
import javax.validation.constraints.NotNull;

import com.alex.modelo.enums.FormaPagamento;

@Entity
@Table(name="saida_de_produtos")
public class SaidaDeProdutos implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", foreignKey = @ForeignKey(name = "FK_SAIDA_DE_PRODUTOS_PARA_CLIENTE"))
	private Cliente cliente;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", foreignKey = @ForeignKey(name = "FK_SAIDA_DE_PRODUTOS_PARA_USUARIO"))
	private Usuario usuario;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@Temporal(value = TemporalType.DATE)
	private Date data;
	
	@Transient
	private BigDecimal valor = new BigDecimal("0.00");
	
	@Column(name = "forma_pagamento")
	private Integer formaPagamento;
	
	@OneToMany(mappedBy = "saidaDeProdutos")
	private List<DetalhesFatura> detalhesFaturas;
	
	@OneToMany(mappedBy = "saidaDeProdutos", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemProdSaida> itemProdSaidas;
	
	@Transient
	private boolean garantia;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
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

	public List<DetalhesFatura> getDetalhesFaturas() {
		return detalhesFaturas;
	}

	public void setDetalhesFaturas(List<DetalhesFatura> detalhesFaturas) {
		this.detalhesFaturas = detalhesFaturas;
	}

	public List<ItemProdSaida> getItemProdSaidas() {
		return itemProdSaidas;
	}

	public void setItemProdSaidas(List<ItemProdSaida> itemProdSaidas) {
		this.itemProdSaidas = itemProdSaidas;
	}
	
	public BigDecimal getValor() {
		Stream<BigDecimal> stream1 = itemProdSaidas.stream()
				.map(c -> new BigDecimal(c.getQuant())
						.multiply(c.getValorUnit(), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_EVEN));
		DoubleStream doubleStream = stream1.mapToDouble(BigDecimal::doubleValue);
		return new BigDecimal(doubleStream.sum());
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
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
		SaidaDeProdutos other = (SaidaDeProdutos) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public void addItensProdutos(ItemProdSaida i) {
		if(this.itemProdSaidas == null) {
			this.itemProdSaidas = new ArrayList<ItemProdSaida>();
		}
		this.itemProdSaidas.add(i);
	}
}