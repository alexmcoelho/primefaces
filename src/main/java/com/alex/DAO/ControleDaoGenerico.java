package com.alex.DAO;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.alex.exception.NegocioException;
import com.alex.modelo.enums.TipoOperacaoCRUD;
import com.alex.util.FacesUtil;
import com.alex.util.Transacional;

public abstract class ControleDaoGenerico<E, ID> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected boolean controle;
	
	@Inject
	protected EntityManager manager;
	
	private final Class<E> classe;
	
	@Inject
	protected E entidade;
	
	protected int quantLinhas;
	
	protected Query query;
	
	protected Map<String, Object> filtros = new HashMap<String, Object>();
	
	protected int primeiroReg;
	
	protected int quantRegistros;
	
	private List<String> listaString;
	
	public ControleDaoGenerico(Class<E> classe) {
		this.classe = classe;
	}
	
	public abstract E getEntidade();

	@Transacional
	public void salvar(E entidade) {

		try {
			entidade = manager.merge(entidade);
			manager.flush();
			this.entidade = entidade;
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause() != null ? e.getCause().getCause().getMessage() : "Erro", 
					TipoOperacaoCRUD.INSERT_UPDATE);
		}
		FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoSalvar(), "");
	}
	
	@Transacional
	public void excluir(ID id){
		try {
			entidade = porID(id);
			manager.remove(entidade);
			manager.flush();
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause() != null ? e.getCause().getCause().getMessage() : "Erro", 
					TipoOperacaoCRUD.DELETE);
		}
		FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoExcluir(), "");
	}

	public E porID(ID id) {		
		return manager.find(classe, id);
	}
	
	public int converterLongParaInt(Long num) {
		if(num != null && num > 0) {
			try {
				return Integer.valueOf(num.toString());
			}catch (Exception e) {   
			    System.out.println("Capacidade do Integer estourou. ->" + e.getMessage());
			    return 0;
			} 
		}
		return 0;
	}
	
	public int contadorRegistros(String queryString) {
		filtros = filtros.entrySet().stream()
				.filter(x -> x.getValue() != null && !x.getValue().equals("todos")
						&& !x.getValue().equals("entrada") && !x.getValue().equals("conclusao")
						&& !x.getValue().equals("semFaturaGerada") && !x.getValue().equals("comFaturaGerada")
						&& !x.getValue().equals("ordem") && !x.getValue().equals("venda")
						&& !x.getValue().equals("buscaValorPagoVendaLucro") && !x.getValue().equals("buscaQuant"))
				.collect(Collectors.toMap(x -> x.getKey(), x -> x.getValue()));
		
		if(quantLinhas == 0) {
			query = manager.createQuery(queryString);
			
			for (String chave : filtros.keySet()) {
				query.setParameter(chave, filtros.get(chave));
			}
			return converterLongParaInt((Long) query.getSingleResult()); 
		}
		else {
			return quantLinhas;
		}
	}
	
	public void adicionaFiltro(String chave, Object valor) {
		filtros.put(chave, valor);
	}
	
	public void limpaParametros() {
		filtros.put("id", null);
		filtros.put("descricao", null);
		filtros.put("todos", null);
	}

	public int getQuantLinhas() {
		return quantLinhas;
	}
	
	public void setQuantLinhas(int quantLinhas) {
		this.quantLinhas = quantLinhas;
	}
	
	public boolean isControle() {
		return controle;
	}

	public Map<String, Object> getFiltros() {
		return filtros;
	}

	public void setFiltros(Map<String, Object> filtros) {
		this.filtros = filtros;
	}

	public int getPrimeiroReg() {
		return primeiroReg;
	}

	public void setPrimeiroReg(int primeiroReg) {
		this.primeiroReg = primeiroReg;
	}

	public int getQuantRegistros() {
		return quantRegistros;
	}

	public void setQuantRegistros(int quantRegistros) {
		this.quantRegistros = quantRegistros;
	}
	
	public List<String> getListaString() {
		return listaString;
	}

	public void setListaString(List<String> listaString) {
		this.listaString = listaString;
	}

}
