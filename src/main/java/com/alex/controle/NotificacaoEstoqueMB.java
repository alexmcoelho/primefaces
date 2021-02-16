package com.alex.controle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.DAO.ProdutoDAO;
import com.alex.modelo.Produto;
import com.alex.servico.ProdutoLazyDataModel;
import com.alex.util.QuantEstoqueAviso;

@Named
@ViewScoped
public class NotificacaoEstoqueMB implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Inject
	private ProdutoDAO produtoDAO;
	
	private List<Produto> lista = new ArrayList<>();
	
	private int tamanho = 0;
	
	private Integer controle;
	
	private int quantAviso = QuantEstoqueAviso.quant;
	
	@Inject
	private ProdutoLazyDataModel model;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		controle = (Integer) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("controle");
		if(controle == null) {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("controle", 1);
			lista = produtoDAO.listAll();
			lista = model.calculaQuant(lista);
			
			lista = lista.stream()
					.filter(line -> line.getQuant() <= QuantEstoqueAviso.quant)
		            .collect(Collectors.toList());
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("notificacoes", lista);
		}
		lista = (List<Produto>) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("notificacoes");
		
		if(lista != null && lista.size() > 0) {
			tamanho = lista.size();
		}
	}

	public List<Produto> getLista() {
		return lista;
	}

	public void setLista(List<Produto> lista) {
		this.lista = lista;
	}

	public int getTamanho() {
		return tamanho;
	}

	public void setTamanho(int tamanho) {
		this.tamanho = tamanho;
	}

	public int getQuantAviso() {
		return quantAviso;
	}

	public void setQuantAviso(int quantAviso) {
		this.quantAviso = quantAviso;
	}

}
