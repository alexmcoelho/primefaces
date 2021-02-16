package com.alex.DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.Query;

import org.hibernate.Hibernate;

import com.alex.exception.NegocioException;
import com.alex.modelo.Aparelho;
import com.alex.modelo.ItemProduto;
import com.alex.modelo.ItemServico;
import com.alex.modelo.Modelo;
import com.alex.modelo.OrdemDeServico;
import com.alex.modelo.Servico;
import com.alex.modelo.enums.Estado;
import com.alex.modelo.enums.TipoOperacaoCRUD;
import com.alex.util.FacesUtil;
import com.alex.util.Transacional;

public class OrdemDeServicoDAO extends ControleDaoGenerico<OrdemDeServico, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public OrdemDeServicoDAO() {
		super(OrdemDeServico.class);
	}

	@Inject
	private DetalhesFaturaDAO detalhesFaturaDAO;

	@Inject
	private ItemProdutoDAO itemProdutoDAO;

	@Inject
	private Modelo modelo;
	
	@Inject
	private ItemServicoDAO itemServicoDAO;

	@Transacional
	public void salvar(OrdemDeServico ordemDeServico, List<ItemProduto> itemProdutos, List<ItemServico> itemServicos) {
		
		try {
			//só filtra itens da lista que tenham id diferente de nulo (produtos)
			if(ordemDeServico.getItemProdutos() != null && ordemDeServico.getItemProdutos().size() > 0) {
				ordemDeServico.setItemProdutos(ordemDeServico.getItemProdutos().stream()        
						.filter(line -> line.getId() != null)     
						.collect(Collectors.toList()));
			}
			//só filtra itens da lista que tenham id diferente de nulo (serviços)
			if(ordemDeServico.getItemServicos() != null && ordemDeServico.getItemServicos().size() > 0) {
				ordemDeServico.setItemServicos(ordemDeServico.getItemServicos().stream()        
						.filter(line -> line.getId() != null)     
						.collect(Collectors.toList()));
			}
			ordemDeServico = manager.merge(ordemDeServico);
			/*Hibernate.initialize(ordemDeServico.getCliente());
			Hibernate.initialize(ordemDeServico.getModelo());
			Hibernate.initialize(ordemDeServico.getModelo().getAparelho());*/
			manager.flush();
			this.entidade = ordemDeServico;
			// INICIO - para dar baixa
			for (ItemProduto i : itemProdutos) {
				i.setOrdemDeServico(ordemDeServico);
				// irá inserir na tabela de qualquer forma o itemproduto para depois possa
				// aparecer na consulta
				itemProdutoDAO.salvar(i);
				// FIM - para dar baixa
			}
			for (ItemServico i : itemServicos) {
				i.setOrdemDeServico(ordemDeServico);
				itemServicoDAO.salvar(i);
			}
			controle = true;
			Hibernate.initialize(this.entidade.getModelo().getAparelho());
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause() != null ? e.getCause().getCause().getMessage() : "Erro", TipoOperacaoCRUD.INSERT_UPDATE);
		}
		FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoSalvar(), "");

	}
	
	public OrdemDeServico porID(Long id) {
		this.entidade = manager.find(OrdemDeServico.class, id); 
		//fiz apenas com itemServico, pois itemProduto.produto já tinha tirado o LAZY
		if(this.entidade != null) {
			for (ItemServico itemServico : this.entidade.getItemServicos()) {
				Hibernate.initialize(itemServico.getServico());
			}
			this.entidade.getItemProdutos().forEach(i -> {
                Hibernate.initialize(i.getProduto().getItemEntradas());
                Hibernate.initialize(i.getProduto().getItemProdutos());
                Hibernate.initialize(i.getProduto().getItemProdSaidas());
            });
		}
		return this.entidade;
	}

	@Transacional
	public OrdemDeServico salvarUnique(OrdemDeServico ordemDeServico) {
		return manager.merge(ordemDeServico);
	}

	public List<OrdemDeServico> listAll() {
		return manager.createQuery("FROM OrdemDeServico ORDER BY id DESC", OrdemDeServico.class).getResultList();
	}
	
	public OrdemDeServico porIdAndChave(Long id, String chave) {
		List<OrdemDeServico> lista = manager.createQuery("FROM OrdemDeServico " + 
				"WHERE id = :id AND chave = :chave", OrdemDeServico.class)
				.setParameter("id", id)
				.setParameter("chave", chave)
				.getResultList();
		if(lista != null && lista.size() > 0) {
			controle = true;
			return lista.get(0);
		}
		controle = false;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> listAllPaginado() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "ORDER BY o.id DESC", OrdemDeServico.class);
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porId() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE id = :id");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.id = :id ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
		
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porImei() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE imei = :imei");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.imei = :imei ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porCpfCnpjCliente() {
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE cliente.cpfCnpj = :cpfCnpj");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.cliente.cpfCnpj = :cpfCnpj ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> intervaloEntreDatasDeEntrada() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE dataEntrada BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.dataEntrada BETWEEN :dataInicio AND :dataFim ORDER BY o.id DESC",
				OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porNomeCliente() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE LOWER(cliente.nome) LIKE :nomeCliente");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE LOWER(o.cliente.nome) LIKE :nomeCliente ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porNomeResponsavel() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE LOWER(usuario.nome) LIKE :nomeResponsavel");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH o.usuario u " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE LOWER(o.usuario.nome) LIKE :nomeResponsavel ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	public OrdemDeServico objporEstadoAndId(Long id, Estado estado) {
		List<OrdemDeServico> lista = manager.createQuery("FROM OrdemDeServico WHERE estado = :estado AND id = :id", OrdemDeServico.class)
				.setParameter("estado", estado.getCod()).setParameter("id", id).getResultList();
		if(lista != null && lista.size() > 0) {
			return lista.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porEstado() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE estado = :estado");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.estado = :estado ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
		
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porEstadoAndId() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE estado = :estado AND id = :id");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o "
				+ "JOIN FETCH o.cliente c "
				+ "JOIN FETCH c.telefones "
				+ "WHERE o.estado = :estado AND o.id = :id ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
		
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porEstadoAndImei() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE estado = :estado AND imei = :imei");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.estado = :estado AND o.imei = :imei ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	/*
	 * pesquisa ordens de serviço que não foram finalizadas a partir de um IMEI, no
	 * caso isso poderá acontecer apenas uma vez, e também caso aconteça o usuário
	 * não poderá não poderá salvar a ordem de serviço
	 */
	public List<OrdemDeServico> porEstadoDiferenteFinalizadoAndImei(String imei, Long id) {
		if (id != null) {
			return manager.createQuery("FROM OrdemDeServico WHERE estado != 3 AND imei = ?1 AND id != ?2",
					OrdemDeServico.class).setParameter(1, imei).setParameter(2, id).getResultList();
		}
		return manager.createQuery("FROM OrdemDeServico WHERE estado != 3 AND imei = ?1", OrdemDeServico.class)
				.setParameter(1, imei).getResultList();
	}

	public OrdemDeServico porImei(String imei, Long id) {
		OrdemDeServico obj = null;
		
		String hql = "";
		if (id != null) {
			hql = "SELECT o FROM OrdemDeServico o WHERE o.estado = '3' AND o.imei = :imei AND o.id <> :id ORDER BY o.dataConclusao DESC";
		} else {
			hql = "SELECT o FROM OrdemDeServico o WHERE o.estado = '3' AND o.imei = :imei ORDER BY o.dataConclusao DESC";
		}

		try {
			Query q = manager.createQuery(hql);
			q.setParameter("imei", imei);
			if (id != null) {
				q.setParameter("id", id);
			}
			q.setMaxResults(1);
			q.setFirstResult(0);
			
			if(q.getResultList().size() >= 1) {
				obj = (OrdemDeServico) q.getSingleResult(); // Este método é do JPA se não me engano. Se estiver
			}

		} catch (Exception e) {
			System.out.println("Erro ao tentar buscar IMEI." + e);
		}

		return obj;

	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porEstadoAndCpfCnpjCliente() {
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE estado = :estado AND cliente.cpfCnpj = :cpfCnpj");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.estado = :estado AND o.cliente.cpfCnpj = :cpfCnpj ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porEstadoAndNomeCliente() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE estado = :estado AND LOWER(cliente.nome) LIKE :nomeCliente");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.estado = :estado AND LOWER(o.cliente.nome) LIKE :nomeCliente ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> porEstadoAndNomeResponsavel() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE estado = :estado AND LOWER(usuario.nome) LIKE :nomeResponsavel");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH o.usuario u " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.estado = :estado AND LOWER(o.usuario.nome) LIKE :nomeResponsavel ORDER BY o.id DESC", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	public Aparelho filtraAparelho(Long idModelo) {
		modelo = manager.find(Modelo.class, idModelo);
		return modelo.getAparelho();
	}

	public Servico filtraServico(Long idServico) {
		return manager.find(Servico.class, idServico);
	}

	public List<OrdemDeServico> estadoFinalizado() {

		List<OrdemDeServico> lista = new ArrayList<>();
		lista = manager
				.createQuery("FROM OrdemDeServico o WHERE o.estado = ?1 " + "ORDER BY id DESC", OrdemDeServico.class)
				.setParameter(1, Estado.FINALIZADO.getCod()).getResultList();
		/*
		 * essa funcao é temporaria - tentei usar a consulta FROM OrdemDeServico o LEFT
		 * JOIN o.detalhesFaturas d WHERE d.ordemDeServico IS NULL AND o.estado = ?1 mas
		 * nao deu certo, peguei do link:
		 * http://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3 MAS
		 * O CERTO É FAZER A FUNCAO DO LINK FUNCIONAR NESTE CASO TBM
		 */
		for (int i = 0; i < lista.size(); i++) {
			if (detalhesFaturaDAO.filtraFaturaPorIdOrdem(lista.get(i).getId()).size() > 0) {
				lista.remove(i);
			}
		}
		return lista;
	}

	public List<OrdemDeServico> intervaloEntreDatas(Date dataInicio, Date dataFim) {
		return manager.createQuery("FROM OrdemDeServico WHERE dataConclusao BETWEEN ?1 AND ?2 " + "ORDER BY id DESC",
				OrdemDeServico.class).setParameter(1, dataInicio).setParameter(2, dataFim).getResultList();
	}

	/* Este método é usado apenas na lista-os.xhtml */
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> intervaloEntreDatasDeEntradaAndEstado() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE estado = :estado AND dataEntrada BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.estado = :estado AND o.dataEntrada BETWEEN :dataInicio AND :dataFim ORDER BY o.id DESC",
				OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	/* Este método é usado apenas na lista-os.xhtml */
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> intervaloEntreDatasDeConclusaoAndEstado() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM OrdemDeServico WHERE estado = :estado AND dataConclusao BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("SELECT o FROM OrdemDeServico o " 
				+ "JOIN FETCH o.cliente c " 
				+ "JOIN FETCH c.telefones " 
				+ "WHERE o.estado = :estado AND o.dataConclusao BETWEEN :dataInicio AND :dataFim ORDER BY o.id DESC",
				OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensComFaturaGerada() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o JOIN o.detalhesFaturas del " 
				+ "WHERE o.estado = 3");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o JOIN FETCH o.detalhesFaturas del "
				+ "WHERE o.estado = 3", OrdemDeServico.class);
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	// filtra ordem de serviço sem faturas geradas
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensSemFaturaGerada() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3", OrdemDeServico.class);
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensFaturaGeradaComFiltradoPorNumeroSO() {
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o JOIN o.detalhesFaturas del " 
				+ "WHERE o.estado = 3 AND o.id = :id");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o JOIN FETCH o.detalhesFaturas del "
				+ "WHERE o.estado = 3 AND o.id = :id", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensSemFaturaGeradaComFiltradoPorNumeroSO() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3 AND o.id = :id");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3 AND o.id = :id", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
		
	}

	// filtra ordem de serviço sem faturas geradas pelo IMEI
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensSemFaturaGeradaComFiltradoPeloImei() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3 AND o.imei = :imei");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3 AND o.imei = :imei", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	// filtra ordem de serviço com faturas geradas pelo IMEI
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensComFaturaGeradaComFiltradoPeloImei() {
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o JOIN o.detalhesFaturas del " 
				+ "WHERE o.estado = 3 AND o.imei = :imei");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o JOIN FETCH o.detalhesFaturas del "
				+ "WHERE o.estado = 3 AND o.imei = :imei", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensFaturaGeradaComFiltradoPorCpfCnpjCliente() {
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o JOIN o.detalhesFaturas del " 
				+ "WHERE o.estado = 3 AND o.cliente.cpfCnpj = :cpfCnpj");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o JOIN FETCH o.detalhesFaturas del "
				+ "WHERE o.estado = 3 AND o.cliente.cpfCnpj = :cpfCnpj", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensSemFaturaGeradaComFiltradoPorCpfCnpjCliente() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3 AND o.cliente.cpfCnpj = :cpfCnpj");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3 AND o.cliente.cpfCnpj = :cpfCnpj", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensFaturaGeradaComFiltradoPorDatas() {
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o JOIN o.detalhesFaturas del " 
				+ "WHERE o.estado = 3 AND o.dataConclusao BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o JOIN FETCH o.detalhesFaturas del "
				+ "WHERE o.estado = 3 AND o.dataConclusao BETWEEN :dataInicio AND :dataFim", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensSemFaturaGeradaComFiltradoPorDatas() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3 "
				+ "AND o.dataConclusao BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3 "
				+ "AND o.dataConclusao BETWEEN :dataInicio AND :dataFim", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	// filtra ordem de serviço sem faturas geradas pelas iniciais do nome do cliente
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensSemFaturaGeradaComFiltradoPorNome() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3 AND LOWER(o.cliente.nome) LIKE :nomeCliente");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o LEFT JOIN "
				+ "o.detalhesFaturas del WHERE del.ordemDeServico IS NULL AND o.estado = 3 AND LOWER(o.cliente.nome) LIKE :nomeCliente", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	// filtra ordem de serviço com faturas geradas pelas iniciais do nome do cliente
	@SuppressWarnings("unchecked")
	public List<OrdemDeServico> ordensComFaturaGeradaComFiltradoPorNome() {
		quantLinhas = contadorRegistros("SELECT COUNT(DISTINCT o) FROM OrdemDeServico o JOIN o.detalhesFaturas del " 
				+ "WHERE o.estado = 3 AND LOWER(o.cliente.nome) LIKE :nomeCliente");
		
		query = manager.createQuery("SELECT DISTINCT o FROM OrdemDeServico o JOIN FETCH o.detalhesFaturas del "
				+ "WHERE o.estado = 3 AND LOWER(o.cliente.nome) LIKE :nomeCliente", OrdemDeServico.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	public List<OrdemDeServico> estadoFinalizadoPorCliente(Long idCliente) {
		List<OrdemDeServico> lista = new ArrayList<>();
		lista = manager.createQuery("FROM OrdemDeServico WHERE estado = ?1 AND cliente.id = ?2", OrdemDeServico.class)
				.setParameter(1, Estado.FINALIZADO.getCod()).setParameter(2, idCliente).getResultList();
		for (int i = 0; i < lista.size(); i++) {
			if (detalhesFaturaDAO.filtraFaturaPorIdOrdem(lista.get(i).getId()).size() > 0) {
				lista.remove(i);
			}
		}
		return lista;
	}

	public List<OrdemDeServico> estadoFinalizadoPorNumeroSO(Long id) {
		return manager.createQuery("FROM OrdemDeServico WHERE estado = ?1 AND id = ?2", OrdemDeServico.class)
				.setParameter(1, Estado.FINALIZADO.getCod()).setParameter(2, id).getResultList();
	}

	public List<OrdemDeServico> filtroPorUsuario(Long idUsuario) {
		return manager.createQuery("FROM OrdemDeServico WHERE usuario.id = ?1", OrdemDeServico.class)
				.setParameter(1, idUsuario).getResultList();
	}

	@Override
	public OrdemDeServico getEntidade() {
		return this.entidade;
	}
	
	public void setEntidade(OrdemDeServico obj) {
		this.entidade = obj;
	}

}
