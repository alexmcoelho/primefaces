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

import com.alex.DAO.OrdemDeServicoDAO;
import com.alex.modelo.OrdemDeServico;
import com.alex.modelo.Usuario;
import com.alex.util.FacesUtil;

public class OrdemDeServicoLazyDataModel extends LazyDataModel<OrdemDeServico> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<OrdemDeServico> objOptional = null;

	private Long idObj;

	private List<OrdemDeServico> lista = new ArrayList<OrdemDeServico>();

	@Inject
	private OrdemDeServicoDAO objDAO;

	@Inject
	private OrdemDeServico ordemDeServico;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	public boolean limpaParametros = true;

	@Override
	public List<OrdemDeServico> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {
			
			objDAO.setFiltros(parametros);
			objDAO.setPrimeiroReg(objDAO.getQuantLinhas() != 0 ? first : 0);
			objDAO.setQuantRegistros(pageSize);
			
			if (chaves.contains("estado") && chaves.contains("id")) {
				lista = objDAO.porEstadoAndId();
			} 
			else if (chaves.contains("estado") && chaves.contains("imei")) {
				lista = objDAO.porEstadoAndImei();
			} 
			else if (chaves.contains("estado") && chaves.contains("cpfCnpj")) {
				lista = objDAO.porEstadoAndCpfCnpjCliente();
			} 
			else if (chaves.contains("entrada") && chaves.contains("estado") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.intervaloEntreDatasDeEntradaAndEstado();
			}
			else if (chaves.contains("conclusao") && chaves.contains("estado") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.intervaloEntreDatasDeConclusaoAndEstado();
			}
			else if (chaves.contains("estado") && chaves.contains("nomeCliente")) {
				lista = objDAO.porEstadoAndNomeCliente();
			}
			else if (chaves.contains("estado") && chaves.contains("nomeResponsavel")) {
				lista = objDAO.porEstadoAndNomeResponsavel();
			}
			else if (chaves.contains("estado") ) {
				lista = objDAO.porEstado();
			}
			else if (chaves.contains("semFaturaGerada") && chaves.contains("id")) {
				lista = objDAO.ordensSemFaturaGeradaComFiltradoPorNumeroSO();
			}
			else if (chaves.contains("semFaturaGerada") && chaves.contains("imei")) {
				lista = objDAO.ordensSemFaturaGeradaComFiltradoPeloImei();
			}
			else if (chaves.contains("semFaturaGerada") && chaves.contains("cpfCnpj")) {
				lista = objDAO.ordensSemFaturaGeradaComFiltradoPorCpfCnpjCliente();
			}
			else if (chaves.contains("semFaturaGerada") && chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.ordensSemFaturaGeradaComFiltradoPorDatas();
			}
			else if (chaves.contains("semFaturaGerada") && chaves.contains("nomeCliente")) {
				lista = objDAO.ordensSemFaturaGeradaComFiltradoPorNome();
			}
			else if (chaves.contains("semFaturaGerada")) {
				lista = objDAO.ordensSemFaturaGerada();
			}
			else if (chaves.contains("comFaturaGerada") && chaves.contains("id")) {
				lista = objDAO.ordensFaturaGeradaComFiltradoPorNumeroSO();
			}
			else if (chaves.contains("comFaturaGerada") && chaves.contains("imei")) {
				lista = objDAO.ordensComFaturaGeradaComFiltradoPeloImei();
			}
			else if (chaves.contains("comFaturaGerada") && chaves.contains("cpfCnpj")) {
				lista = objDAO.ordensFaturaGeradaComFiltradoPorCpfCnpjCliente();
			}
			else if (chaves.contains("comFaturaGerada") && chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.ordensFaturaGeradaComFiltradoPorDatas();
			}
			else if (chaves.contains("comFaturaGerada") && chaves.contains("nomeCliente")) {
				lista = objDAO.ordensComFaturaGeradaComFiltradoPorNome();
			}
			else if (chaves.contains("comFaturaGerada")) {
				lista = objDAO.ordensComFaturaGerada();
			}
			else if(chaves.contains("todos")) {
				lista = objDAO.listAllPaginado();
			}
			else if (chaves.contains("id")) {
				lista = objDAO.porId();
			}
			else if (chaves.contains("imei")) {
				lista = objDAO.porImei();
			}
			else if (chaves.contains("cpfCnpj")) {
				lista = objDAO.porCpfCnpjCliente();
			}
			else if (chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.intervaloEntreDatasDeEntrada();
			}
			else if (chaves.contains("nomeCliente")) {
				lista = objDAO.porNomeCliente();
			}
			else if (chaves.contains("nomeResponsavel")) {
				lista = objDAO.porNomeResponsavel();
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
	public OrdemDeServico getRowData(String rowKey) {
		lista = (List<OrdemDeServico>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				ordemDeServico = objOptional.get();
			}
		}

		return objOptional.orElse(new OrdemDeServico());

	}

	@Override
	public Object getRowKey(OrdemDeServico obj) {
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

	public OrdemDeServico getOrdemDeServico() {
		if(ordemDeServico.getUsuario() == null) {
			ordemDeServico.setUsuario(new Usuario());
		}
		return ordemDeServico;
	}

	public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
		this.ordemDeServico = ordemDeServico;
	}

	public OrdemDeServicoDAO getObjDAO() {
		return objDAO;
	}

	public void setObjDAO(OrdemDeServicoDAO objDAO) {
		this.objDAO = objDAO;
	}

}
