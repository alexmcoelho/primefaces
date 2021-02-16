package com.alex.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import com.alex.modelo.validacao.TamanhoStringValid;

@Entity
@Table(name="detalhes_fatura")
public class DetalhesFatura implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@TamanhoStringValid(max = 50, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String descricao;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@Temporal(value = TemporalType.DATE)
	@Column(name = "data_vencimento")
	private Date dataVencimento;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@DecimalMin(value="0.00", inclusive = true)
	@Column(precision = 14, scale = 2, name = "valor_parcela")
    private BigDecimal valorParcela;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	private boolean baixada = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ordem_de_servico_id", foreignKey = @ForeignKey(name = "FK_DETALHES_FATURA_PARA_ORDEM_DE_SERVICO"))
	private OrdemDeServico ordemDeServico;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "saida_de_produtos_id", foreignKey = @ForeignKey(name = "FK_DETALHES_FATURA_PARA_SAIDA_DE_PRODUTOS"))
	private SaidaDeProdutos saidaDeProdutos;

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

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public BigDecimal getValorParcela() {
		return valorParcela;
	}

	public void setValorParcela(BigDecimal valorParcela) {
		this.valorParcela = valorParcela;
	}

	public OrdemDeServico getOrdemDeServico() {
		return ordemDeServico;
	}

	public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
		this.ordemDeServico = ordemDeServico;
	}

	public SaidaDeProdutos getSaidaDeProdutos() {
		return saidaDeProdutos;
	}

	public void setSaidaDeProdutos(SaidaDeProdutos saidaDeProdutos) {
		this.saidaDeProdutos = saidaDeProdutos;
	}

	public boolean isBaixada() {
		return baixada;
	}

	public void setBaixada(boolean baixada) {
		this.baixada = baixada;
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
		DetalhesFatura other = (DetalhesFatura) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
