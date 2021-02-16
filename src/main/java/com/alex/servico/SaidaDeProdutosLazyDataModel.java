package com.alex.servico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.alex.DAO.SaidaDeProdutosDAO;
import com.alex.modelo.SaidaDeProdutos;
import com.alex.util.FacesUtil;

public class SaidaDeProdutosLazyDataModel extends LazyDataModel<SaidaDeProdutos> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<SaidaDeProdutos> objOptional = null;

	private Long idObj;

	private List<SaidaDeProdutos> lista = new ArrayList<SaidaDeProdutos>();

	@Inject
	private SaidaDeProdutosDAO objDAO;

	@Inject
	private SaidaDeProdutos saidaDeProduto;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	public boolean limpaParametros = true;
	
	private String descricaoProduto;

	@Override
	public List<SaidaDeProdutos> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {
			
			objDAO.setFiltros(parametros);
			objDAO.setPrimeiroReg(objDAO.getQuantLinhas() != 0 ? first : 0);
			objDAO.setQuantRegistros(pageSize);
			
			if(chaves.contains("semFaturaGerada") && chaves.contains("id")) {
				lista = objDAO.saidasSemFaturaGeradaComFiltradoPorCodigo();
			}
			else if(chaves.contains("semFaturaGerada") && chaves.contains("cpfCnpj")) {
				lista = objDAO.saidasSemFaturaGeradaComFiltradoPorCpfCnpjCliente();
			}
			else if(chaves.contains("semFaturaGerada") && chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.saidasSemFaturaGeradaComFiltradoPorDatas();
			}
			else if(chaves.contains("semFaturaGerada") && chaves.contains("nomeCliente")) {
				lista = objDAO.saidasSemFaturaGeradaComFiltradoPorNomeCliente();
			}
			else if(chaves.contains("semFaturaGerada")) {
				lista = objDAO.saidasSemFaturasGeradas();
			}
			else if(chaves.contains("id") && chaves.contains("comFaturaGerada")) {
				lista = objDAO.saidasFaturaGeradaComFiltradoPorCodigo();
			}
			else if(chaves.contains("cpfCnpj") && chaves.contains("comFaturaGerada")) {
				lista = objDAO.saidasFaturaGeradaComFiltradoPorCpfCnpjCliente();
			}
			else if(chaves.contains("dataInicio") && chaves.contains("dataFim") && chaves.contains("comFaturaGerada")) {
				lista = objDAO.saidasFaturaGeradaComFiltradoPorDatas();
			}
			else if(chaves.contains("nomeCliente") && chaves.contains("comFaturaGerada")) {
				lista = objDAO.saidasComFaturaGeradaComFiltradoPorNomeCliente();
			}
			else if(chaves.contains("comFaturaGerada")) {
				lista = objDAO.saidasComFaturasGeradas();
			}
			else if (chaves.contains("todos")) {
				lista = objDAO.listAll();
			} 
			else if (chaves.contains("id")) {
				lista = objDAO.listPorId();
			} 
			else if (chaves.contains("nomeCliente")) {
				lista = objDAO.listaPorNomeCliente();
			} 
			else if (chaves.contains("cpfCnpj")) {
				lista = objDAO.listaPorCpfCnpjCliente();
			} 
			else if(chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.intervaloEntreDatas();
			}
			else if (chaves.contains("produto")) {
				lista = objDAO.listaPorProduto();
			}
			
			if(objDAO.getQuantLinhas() <= 0) {
				FacesUtil.addWarnMessage(FacesUtil.mensagemPadraoRegistroNaoEncontrado(), "");
			}

			setRowCount(objDAO.getQuantLinhas());

		}
		else {
			setRowCount(0);
			lista.clear();
		}

		return lista;
	}

	@SuppressWarnings("unchecked")
	@Override
	public SaidaDeProdutos getRowData(String rowKey) {
		lista = (List<SaidaDeProdutos>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				saidaDeProduto = objOptional.get();
			}
		}

		return objOptional.orElse(new SaidaDeProdutos());

	}

	@Override
	public Object getRowKey(SaidaDeProdutos obj) {
		return obj != null ? obj.getId() : null;
	}

	public void aplicaChavesParametrosUtilizados() {
		if(chaves != null) {
			chaves.clear();
		}
		
		for (String chave : parametros.keySet()) {
			if(parametros.get(chave) != null) {
				chaves.add(chave);
			}
		}
	}
	
	public void limpaParametros() {
		objDAO.setQuantLinhas(0);
		for (String chave : parametros.keySet()) {
			parametros.put(chave, null);
		}
	}

	public SaidaDeProdutosDAO getObjDAO() {
		return objDAO;
	}

	public void setObjDAO(SaidaDeProdutosDAO objDAO) {
		this.objDAO = objDAO;
	}

	public SaidaDeProdutos getSaidaDeProduto() {
		return saidaDeProduto;
	}

	public void setSaidaDeProduto(SaidaDeProdutos saidaDeProduto) {
		this.saidaDeProduto = saidaDeProduto;
	}

	public String getDescricaoProduto() {
		return descricaoProduto;
	}

	public void setDescricaoProduto(String descricaoProduto) {
		this.descricaoProduto = descricaoProduto;
	}

}
