package com.alex.modelo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.alex.modelo.validacao.TamanhoStringValid;

@Entity
@Table(name="usuario", uniqueConstraints = {
@UniqueConstraint(columnNames = "login", name = "UK_LOGIN"),
@UniqueConstraint(columnNames = "email", name = "UK_EMAIL_USUARIO")
})
public class Usuario implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	@TamanhoStringValid(max = 150, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String nome;
	
	@NotEmpty
	@TamanhoStringValid(max = 50, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String login;
	
	@NotEmpty
	@TamanhoStringValid(max = 50, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String email;
	
	@NotEmpty
	@TamanhoStringValid(max = 200, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String senha;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	private Boolean ativo = true;
	
	@ManyToMany(cascade=CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name="tipo_usuario_usuario", joinColumns=
    {@JoinColumn(name="usuario_id")}, inverseJoinColumns=
      {@JoinColumn(name="tipo_usuario_id")})
	private List<TipoUsuario> tipoUsuarios;
	
	@OneToMany(mappedBy = "usuario")
	private List<SaidaDeProdutos> saidaDeProdutos;

	@ManyToMany(cascade=CascadeType.MERGE)
    @JoinTable(name="perfil_usuario", joinColumns=
    {@JoinColumn(name="usuario_id")}, inverseJoinColumns=
      {@JoinColumn(name="perfil_id")})
	private List<Perfil> perfis;
	
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public List<SaidaDeProdutos> getSaidaDeProdutos() {
		return saidaDeProdutos;
	}

	public void setSaidaDeProdutos(List<SaidaDeProdutos> saidaDeProdutos) {
		this.saidaDeProdutos = saidaDeProdutos;
	}

	public List<Perfil> getPerfis() {
		return perfis;
	}

	public void setPerfis(List<Perfil> perfis) {
		this.perfis = perfis;
	}

	public List<TipoUsuario> getTipoUsuarios() {
		return tipoUsuarios;
	}

	public void setTipoUsuarios(List<TipoUsuario> tipoUsuarios) {
		this.tipoUsuarios = tipoUsuarios;
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
		Usuario other = (Usuario) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
