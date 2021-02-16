package com.alex.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.alex.modelo.validacao.TamanhoStringValid;

@Entity
@Table(name="email")
public class Email implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotEmpty
	@TamanhoStringValid(max = 20, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	@Column(name = "host_name")
	private String hostName;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	private int porta;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@Column(name = "habilitado_tls")
	private boolean habilitadoTLS;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@Column(name = "habilitado_ssl")
	private boolean habilitadoSSL;
	
	@NotEmpty()
	@TamanhoStringValid(max = 50, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String email;
	
	@NotEmpty
	@TamanhoStringValid(max = 200, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String senha;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public boolean isHabilitadoTLS() {
		return habilitadoTLS;
	}

	public void setHabilitadoTLS(boolean habilitadoTLS) {
		this.habilitadoTLS = habilitadoTLS;
	}

	public boolean isHabilitadoSSL() {
		return habilitadoSSL;
	}

	public void setHabilitadoSSL(boolean habilitadoSSL) {
		this.habilitadoSSL = habilitadoSSL;
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
		Email other = (Email) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}