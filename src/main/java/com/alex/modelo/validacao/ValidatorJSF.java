package com.alex.modelo.validacao;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.DAO.ClienteDAO;
import com.alex.DAO.OrdemDeServicoDAO;
import com.alex.DAO.ProdutoDAO;
import com.alex.exception.ValidaCNPJ;
import com.alex.exception.ValidaCPF;
import com.alex.modelo.Cliente;
import com.alex.modelo.OrdemDeServico;
import com.alex.modelo.Produto;

@Named
@RequestScoped
public class ValidatorJSF implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ProdutoDAO produtoDAO;
	
	@Inject
	private ClienteDAO clienteDAO;
	
	@Inject
	private OrdemDeServicoDAO ordemDeServicoDAO;
	
	private String value;
	
	public void validateIMEIProduto(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		this.value = (String) value;
		Long id = (Long) component.getAttributes().get("idProduto");
		List<Produto> lista = null;
		if(!value.equals("000000-00-000000-0")) {
			lista = produtoDAO.porImei(this.value, id);
		}
		
		if (lista != null && lista.size() > 0) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção com o IMEI!",
					"O IMEI informado já existe para algum registro cadastrado!"));
		}
	}
	
	//aqui não verifica mais a garantia
	public void validaImeiAndGarantia(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		this.value = (String) value;
		Long id = (Long) component.getAttributes().get("idOrdem");
		List<OrdemDeServico> lista = null;
		if(!value.equals("000000-00-000000-0")) {
			lista = ordemDeServicoDAO.porEstadoDiferenteFinalizadoAndImei(this.value, id);
		}
		
		if (lista != null && lista.size() > 0) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção com esse IMEI!",
					"O aparelho com essa identificação tem uma O.S. que não foi finalizada."));
		}

	}
	
	public void validateCpfCnpjCliente(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		this.value = (String) value;
		Long id = (Long) component.getAttributes().get("idCliente");
		List<Cliente> lista = null;
		//se for um CPF
		if(this.value != null && this.value.length() <= 14 && !ValidaCPF.tiraPontoeTraco(this.value).equals("00000000000")) {
			lista = clienteDAO.porCpfCnpj(this.value, id);
			this.value = "CPF";
		}
		//ser for um CNPJ
		if(this.value != null && this.value.length() > 14 && !ValidaCNPJ.tiraPontoeTraco(this.value).equals("00000000000000")) {
			lista = clienteDAO.porCpfCnpj(this.value, id);
			this.value = "CNPJ";
		}
		
		if (lista != null && lista.size() > 0) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_WARN, String.format("Atenção com o %s!", this.value),
					String.format("O %s informado já existe para algum registro cadastrado!", this.value)));
		}
	}
	
	

}
