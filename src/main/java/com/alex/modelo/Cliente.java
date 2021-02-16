package com.alex.modelo;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Past;

import org.hibernate.validator.constraints.NotEmpty;

import com.alex.modelo.validacao.CPFECNPJValid;
import com.alex.modelo.validacao.EmailValid;
import com.alex.modelo.validacao.TamanhoStringValid;

@Entity
@Table(name="cliente", uniqueConstraints = {
@UniqueConstraint(columnNames = "email", name = "UK_EMAIL_CLIENTE")
})
public class Cliente implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty()
	@TamanhoStringValid(max = 150, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String nome;
	
	@NotEmpty()
	@CPFECNPJValid()
	@Column(name = "cpf_cnpj")
	private String cpfCnpj;
	
	@Transient
	private int tipo = 1;
	
	@Past(message = "A data deve ser anterior à de hoje")
	@Temporal(value = TemporalType.DATE)
	@Column(name = "data_nascimento")
	private Date dataNascimento;
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name="cliente_telefone", joinColumns=
    {@JoinColumn(name="cliente_id")}, inverseJoinColumns=
      {@JoinColumn(name="telefone_id")})
	private List<Telefone> telefones = new ArrayList<Telefone>();
	
	@EmailValid
	@TamanhoStringValid(max = 50, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String email;
	
	private String endereco;
	
	@OneToMany(mappedBy = "cliente")
	private List<OrdemDeServico> ordemDeServicos;
	
	@OneToMany(mappedBy = "cliente")
	private List<SaidaDeProdutos> saidaDeProdutos;
	
	@Transient
	private StringBuilder telefonePrincipal;
	
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

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public List<Telefone> getTelefones() {
		return telefones;
	}

	public void setTelefones(List<Telefone> telefones) {
		this.telefones = telefones;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public List<OrdemDeServico> getOrdemDeServicos() {
		return ordemDeServicos;
	}

	public void setOrdemDeServicos(List<OrdemDeServico> ordemDeServicos) {
		this.ordemDeServicos = ordemDeServicos;
	}

	public List<SaidaDeProdutos> getSaidaDeProdutos() {
		return saidaDeProdutos;
	}

	public void setSaidaDeProdutos(List<SaidaDeProdutos> saidaDeProdutos) {
		this.saidaDeProdutos = saidaDeProdutos;
	}
	
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	public StringBuilder getTelefonePrincipal() {
		if(telefonePrincipal == null) {
			telefonePrincipal = new StringBuilder();
		}
		telefonePrincipal.setLength(0);
		
		if(this.telefones != null && telefones.size() > 0) {
			String telTemp = "";
			for (Telefone t : telefones) {
				if(!t.getNumero().equals(telTemp)) {
					telefonePrincipal.append(t.getDdd() + " ");
					telefonePrincipal.append(t.getNumero());
					if(t.getRamal() != null && !t.getRamal().equals("")) {
						telefonePrincipal.append(" R.: " + t.getRamal());
					}
					telefonePrincipal.append("<br/>");
					telTemp = t.getNumero();
				}
			}
		}
		return telefonePrincipal;
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
		Cliente other = (Cliente) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}