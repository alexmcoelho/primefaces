package com.alex.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.alex.modelo.enums.EnumSimNaoGenerico;
import com.alex.modelo.enums.Estado;
import com.alex.modelo.enums.FormaPagamento;
import com.alex.modelo.enums.QuatMesesGarantia;
import com.alex.modelo.validacao.TamanhoStringValid;

@Entity
@Table(name="ordem_de_servico")
public class OrdemDeServico implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cliente_id", foreignKey = @ForeignKey(name = "FK_ORDEM_DE_SERVICO_PARA_CLIENTE"))
	private Cliente cliente;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", foreignKey = @ForeignKey(name = "FK_ORDEM_DE_SERVICO_PARA_USUARIO"))
	private Usuario usuario;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	private Integer estado;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@Column(name = "data_entrada")
	@Temporal(value = TemporalType.DATE)
	private Date dataEntrada;
	
	@Temporal(value = TemporalType.DATE)
	@Column(name = "data_conclusao")
	private Date dataConclusao;
	
	@Column(name = "estado_aparelho", columnDefinition = "TEXT")
	private String estadoAparelho;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	private Integer bateria;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	private Integer carregador;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@Column(name = "defeito_informado", columnDefinition = "TEXT")
	private String defeitoInformado;
	
	@Column(name = "laudo_tecnico", columnDefinition = "TEXT")
	private String laudoTecnico;
	
	@Column(columnDefinition = "TEXT")
	private String observacoes;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "modelo_id", foreignKey = @ForeignKey(name = "FK_ORDEM_DE_SERVICO_PARA_MODELO"))
	private Modelo modelo;
	
	@Column(precision = 14, scale = 2)
    private BigDecimal valor;
	
	@NotNull(message = "O preenchimento do campo é obrigatório")
	private String imei;
	
	@Column(name = "forma_pagamento")
	private Integer formaPagamento;

	@NotNull(message = "O preenchimento do campo é obrigatório")
	@Column(name = "quant_meses_garantia")
	private Integer quantMesesGarantia;
	
	@TamanhoStringValid(max = 150, message = "{com.alex.modelo.validacao.TamanhoStringValid.max.message}")
	private String chave;
	
	@OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemProduto> itemProdutos;
	
	@OneToMany(mappedBy = "ordemDeServico", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ItemServico> itemServicos;
	
	@OneToMany(mappedBy = "ordemDeServico")
	private Collection<DetalhesFatura> detalhesFaturas;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Estado getEstado() {
		return Estado.toEnum(estado);
	}

	public void setEstado(Estado estado) {
		this.estado = estado.getCod();
	}
	
	public EnumSimNaoGenerico getCarregador() {
		return EnumSimNaoGenerico.toEnum(carregador);
	}

	public void setCarregador(EnumSimNaoGenerico carregador) {
		this.carregador = carregador.getCod();
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public Date getDataConclusao() {
		return dataConclusao;
	}

	public void setDataConclusao(Date dataConclusao) {
		this.dataConclusao = dataConclusao;
	}

	public String getLaudoTecnico() {
		return laudoTecnico;
	}

	public void setLaudoTecnico(String laudoTecnico) {
		this.laudoTecnico = laudoTecnico;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	public Modelo getModelo() {
		return modelo;
	}

	public void setModelo(Modelo modelo) {
		this.modelo = modelo;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public FormaPagamento getFormaPagamento() {
		return FormaPagamento.toEnum(formaPagamento);
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento.getCod();
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public boolean isInclusao(){
		return id == null ? true : false;
	}
	
	public boolean isEdicao(){
		return !isInclusao();
	}
	
	public List<ItemProduto> getItemProdutos() {
		return itemProdutos;
	}

	public void setItemProdutos(List<ItemProduto> itemProdutos) {
		this.itemProdutos = itemProdutos;
	}

	public Collection<DetalhesFatura> getDetalhesFaturas() {
		return detalhesFaturas;
	}

	public void setDetalhesFaturas(Collection<DetalhesFatura> detalhesFaturas) {
		this.detalhesFaturas = detalhesFaturas;
	}

	public QuatMesesGarantia getQuantMesesGarantia() {
		return QuatMesesGarantia.toEnum(quantMesesGarantia);
	}

	public void setQuantMesesGarantia(QuatMesesGarantia quatMesesGarantia) {
		this.quantMesesGarantia = quatMesesGarantia.getCod();
	}

	public String getEstadoAparelho() {
		return estadoAparelho;
	}

	public void setEstadoAparelho(String estadoAparelho) {
		this.estadoAparelho = estadoAparelho;
	}

	public String getDefeitoInformado() {
		return defeitoInformado;
	}

	public void setDefeitoInformado(String defeitoInformado) {
		this.defeitoInformado = defeitoInformado;
	}

	public EnumSimNaoGenerico getBateria() {
		return EnumSimNaoGenerico.toEnum(bateria);
	}

	public void setBateria(EnumSimNaoGenerico bateria) {
		this.bateria = bateria.getCod();
	}

	public List<ItemServico> getItemServicos() {
		return itemServicos;
	}

	public void setItemServicos(List<ItemServico> itemServicos) {
		this.itemServicos = itemServicos;
	}
	
	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public List<Integer> listaEnumTiraEstoque(){
		return Arrays.asList(
				Estado.GARANTIA_CONCLUIDA.getCod(),
				Estado.GARANTIA_ANDAMENTO.getCod(),
				Estado.APROVADO.getCod(),
				Estado.FINALIZADO.getCod()
				);
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
		OrdemDeServico other = (OrdemDeServico) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
