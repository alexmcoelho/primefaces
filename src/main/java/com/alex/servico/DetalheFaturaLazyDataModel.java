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

import com.alex.DAO.DetalhesFaturaDAO;
import com.alex.modelo.ClasseAuxiliarTotaisFaturas;
import com.alex.modelo.DetalhesFatura;
import com.alex.util.FacesUtil;

public class DetalheFaturaLazyDataModel extends LazyDataModel<DetalhesFatura> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<DetalhesFatura> objOptional = null;

	private Long idObj;

	private List<DetalhesFatura> lista = new ArrayList<DetalhesFatura>();

	@Inject
	private DetalhesFaturaDAO objDAO;

	@Inject
	private DetalhesFatura detalhesFatura;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	public boolean limpaParametros = true;
	
	@Inject
	private ClasseAuxiliarTotaisFaturas classeAuxiliarTotaisFaturas;

	@Override
	public List<DetalhesFatura> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {
			
			objDAO.setFiltros(parametros);
			objDAO.setPrimeiroReg(objDAO.getQuantLinhas() != 0 ? first : 0);
			objDAO.setQuantRegistros(pageSize);
			
			if (chaves.contains("id") && chaves.contains("ordem") && chaves.contains("baixada") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorIdOrdemAndSituacao();
			} 
			else if (chaves.contains("id") && chaves.contains("venda") && chaves.contains("baixada") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorIdSaidaAndSituacao();
			}
			else if (chaves.contains("imei") && chaves.contains("ordem") && chaves.contains("baixada")
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPeloImeiAndSituacao();
			}
			else if (chaves.contains("cpfCnpj") && chaves.contains("ordem") && chaves.contains("baixada") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorCpfCnpjClienteMasPegaSoOrdem(); 
			}
			else if (chaves.contains("cpfCnpj") && chaves.contains("venda") && chaves.contains("baixada") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorCpfCnpjClienteMasPegaSoSaida();
			}
			else if (chaves.contains("dataInicio") && chaves.contains("dataFim") 
					&& chaves.contains("ordem") && chaves.contains("baixada") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.intervaloEntreDatasPegaSoOrdem();
			}
			else if (chaves.contains("dataInicio") && chaves.contains("dataFim") 
					&& chaves.contains("venda") && chaves.contains("baixada")
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.intervaloEntreDatasPegaSoSaida();
			}
			else if (chaves.contains("nomeCliente")
					&& chaves.contains("ordem") && chaves.contains("baixada")
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorNomeClienteDaOrdemAndSituacao();
			}
			else if (chaves.contains("venda") && chaves.contains("baixada") 
					&& chaves.contains("nomeCliente") && chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorNomeClienteDaSaidaAndSituacao();
			}
			else if (chaves.contains("ordem") && chaves.contains("baixada") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorSituacaoMasPegaSoOrdem();
			}
			else if (chaves.contains("venda") && chaves.contains("baixada") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorSituacaoMasPegaSoSaida();
			}
			//SEM SITUAÇÃO
			else if (chaves.contains("id") && chaves.contains("ordem")  
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorIdOrdem();
			} 
			else if (chaves.contains("id") && chaves.contains("venda") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorIdSaida();
			}
			else if (chaves.contains("imei") && chaves.contains("ordem") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPeloImei();
			}
			else if (chaves.contains("cpfCnpj") && chaves.contains("ordem") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorCpfCnpjClienteMasPegaSoOrdemSemSituacao(); 
			}
			else if (chaves.contains("cpfCnpj") && chaves.contains("venda") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorCpfCnpjClienteMasPegaSoSaidaSemSituacao();
			}
			else if (chaves.contains("dataInicio") && chaves.contains("dataFim") 
					&& chaves.contains("ordem") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.intervaloEntreDatasPegaSoOrdemSemSituacao();
			}
			else if (chaves.contains("dataInicio") && chaves.contains("dataFim") 
					&& chaves.contains("venda") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.intervaloEntreDatasPegaSoSaidaSemSituacao();
			}
			else if (chaves.contains("nomeCliente")
					&& chaves.contains("ordem") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorNomeClienteDaOrdem();
			}
			else if (chaves.contains("venda") 
					&& chaves.contains("nomeCliente") && chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaPorNomeClienteDaSaida();
			}
			else if (chaves.contains("ordem")  
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaMasPegaSoOrdem();
			}
			else if (chaves.contains("venda") 
					&& chaves.contains("dataInicio") && chaves.contains("dataFim")) {
				lista = objDAO.filtraFaturaMasPegaSoSaida();
			}
			
			if(lista != null && lista.size() > 0) {
				classeAuxiliarTotaisFaturas = objDAO.totais();
			}
			else {
				classeAuxiliarTotaisFaturas.newObj();
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
	public DetalhesFatura getRowData(String rowKey) {
		lista = (List<DetalhesFatura>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				detalhesFatura = objOptional.get();
			}
		}

		return objOptional.orElse(new DetalhesFatura());

	}

	@Override
	public Object getRowKey(DetalhesFatura obj) {
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
		objDAO.setGuardaQuanLinhasAnterior(0);
		for (String chave : parametros.keySet()) {
			parametros.put(chave, null);
		}
	}

	public DetalhesFatura getDetalhesFatura() {
		return detalhesFatura;
	}

	public void setDetalhesFatura(DetalhesFatura detalhesFatura) {
		this.detalhesFatura = detalhesFatura;
	}

	public ClasseAuxiliarTotaisFaturas getClasseAuxiliarTotaisFaturas() {
		return classeAuxiliarTotaisFaturas;
	}

	public void setClasseAuxiliarTotaisFaturas(ClasseAuxiliarTotaisFaturas classeAuxiliarTotaisFaturas) {
		this.classeAuxiliarTotaisFaturas = classeAuxiliarTotaisFaturas;
	}

}
