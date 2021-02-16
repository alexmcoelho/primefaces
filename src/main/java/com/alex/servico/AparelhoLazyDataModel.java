package com.alex.servico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.alex.DAO.AparelhoDAO;
import com.alex.modelo.Aparelho;
import com.alex.util.FacesUtil;

public class AparelhoLazyDataModel extends LazyDataModel<Aparelho> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
    private ServicoAutocomplete servicoAutocomplete;

	private Optional<Aparelho> objOptional = null;

	private Long idObj;

	private List<Aparelho> lista = new ArrayList<Aparelho>();

	@Inject
	private AparelhoDAO objDAO;

	@Inject
	private Aparelho aparelho;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	public boolean limpaParametros = true;

	@Override
	public List<Aparelho> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {
			
			objDAO.setFiltros(parametros);
			objDAO.setPrimeiroReg(objDAO.getQuantLinhas() != 0 ? first : 0);
			objDAO.setQuantRegistros(pageSize);
			
			if (chaves.contains("todos")) {
				lista = objDAO.listAll();
			} else if (chaves.contains("id")) {
				lista = objDAO.porId();
			} else if (chaves.contains("marca")) {
				lista = objDAO.porMarcaSemelhante();
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
	public Aparelho getRowData(String rowKey) {
		lista = (List<Aparelho>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				aparelho = objOptional.get();
			}
		}

		return objOptional.orElse(new Aparelho());

	}

	@Override
	public Object getRowKey(Aparelho obj) {
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
	
	public List<String> filtro(String query, int quantRegistros){
		int pos = query.indexOf("-");
		if(pos >= 0 && StringUtils.isNumericSpace(query.substring(0, pos-1)) && query.length() > (pos+1)) {
			return null;
		}
		List<String> listaString = new ArrayList<String>();
		objDAO.setQuantRegistros(quantRegistros);
		objDAO.limpaParametros();
		if(StringUtils.isNumericSpace(query)) {
			query = query.replace(" ", "");
			objDAO.adicionaFiltro("id", Long.parseLong(query));
			lista = objDAO.porId(); 
		}
		else {
			objDAO.adicionaFiltro("marca", ServicoGenerico.montaPesquisaInteligente(query, true));
			lista = objDAO.porMarcaSemelhante(); 
		}
		
		lista.forEach(obj -> {
			listaString.add(obj.getMarca());
		});
		return listaString;
    }
	
	public Aparelho busca(String pesquisa, boolean pesquisaPorId) {
		//vai sempre pesquisar sem o ID
		if(pesquisaPorId) {
			idObj = ServicoGenerico.retornaId(pesquisa);
		}
		
		if(lista != null && lista.size() > 0) {
			if(pesquisaPorId) {
				lista = lista.stream()                // convert list to stream
						.filter(line -> line.getId().equals(idObj))     
						.collect(Collectors.toList());
			}
			else {
				lista = lista.stream()                // convert list to stream
						.filter(line -> line.getMarca().equals(pesquisa))     
						.collect(Collectors.toList());
			}
			
			if(lista != null && lista.size() > 0) {
				return lista.get(0);
			}
		}
		
		//se a pesquisa ser feita pela segunda vez e tiver no cache, a lista será vazia, por isso tem que preencher ela de volta
		lista = objDAO.porMarca(pesquisa, 1);
		if(lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		
		return null;
	}
	
	public void addLista() {
		lista.add(this.aparelho);
	}

	public Aparelho getAparelho() {
		return aparelho;
	}

	public void setAparelho(Aparelho aparelho) {
		this.aparelho = aparelho;
	}

	public AparelhoDAO getObjDAO() {
		return objDAO;
	}

	public void setObjDAO(AparelhoDAO objDAO) {
		this.objDAO = objDAO;
	}

	public ServicoAutocomplete getServicoAutocomplete() {
		return servicoAutocomplete;
	}

	public void setServicoAutocomplete(ServicoAutocomplete servicoAutocomplete) {
		this.servicoAutocomplete = servicoAutocomplete;
	}

}
