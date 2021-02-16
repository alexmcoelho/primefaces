package com.alex.DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.Query;

import com.alex.exception.NegocioException;
import com.alex.modelo.EntradaDeProdutos;
import com.alex.modelo.ItemEntrada;
import com.alex.modelo.Usuario;
import com.alex.modelo.enums.TipoOperacaoCRUD;
import com.alex.util.FacesUtil;
import com.alex.util.Transacional;

public class EntradaDeProdutosDAO extends ControleDaoGenerico<EntradaDeProdutos, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public EntradaDeProdutosDAO() {
		super(EntradaDeProdutos.class);
	}

	@Inject
	private DetalhesContaAPagarDAO detalhesContaAPagarDAO;

	@Inject
	private ItemEntradaDAO itemEntradaDAO;

	public EntradaDeProdutos porID(Long id) {
		List<EntradaDeProdutos> lista = manager.createQuery(""
				+ "SELECT e FROM EntradaDeProdutos e "
				+ "JOIN FETCH e.itemEntradas i "
				+ "JOIN FETCH i.produto p "
				+ "WHERE e.id = ?1", 
				EntradaDeProdutos.class)
				.setParameter(1, id).setMaxResults(1).getResultList();
		if(lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		return null;
		/*this.entidade = manager.find(EntradaDeProdutos.class, id);
		if(this.entidade != null) {
			this.entidade.getItemEntradas().forEach(i -> {
				Hibernate.initialize(i.getProduto().getItemEntradas());
				Hibernate.initialize(i.getProduto().getItemProdutos());
				Hibernate.initialize(i.getProduto().getItemProdSaidas());
			});
		}
		return this.entidade;*/
	}
	
	@Transacional
	public void salvar(EntradaDeProdutos entradaDeProdutos, List<ItemEntrada> itemEntradas) {

		try {
			//só filtra itens da lista que tenham id diferente de nulo
			if(entradaDeProdutos.getItemEntradas() != null && entradaDeProdutos.getItemEntradas().size() > 0) {
				entradaDeProdutos.setItemEntradas(entradaDeProdutos.getItemEntradas().stream()        
						.filter(line -> line.getId() != null)     
						.collect(Collectors.toList()));
			}
			entradaDeProdutos = manager.merge(entradaDeProdutos);
			manager.flush();
			this.entidade = entradaDeProdutos;
			// INICIO - para dar baixa
			for (ItemEntrada i : itemEntradas) {
				i.setEntradaDeProdutos(entradaDeProdutos);
				// irá inserir na tabela de qualquer forma o itemproSaida para depois possa
				// aparecer na consulta
				itemEntradaDAO.salvar(i);
				// FIM - para dar baixa
			}
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.INSERT_UPDATE);
		}
		FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoSalvar(), "");
	}
	
	@SuppressWarnings("unchecked")
	public List<EntradaDeProdutos> listAll() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM EntradaDeProdutos");
		
		query = manager.createQuery("FROM EntradaDeProdutos ORDER BY data DESC", EntradaDeProdutos.class);
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<EntradaDeProdutos> listaPorId() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM EntradaDeProdutos WHERE id = :id");
		
		query = manager.createQuery("FROM EntradaDeProdutos WHERE id = :id ORDER BY data DESC", EntradaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	public List<EntradaDeProdutos> listaPorIdFornecedor(Long idFornecedor) {
		return manager.createQuery("FROM EntradaDeProdutos WHERE fornecedor.id = ?1", EntradaDeProdutos.class)
				.setParameter(1, idFornecedor).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<EntradaDeProdutos> intervaloEntreDatas() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM EntradaDeProdutos WHERE data BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM EntradaDeProdutos WHERE data BETWEEN :dataInicio AND :dataFim ORDER BY data DESC", EntradaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<EntradaDeProdutos> peloCodigoDaNota() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM EntradaDeProdutos WHERE codigoNota = :codigoNota");
		
		query = manager.createQuery("FROM EntradaDeProdutos WHERE codigoNota = :codigoNota ORDER BY data DESC", EntradaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<EntradaDeProdutos> peloProduto() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM EntradaDeProdutos e "
				+ "INNER JOIN e.itemEntradas itens "
				+ "INNER JOIN itens.produto p WHERE LOWER(p.descricao) LIKE :produto");
		
		query = manager.createQuery("SELECT e FROM EntradaDeProdutos e "
				+ "INNER JOIN e.itemEntradas itens "
				+ "INNER JOIN itens.produto p WHERE LOWER(p.descricao) LIKE :produto ORDER BY e.data", EntradaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	/* Lista do que foi gerado os detalhes das contas a pagar */
	public List<EntradaDeProdutos> entradasComContaGerada() {
		List<EntradaDeProdutos> lista = new ArrayList<>();
		List<EntradaDeProdutos> listaCorreta = new ArrayList<>();
		lista = manager.createQuery("FROM EntradaDeProdutos", EntradaDeProdutos.class).getResultList();
		/*
		 * essa funcao é temporaria - tentei usar a consulta teria que usar INNER JOIN
		 * nesta consulta, arruma futuramente MAS O CERTO É FAZER A FUNCAO DO LINK
		 * FUNCIONAR NESTE CASO TBM
		 */
		for (int i = 0; i < lista.size(); i++) {
			if (detalhesContaAPagarDAO.filtraFaturaPorIdEntrada(lista.get(i).getId()).size() > 0) {
				listaCorreta.add(lista.get(i));
			}
		}
		return listaCorreta;
	}

	/* Lista do que NÃO foi gerado os detalhes das contas a pagar */
	public List<EntradaDeProdutos> entradasSemContaGerada() {
		List<EntradaDeProdutos> lista = new ArrayList<>();
		lista = manager.createQuery("FROM EntradaDeProdutos", EntradaDeProdutos.class).getResultList();
		/*
		 * essa funcao é temporaria - tentei usar a consulta FROM OrdemDeServico o LEFT
		 * JOIN o.detalhesFaturas d WHERE d.ordemDeServico IS NULL AND o.estado = ?1 mas
		 * nao deu certo, peguei do link:
		 * http://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3 MAS
		 * O CERTO É FAZER A FUNCAO DO LINK FUNCIONAR NESTE CASO TBM
		 */
		for (int i = 0; i < lista.size(); i++) {
			if (detalhesContaAPagarDAO.filtraFaturaPorIdEntrada(lista.get(i).getId()).size() > 0) {
				lista.remove(i);
			}
		}
		return lista;
	}

	public List<EntradaDeProdutos> entradaContaGeradaComFiltroPorCodigo(Long id) {
		List<EntradaDeProdutos> lista = new ArrayList<>();
		List<EntradaDeProdutos> listaCorreta = new ArrayList<>();
		lista = manager.createQuery("FROM EntradaDeProdutos WHERE id = ?1", EntradaDeProdutos.class).setParameter(1, id)
				.getResultList();
		/*
		 * essa funcao é temporaria - tentei usar a consulta teria que usar INNER JOIN
		 * nesta consulta, arruma futuramente MAS O CERTO É FAZER A FUNCAO DO LINK
		 * FUNCIONAR NESTE CASO TBM
		 */
		for (int i = 0; i < lista.size(); i++) {
			if (detalhesContaAPagarDAO.filtraFaturaPorIdEntrada(lista.get(i).getId()).size() > 0) {
				listaCorreta.add(lista.get(i));
			}
		}
		return listaCorreta;
	}

	public List<EntradaDeProdutos> entradaSemContaGeradaComFiltradoPorCodigo(Long id) {
		List<EntradaDeProdutos> lista = new ArrayList<>();
		lista = manager.createQuery("FROM EntradaDeProdutos WHERE id = ?1", EntradaDeProdutos.class).setParameter(1, id)
				.getResultList();
		/*
		 * essa funcao é temporaria - tentei usar a consulta FROM OrdemDeServico o LEFT
		 * JOIN o.detalhesFaturas d WHERE d.ordemDeServico IS NULL AND o.estado = ?1 mas
		 * nao deu certo, peguei do link:
		 * http://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3 MAS
		 * O CERTO É FAZER A FUNCAO DO LINK FUNCIONAR NESTE CASO TBM
		 */
		for (int i = 0; i < lista.size(); i++) {
			if (detalhesContaAPagarDAO.filtraFaturaPorIdEntrada(lista.get(i).getId()).size() > 0) {
				lista.remove(i);
			}
		}
		return lista;
	}

	public List<EntradaDeProdutos> entradasContasGeradaComFiltradoPorFornecedor(Long idFornecedor) {
		List<EntradaDeProdutos> lista = new ArrayList<>();
		List<EntradaDeProdutos> listaCorreta = new ArrayList<>();
		lista = manager.createQuery("FROM EntradaDeProdutos WHERE fornecedor.id = ?1", EntradaDeProdutos.class)
				.setParameter(1, idFornecedor).getResultList();

		for (int i = 0; i < lista.size(); i++) {
			if (detalhesContaAPagarDAO.filtraFaturaPorIdEntrada(lista.get(i).getId()).size() > 0) {
				listaCorreta.add(lista.get(i));
			}
		}
		return listaCorreta;
	}

	public List<EntradaDeProdutos> entradasSemContaGeradaComFiltradoPorFornecedor(Long idFornecedor) {
		List<EntradaDeProdutos> lista = new ArrayList<>();
		lista = manager.createQuery("FROM EntradaDeProdutos WHERE fornecedor.id = ?1", EntradaDeProdutos.class)
				.setParameter(1, idFornecedor).getResultList();
		/*
		 * essa funcao é temporaria - tentei usar a consulta FROM OrdemDeServico o LEFT
		 * JOIN o.detalhesFaturas d WHERE d.ordemDeServico IS NULL AND o.estado = ?1 mas
		 * nao deu certo, peguei do link:
		 * http://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3 MAS
		 * O CERTO É FAZER A FUNCAO DO LINK FUNCIONAR NESTE CASO TBM
		 */
		for (int i = 0; i < lista.size(); i++) {
			if (detalhesContaAPagarDAO.filtraFaturaPorIdEntrada(lista.get(i).getId()).size() > 0) {
				lista.remove(i);
			}
		}
		return lista;
	}

	public List<EntradaDeProdutos> entradasContaGeradaComFiltradoPorDatas(Date dataInicio, Date dataFim) {
		List<EntradaDeProdutos> lista = new ArrayList<>();
		List<EntradaDeProdutos> listaCorreta = new ArrayList<>();
		lista = manager.createQuery("FROM EntradaDeProdutos WHERE data BETWEEN ?1 AND ?2", EntradaDeProdutos.class)
				.setParameter(1, dataInicio).setParameter(2, dataFim).getResultList();

		for (int i = 0; i < lista.size(); i++) {
			if (detalhesContaAPagarDAO.filtraFaturaPorIdEntrada(lista.get(i).getId()).size() > 0) {
				listaCorreta.add(lista.get(i));
			}
		}
		return listaCorreta;
	}

	public List<EntradaDeProdutos> entradasSemContaGeradaComFiltradoPorDatas(Date dataInicio, Date dataFim) {
		List<EntradaDeProdutos> lista = new ArrayList<>();
		lista = manager.createQuery("FROM EntradaDeProdutos WHERE data BETWEEN ?1 AND ?2", EntradaDeProdutos.class)
				.setParameter(1, dataInicio).setParameter(2, dataFim).getResultList();
		/*
		 * essa funcao é temporaria - tentei usar a consulta FROM OrdemDeServico o LEFT
		 * JOIN o.detalhesFaturas d WHERE d.ordemDeServico IS NULL AND o.estado = ?1 mas
		 * nao deu certo, peguei do link:
		 * http://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3 MAS
		 * O CERTO É FAZER A FUNCAO DO LINK FUNCIONAR NESTE CASO TBM
		 */
		for (int i = 0; i < lista.size(); i++) {
			if (detalhesContaAPagarDAO.filtraFaturaPorIdEntrada(lista.get(i).getId()).size() > 0) {
				lista.remove(i);
			}
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public boolean existeCodigoNota(EntradaDeProdutos en) {
		boolean retorno = false;
		String consulta = "FROM EntradaDeProdutos e WHERE e.codigoNota = ?1";
		Query query = null;
		if (en.getId() != null) {
			consulta = "FROM EntradaDeProdutos e WHERE e.codigoNota = ?1 AND e.id <> ?2";
			query = manager.createQuery(consulta);
			query.setParameter(1, en.getCodigoNota());
			query.setParameter(2, en.getId());
		} else {
			query = manager.createQuery(consulta);
			query.setParameter(1, en.getCodigoNota());
		}

		try {
			List<Usuario> lista = query.getResultList();
			if (!lista.isEmpty()) {
				retorno = true;
			}
		} catch (Exception e) {
			System.out.println("Imprimi erro: " + e);
		} finally {
			// manager.close();
		}
		return retorno;
	}

	@Override
	public EntradaDeProdutos getEntidade() {
		return this.entidade;
	}

}
