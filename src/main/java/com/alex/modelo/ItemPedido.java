package com.alex.modelo;

import java.io.Serializable;
import java.util.Date;

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
import javax.validation.constraints.NotNull;

import com.alex.modelo.validacao.MinIntValid;

@Entity
@Table(name="item_pedido")
public class ItemPedido implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pedido_id", foreignKey = @ForeignKey(name = "FK_ITEM_PEDIDO_PARA_PEDIDO"))
	private Pedido pedido;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne()
	@JoinColumn(name = "produto_id", foreignKey = @ForeignKey(name = "FK_ITEM_PEDIDO_PARA_PRODUTO"))
	private Produto produto;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@MinIntValid(min = 1)
	private int quant;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne()
	@JoinColumn(name = "usuario_id", foreignKey = @ForeignKey(name = "FK_ITEM_PEDIDO_PARA_USUARIO"))
	private Usuario usuario;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@Temporal(value = TemporalType.DATE)
	private Date data;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
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

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
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
		ItemPedido other = (ItemPedido) obj;
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