package com.alex.modelo;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.validator.constraints.NotEmpty;

import com.alex.modelo.validacao.TamanhoStringValid;

@Entity
@Table(name="fornecedor", uniqueConstraints = {
@UniqueConstraint(columnNames = "email", name = "UK_NOME_FORNECEDOR"),
@UniqueConstraint(columnNames = "email", name = "UK_EMAIL_FORNECEDOR")
})
public class Fornecedor implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty()
	@TamanhoStringValid(max = 150, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String nome;
	
	@NotEmpty()
	@TamanhoStringValid(max = 50, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String email;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="fornecedor_telefone", joinColumns=
    {@JoinColumn(name="fornecedor_id")}, inverseJoinColumns=
      {@JoinColumn(name="telefone_id")})
	private List<Telefone> telefones = new ArrayList<Telefone>();
	
	@OneToMany(mappedBy = "fornecedor")
	private List<EntradaDeProdutos> entradaDeProdutos;
	
	@Transient
	private String codDescricao;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<Telefone> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

	public List<EntradaDeProdutos> getEntradaDeProdutos() {
		return entradaDeProdutos;
	}

	public void setEntradaDeProdutos(List<EntradaDeProdutos> entradaDeProdutos) {
		this.entradaDeProdutos = entradaDeProdutos;
	}
	
	public boolean isInclusao(){
		return id == null ? true : false;
	}
	
	public boolean isEdicao(){
		return !isInclusao();
	}
	
	public String getCodDescricao() {
		if(this.id != null) {
			codDescricao = new DecimalFormat("000000").format(this.id) + " - " + this.nome;
		}
		return codDescricao;
	}

	public void setCodDescricao(String codDescricao) {
		this.codDescricao = codDescricao;
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
		Fornecedor other = (Fornecedor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
