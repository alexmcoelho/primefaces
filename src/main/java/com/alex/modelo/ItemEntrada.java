package com.alex.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.alex.modelo.validacao.MinIntValid;

@Entity
@Table(name="item_entrada")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ItemEntrada implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entrada_de_produtos_id", foreignKey = @ForeignKey(name = "FK_ITEM_ENTRADA_PARA_ENTRADA_DE_PRODUTOS"))
	private EntradaDeProdutos entradaDeProdutos;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne()
	@JoinColumn(name = "produto_id", foreignKey = @ForeignKey(name = "FK_ITEM_ENTRADA_PARA_PRODUTO"))
	private Produto produto;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@MinIntValid(min = 1)
	private int quant;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@DecimalMin(value = "0.00", inclusive = true)
	@Column(precision = 14, scale = 2, name = "valor_unit")
    private BigDecimal valorUnit;
	
	private String imei;
	
	@Transient
	private BigDecimal subTotal = new BigDecimal("0.00");
	
	@Transient
	private String descricaoCompletaProduto;

	public BigDecimal getSubTotal() {
		this.subTotal = new BigDecimal(this.quant);
		this.subTotal = this.valorUnit.multiply(this.subTotal, MathContext.DECIMAL128).setScale(2,
				RoundingMode.HALF_EVEN);
		return subTotal;
	}

	public void setSubTotal(BigDecimal subTotal) {
		this.subTotal = subTotal;
	}
	
	public EntradaDeProdutos getEntradaDeProdutos() {
		return entradaDeProdutos;
	}

	public void setEntradaDeProdutos(EntradaDeProdutos entradaDeProdutos) {
		this.entradaDeProdutos = entradaDeProdutos;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

	public int getQuant() {
		return quant;
	}

	public void setQuant(int quant) {
		this.quant = quant;
	}

	public BigDecimal getValorUnit() {
		return valorUnit;
	}

	public void setValorUnit(BigDecimal valorUnit) {
		this.valorUnit = valorUnit;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getDescricaoCompletaProduto() {
		if(imei != null && imei.length() > 0 && produto != null) {
			this.descricaoCompletaProduto = produto.getDescricao() + " - " + imei;
		}
		else if (produto != null) {
			this.descricaoCompletaProduto = produto.getDescricao();
		}
		return descricaoCompletaProduto;
	}

	public void setDescricaoCompletaProduto(String descricaoCompletaProduto) {
		this.descricaoCompletaProduto = descricaoCompletaProduto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((produto == null) ? 0 : produto.hashCode());
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
		ItemEntrada other = (ItemEntrada) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (produto == null) {
			if (other.produto != null)
				return false;
		} else if (!produto.equals(other.produto))
			return false;
		return true;
	}

	
}