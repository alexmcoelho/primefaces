package com.alex.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.NotEmpty;

import com.alex.modelo.validacao.TamanhoStringValid;

@Entity
@Table(name="produto", uniqueConstraints = {
@UniqueConstraint(columnNames = "descricao", name = "UK_DESCRICAO"),
@UniqueConstraint(columnNames = "descricao_lower_case", name = "UK_DESCRICAO_LOWER_CASE")
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Produto implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty()
	@TamanhoStringValid(max = 150, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String descricao;
	
	@Column(unique = true, name="descricao_lower_case")
	private String descricaoLowerCase;
	
	@Transient
	private int quant = 0;
	
	@DecimalMin(value = "0", inclusive = true)
	@Column(precision = 14, scale = 2, name = "percentual_lucro")
	private BigDecimal percentualLucro;
	
	@Transient
	private BigDecimal valorSugerido = BigDecimal.ZERO;
	
	@DecimalMin(value = "0", inclusive = true)
	@Column(precision = 14, scale = 2, name = "valor_informado")
	private BigDecimal valorInformado; //valor informado pelo usuário
	
	@Transient
	private BigDecimal valorPago = BigDecimal.ZERO;
	
	@Transient
	private BigDecimal lucro = BigDecimal.ZERO;
	
	@Transient
	private boolean incluidaNoPedido = false;
	
	private String imei;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne()
	@JoinColumn(name = "categoria_id", foreignKey = @ForeignKey(name = "FK_PRODUTO_PARA_CATEGORIA"))
	private Categoria categoria;
	
	@OneToMany(mappedBy = "produto")
	private List<ItemProdSaida> itemProdSaidas;
	
	@OneToMany(mappedBy = "produto")
	private List<ItemProduto> itemProdutos;
	
	@OneToMany(mappedBy = "produto")
	private List<ItemEntrada> itemEntradas;
	
	@Transient
	private String codDescricao;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricaoLowerCase() {
		return descricaoLowerCase;
	}

	public void setDescricaoLowerCase(String descricaoLowerCase) {
		this.descricaoLowerCase = descricaoLowerCase;
	}

	public int getQuant() {
		return quant;
	}

	public void setQuant(int quant) {
		this.quant = quant;
	}

	public BigDecimal getPercentualLucro() {
		return percentualLucro;
	}

	public void setPercentualLucro(BigDecimal percentualLucro) {
		this.percentualLucro = percentualLucro;
	}

	public BigDecimal getValorSugerido() {
		return valorSugerido;
	}

	public void setValorSugerido(BigDecimal valorSugerido) {
		this.valorSugerido = valorSugerido;
	}

	public List<ItemProdSaida> getItemProdSaidas() {
		return itemProdSaidas;
	}

	public void setItemProdSaidas(List<ItemProdSaida> itemProdSaidas) {
		this.itemProdSaidas = itemProdSaidas;
	}

	public List<ItemProduto> getItemProdutos() {
		return itemProdutos;
	}

	public void setItemProdutos(List<ItemProduto> itemProdutos) {
		this.itemProdutos = itemProdutos;
	}

	public List<ItemEntrada> getItemEntradas() {
		return itemEntradas;
	}

	public void setItemEntradas(List<ItemEntrada> itemEntradas) {
		this.itemEntradas = itemEntradas;
	}
	
	public boolean isIncluidaNoPedido() {
		return incluidaNoPedido;
	}

	public void setIncluidaNoPedido(boolean incluidaNoPedido) {
		this.incluidaNoPedido = incluidaNoPedido;
	}

	public BigDecimal getValorPago() {
		return valorPago;
	}

	public void setValorPago(BigDecimal valorPago) {
		this.valorPago = valorPago;
	}

	public BigDecimal getLucro() {
		return lucro;
	}

	public void setLucro(BigDecimal lucro) {
		this.lucro = lucro;
	}

	public BigDecimal getValorInformado() {
		return valorInformado;
	}

	public void setValorInformado(BigDecimal valorInformado) {
		this.valorInformado = valorInformado;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public String getCodDescricao() {
		if(this.id != null) {
			codDescricao = new DecimalFormat("000000").format(this.id) + " - " + this.descricao;
		}
		return codDescricao;
	}

	public void setCodDescricao(String codDescricao) {
		this.codDescricao = codDescricao;
	}

	public boolean isInclusao(){
		return id == null ? true : false;
	}
	
	public boolean isEdicao(){
		return !isInclusao();
	}
	
	@PrePersist @PreUpdate private void prepare(){
        this.descricaoLowerCase = descricao == null ? null : descricao.toLowerCase();
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
		Produto other = (Produto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}