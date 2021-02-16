package com.alex.modelo;

import java.io.Serializable;
import java.util.List;

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
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.alex.modelo.validacao.TamanhoStringValid;

@Entity
@Table(name="modelo", uniqueConstraints = {
@UniqueConstraint(columnNames = "modelo", name = "UK_MODELO")
})
public class Modelo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty()
	@TamanhoStringValid(max = 50, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String modelo;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "aparelho_id", foreignKey = @ForeignKey(name = "FK_MODELO_PARA_APARELHO"))
	private Aparelho aparelho;
	
	@OneToMany(mappedBy = "modelo")
	private List<OrdemDeServico> ordemDeServicos;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getModelo() {
		return modelo;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public Aparelho getAparelho() {
		return aparelho;
	}

	public void setAparelho(Aparelho aparelho) {
		this.aparelho = aparelho;
	}

	public List<OrdemDeServico> getOrdemDeServicos() {
		return ordemDeServicos;
	}

	public void setOrdemDeServicos(List<OrdemDeServico> ordemDeServicos) {
		this.ordemDeServicos = ordemDeServicos;
	}
	
	public boolean isInclusao(){
		return id == null ? true : false;
	}
	
	public boolean isEdicao(){
		return !isInclusao();
	}
	
	public void newObj() {
		this.id = null;
		this.modelo = null;
		this.aparelho = null;
		this.ordemDeServicos = null;
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
		Modelo other = (Modelo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}