package com.alex.servico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.alex.DAO.ModeloDAO;
import com.alex.modelo.Modelo;
import com.alex.util.FacesUtil;

public class ModeloLazyDataModel extends LazyDataModel<Modelo> implements Serializable{

	private static final long serialVersionUID = 1L;

	private Optional<Modelo> objOptional = null;

	private Long idObj;

	private List<Modelo> lista = new ArrayList<Modelo>();

	@Inject
	private ModeloDAO objDAO;

	@Inject
	private Modelo modelo;

	public Map<String, Object> parametros = new HashMap<String, Object>();
	
	public List<String> chaves = new ArrayList<String>();
	
	public boolean limpaParametros = true;

	@Override
	public List<Modelo> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		aplicaChavesParametrosUtilizados();

		if (chaves != null && chaves.size() > 0) {
			
			objDAO.setFiltros(parametros);
			objDAO.setPrimeiroReg(objDAO.getQuantLinhas() != 0 ? first : 0);
			objDAO.setQuantRegistros(pageSize);
			
			if (chaves.contains("todos")) {
				lista = objDAO.listAll(first, pageSize);
			} else if (chaves.contains("id")) {
				lista = objDAO.porId(first, pageSize);
			}  else if (chaves.contains("marcaAparelho")) {
				lista = objDAO.porMarcaAparelho(first, pageSize);
			} else if(chaves.contains("idAparelho") && chaves.contains("modelo")) {
				lista = objDAO.filtrarPorAparelhoEModelo(first, pageSize);
			} else if (chaves.contains("modelo")) {
				lista = objDAO.porModeloSemelhante(first, pageSize);
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
	public Modelo getRowData(String rowKey) {
		lista = (List<Modelo>) getWrappedData();
		idObj = Long.parseLong(rowKey);

		if (lista != null) {
			objOptional = lista.stream() // convert list to stream
					.filter(line -> idObj.equals(line.getId())) // we dont like id
					.findFirst().map(o -> {
						return o;
					});
			if (objOptional.isPresent()) {
				modelo = objOptional.get();
			}
		}

		return objOptional.orElse(new Modelo());

	}
	
	public List<String> filtro(String query, Long idAparelho, int quantRegistros){
		List<String> listaString = new ArrayList<String>();
		objDAO.setQuantRegistros(quantRegistros);
		objDAO.limpaParametros();
		
		//tirei a pesquisa por código para esse caso
		objDAO.adicionaFiltro("idAparelho", idAparelho);
		objDAO.adicionaFiltro("modelo", ServicoGenerico.montaPesquisaInteligente(query, true));
		lista = objDAO.filtrarPorAparelhoEModelo(0, objDAO.getQuantRegistros()); 
		
		lista.forEach(obj -> {
			listaString.add(obj.getModelo());
		});
		return listaString;
    }
	
	public Modelo busca(String pesquisa, boolean pesquisaPorId) {
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
						.filter(line -> line.getModelo().equals(pesquisa))     
						.collect(Collectors.toList());
			}
			
			if(lista != null && lista.size() > 0) {
				return lista.get(0);
			}
		}
		
		//se a pesquisa ser feita pela segunda vez e tiver no cache, a lista será vazia, por isso tem que preencher ela de volta
		lista = objDAO.filtrarPorModelo(pesquisa, 1);
		if(lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		
		return null;
	}
	
	public void addLista() {
		lista.add(this.modelo);
	}

	@Override
	public Object getRowKey(Modelo obj) {
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

	public Modelo getModelo() {
		return modelo;
	}

	public void setModelo(Modelo modelo) {
		this.modelo = modelo;
	}

	public ModeloDAO getObjDAO() {
		return objDAO;
	}

	public void setObjDAO(ModeloDAO objDAO) {
		this.objDAO = objDAO;
	}

}
