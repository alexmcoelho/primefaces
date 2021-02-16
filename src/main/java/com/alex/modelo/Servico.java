package com.alex.modelo;

import java.io.Serializable;
import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.alex.modelo.enums.MaoDeObra;
import com.alex.modelo.validacao.TamanhoStringValid;

@Entity
@Table(name="servico", uniqueConstraints = {
@UniqueConstraint(columnNames = "descricao", name = "UK_DESCRICAO_SERVICO")
})
public class Servico implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty()
	@TamanhoStringValid(max = 100, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String descricao;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@Column(name = "mao_de_obra")
	private Integer maoDeObra;
	
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
	
	public String getCodDescricao() {
		if(this.id != null) {
			codDescricao = new DecimalFormat("000000").format(this.id) + " - " + this.descricao;
		}
		return codDescricao;
	}

	public void setCodDescricao(String codDescricao) {
		this.codDescricao = codDescricao;
	}

	public MaoDeObra getMaoDeObra() {
		return MaoDeObra.toEnum(maoDeObra);
	}

	public void setMaoDeObra(MaoDeObra maoDeObra) {
		this.maoDeObra = maoDeObra.getCod();
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
		Servico other = (Servico) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}