package com.alex.DAO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.hibernate.Hibernate;

import com.alex.exception.NegocioException;
import com.alex.modelo.ItemProdSaida;
import com.alex.modelo.SaidaDeProdutos;
import com.alex.modelo.enums.TipoOperacaoCRUD;
import com.alex.util.FacesUtil;
import com.alex.util.Transacional;

public class SaidaDeProdutosDAO extends ControleDaoGenerico<SaidaDeProdutos, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public SaidaDeProdutosDAO() {
		super(SaidaDeProdutos.class);
	}

	@Inject
	private ItemProdSaidaDAO itemProdSaidaDAO;
	
	
	public SaidaDeProdutos porID(Long id) {
		List<SaidaDeProdutos> lista = manager.createQuery(""
				+ "SELECT s FROM SaidaDeProdutos s "
				+ "JOIN FETCH s.itemProdSaidas i "
				+ "JOIN FETCH i.produto p "
				+ "WHERE s.id = ?1", 
				SaidaDeProdutos.class)
				.setParameter(1, id).setMaxResults(1).getResultList();
		if(lista != null && lista.size() > 0) {
			this.entidade = lista.get(0);
			this.entidade = manager.find(SaidaDeProdutos.class, id);
			if(this.entidade != null) {
				this.entidade.getItemProdSaidas().forEach(i -> {
					Hibernate.initialize(i.getProduto().getItemEntradas());
					Hibernate.initialize(i.getProduto().getItemProdutos());
				});
			}
			return this.entidade;
		}
		return null;
	}

	@Transacional
	public void salvar(SaidaDeProdutos saidaDeProdutos, List<ItemProdSaida> itemProdutos) {
		try {
			//só filtra itens da lista que tenham id diferente de nulo
			if(saidaDeProdutos.getItemProdSaidas() != null && saidaDeProdutos.getItemProdSaidas().size() > 0) {
				saidaDeProdutos.setItemProdSaidas(saidaDeProdutos.getItemProdSaidas().stream()        
						.filter(line -> line.getId() != null)     
						.collect(Collectors.toList()));
			}
			
			saidaDeProdutos = manager.merge(saidaDeProdutos);
			manager.flush();
			// INICIO - para dar baixa
			for (ItemProdSaida i : itemProdutos) {
				i.setSaidaDeProdutos(saidaDeProdutos);
				// irá inserir na tabela de qualquer forma o itemproSaida para depois possa
				// aparecer na consulta
				itemProdSaidaDAO.salvar(i);
				i = itemProdSaidaDAO.getEntidade();
				
				// FIM - para dar baixa
			}
			//saidaDeProdutos.setItemProdSaidas(itemProdutos);
			this.entidade = saidaDeProdutos;
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.INSERT_UPDATE);
		}
		FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoSalvar(), "");
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> listAll() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM SaidaDeProdutos");
		
		query = manager.createQuery("FROM SaidaDeProdutos ORDER BY data DESC", SaidaDeProdutos.class);
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> listPorId() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM SaidaDeProdutos WHERE id = :id");
		
		query = manager.createQuery("FROM SaidaDeProdutos WHERE id = :id ORDER BY data DESC", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> listaPorNomeCliente() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM SaidaDeProdutos WHERE LOWER(cliente.nome) LIKE :nomeCliente");
		
		query = manager.createQuery("FROM SaidaDeProdutos WHERE LOWER(cliente.nome) LIKE :nomeCliente ORDER BY data DESC", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> listaPorCpfCnpjCliente() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM SaidaDeProdutos WHERE cliente.cpfCnpj = :cpfCnpj");
		
		query = manager.createQuery("FROM SaidaDeProdutos WHERE cliente.cpfCnpj = :cpfCnpj ORDER BY data DESC", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> listaPorProduto() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM SaidaDeProdutos s "
				+ "INNER JOIN s.itemProdSaidas itens "
				+ "INNER JOIN itens.produto p WHERE LOWER(p.descricao) LIKE :produto");
		
		query = manager.createQuery("SELECT s FROM SaidaDeProdutos s "
				+ "INNER JOIN s.itemProdSaidas itens "
				+ "INNER JOIN itens.produto p WHERE LOWER(p.descricao) LIKE :produto ORDER BY s.data", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> intervaloEntreDatas() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM SaidaDeProdutos WHERE data BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM SaidaDeProdutos WHERE data BETWEEN :dataInicio AND :dataFim ORDER BY data DESC", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> saidasComFaturasGeradas() {
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT s) FROM SaidaDeProdutos s JOIN s.detalhesFaturas del");
		
		query = manager.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s JOIN FETCH s.detalhesFaturas del", SaidaDeProdutos.class);
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	// filtra vendas sem faturas geradas
	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> saidasSemFaturasGeradas() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT s) FROM SaidaDeProdutos s LEFT JOIN "
				+ "s.detalhesFaturas del WHERE del.saidaDeProdutos IS NULL");
		
		query = manager.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s LEFT JOIN " 
				+ "s.detalhesFaturas del WHERE del.saidaDeProdutos IS NULL", SaidaDeProdutos.class);
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> saidasFaturaGeradaComFiltradoPorCodigo() {
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT s) FROM SaidaDeProdutos s JOIN s.detalhesFaturas del " 
				+ "WHERE s.id = :id");
		
		query = manager.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s JOIN FETCH s.detalhesFaturas del "
				+ "WHERE s.id = :id", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> saidasSemFaturaGeradaComFiltradoPorCodigo() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT s) FROM SaidaDeProdutos s LEFT JOIN "
				+ "s.detalhesFaturas del WHERE del.saidaDeProdutos IS NULL AND s.id = :id");
		
		query = manager.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s LEFT JOIN " 
				+ "s.detalhesFaturas del WHERE del.saidaDeProdutos IS NULL AND s.id = :id", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> saidasFaturaGeradaComFiltradoPorCpfCnpjCliente() {
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT s) FROM SaidaDeProdutos s JOIN s.detalhesFaturas del " 
				+ "WHERE s.cliente.cpfCnpj = :cpfCnpj");
		
		query = manager.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s JOIN FETCH s.detalhesFaturas del "
				+ "WHERE s.cliente.cpfCnpj = :cpfCnpj", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> saidasSemFaturaGeradaComFiltradoPorCpfCnpjCliente() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT s) FROM SaidaDeProdutos s LEFT JOIN "
				+ "s.detalhesFaturas del WHERE del.saidaDeProdutos IS NULL AND s.cliente.cpfCnpj = :cpfCnpj");
		
		query = manager.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s LEFT JOIN " 
				+ "s.detalhesFaturas del WHERE del.saidaDeProdutos IS NULL AND s.cliente.cpfCnpj = :cpfCnpj", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	// filtra vendas sem faturas geradas pelas iniciais do nome do cliente
	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> saidasSemFaturaGeradaComFiltradoPorNomeCliente() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT s) FROM SaidaDeProdutos s LEFT JOIN "
				+ "s.detalhesFaturas del WHERE del.saidaDeProdutos IS NULL AND LOWER(s.cliente.nome) LIKE :nomeCliente");
		
		query = manager.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s LEFT JOIN " 
				+ "s.detalhesFaturas del WHERE del.saidaDeProdutos IS NULL AND LOWER(s.cliente.nome) LIKE :nomeCliente", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	// filtra vendas com faturas geradas pelas iniciais do nome do cliente
	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> saidasComFaturaGeradaComFiltradoPorNomeCliente() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT s) FROM SaidaDeProdutos s JOIN s.detalhesFaturas del " 
				+ "WHERE LOWER(s.cliente.nome) LIKE :nomeCliente");
		
		query = manager.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s JOIN FETCH s.detalhesFaturas del "
				+ "WHERE LOWER(s.cliente.nome) LIKE :nomeCliente", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> saidasFaturaGeradaComFiltradoPorDatas() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT s) FROM SaidaDeProdutos s JOIN s.detalhesFaturas del " 
				+ "WHERE s.data BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s JOIN FETCH s.detalhesFaturas del "
				+ "WHERE s.data BETWEEN :dataInicio AND :dataFim", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<SaidaDeProdutos> saidasSemFaturaGeradaComFiltradoPorDatas() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT s) FROM SaidaDeProdutos s LEFT JOIN "
				+ "s.detalhesFaturas del WHERE del.saidaDeProdutos IS NULL AND s.data BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("SELECT DISTINCT s FROM SaidaDeProdutos s LEFT JOIN " 
				+ "s.detalhesFaturas del WHERE del.saidaDeProdutos IS NULL AND s.data BETWEEN :dataInicio AND :dataFim", SaidaDeProdutos.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	public List<SaidaDeProdutos> saidasComFiltradoPorDatas(Date dataInicio, Date dataFim) {
		return manager.createQuery("FROM SaidaDeProdutos WHERE data BETWEEN ?1 AND ?2", SaidaDeProdutos.class)
				.setParameter(1, dataInicio).setParameter(2, dataFim).getResultList();
	}

	public List<SaidaDeProdutos> filtroPorCliente(Long idCliente) {
		return manager.createQuery("FROM SaidaDeProdutos WHERE cliente.id = ?1", SaidaDeProdutos.class)
				.setParameter(1, idCliente).getResultList();
	}

	public List<SaidaDeProdutos> filtroPorNumero(Long id) {
		return manager.createQuery("FROM SaidaDeProdutos WHERE id = ?1", SaidaDeProdutos.class).setParameter(1, id)
				.getResultList();
	}

	public List<SaidaDeProdutos> filtroPorUsuario(Long idUsuario) {
		return manager.createQuery("FROM SaidaDeProdutos WHERE usuario.id = ?1", SaidaDeProdutos.class)
				.setParameter(1, idUsuario).getResultList();
	}

	@Override
	public SaidaDeProdutos getEntidade() {
		return this.entidade;
	}

}
