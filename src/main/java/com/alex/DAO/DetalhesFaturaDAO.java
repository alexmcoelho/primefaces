package com.alex.DAO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alex.exception.NegocioException;
import com.alex.modelo.ClasseAuxiliarTotaisFaturas;
import com.alex.modelo.DetalhesFatura;
import com.alex.modelo.enums.TipoOperacaoCRUD;
import com.alex.util.FacesUtil;
import com.alex.util.Transacional;

public class DetalhesFaturaDAO extends ControleDaoGenerico<DetalhesFatura, Long> implements Serializable {

	public DetalhesFaturaDAO() {
		super(DetalhesFatura.class);
	}

	private static final long serialVersionUID = 1L;
	
    private List<DetalhesFatura> detalhesFaturasQueVemDoBanco = new ArrayList<>();
    
    private ClasseAuxiliarTotaisFaturas classeAuxiliarTotaisFaturas;
    
    private int guardaQuanLinhasAnterior = 0;
	
	@Transacional
	public DetalhesFatura salvar(List<DetalhesFatura> detalhesFaturas) {
		try {
			if (detalhesFaturas.size() > 0) { // essa condicao nao precisaria, mas so para garantir
				if (detalhesFaturas.get(0).getOrdemDeServico() != null) {
					detalhesFaturasQueVemDoBanco = filtraFaturaPorIdOrdem(
							detalhesFaturas.get(0).getOrdemDeServico().getId());
				} else {
					detalhesFaturasQueVemDoBanco = filtraFaturaPorIdSaida(
							detalhesFaturas.get(0).getSaidaDeProdutos().getId());
				}
				if (detalhesFaturasQueVemDoBanco.size() > 0) {
					// exclui tudo que tem no banco a nao ser que a parcela já tenha sido dado baixa
					for (DetalhesFatura detalheExcluir : detalhesFaturasQueVemDoBanco) {
						detalheExcluir = porID(detalheExcluir.getId());
						if (detalheExcluir.isBaixada() != true) {
							manager.remove(detalheExcluir);
							manager.flush();
						}
					}
				}
			}
			for (DetalhesFatura d : detalhesFaturas) {
				this.entidade = manager.merge(d);
				if (this.entidade == null) {
					break;
				}
			}
		} catch (Exception e) {
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.INSERT_UPDATE);
		}

		return this.entidade;
	}

	@Transacional
	public DetalhesFatura salvarUnique(DetalhesFatura detalhesFatura) {
		return manager.merge(detalhesFatura);
	}

	@Transacional
	public void excluizaoEmMassa(List<DetalhesFatura> lista) {
		for (DetalhesFatura detalhesFatura : lista) {
			try {
				detalhesFatura = porID(detalhesFatura.getId());
				manager.remove(detalhesFatura);
				manager.flush();
				controle = true;
			} catch (Exception e) {
				controle = false;
				throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.DELETE);
			}
		}
		FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoExcluir(), "");
	}

	public DetalhesFatura porID(Long id) {
		return manager.find(DetalhesFatura.class, id);
	}

	public List<DetalhesFatura> listAll() {
		return manager.createQuery("FROM DetalhesFatura ORDER BY id", DetalhesFatura.class).getResultList();
	}

	public List<DetalhesFatura> filtraDetalheFaturaPorSituacao(boolean baixada) {
		return manager
				.createQuery("FROM DetalhesFatura WHERE baixada = ?1 ORDER BY dataVencimento", DetalhesFatura.class)
				.setParameter(1, baixada).getResultList();
	}

	public List<DetalhesFatura> filtraFaturaPorIdOrdem(Long idOrdemDeServico) {
		return manager.createQuery("FROM DetalhesFatura WHERE ordemDeServico.id = ?1 ORDER BY id", DetalhesFatura.class)
				.setParameter(1, idOrdemDeServico).getResultList();
	}

	public List<DetalhesFatura> filtraFaturaPorIdSaida(Long idSaidaDeProdutos) {
		return manager
				.createQuery("FROM DetalhesFatura WHERE saidaDeProdutos.id = ?1 ORDER BY id", DetalhesFatura.class)
				.setParameter(1, idSaidaDeProdutos).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorSituacaoMasPegaSoOrdem() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND saidaDeProdutos IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND saidaDeProdutos IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaMasPegaSoOrdem() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE saidaDeProdutos IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE saidaDeProdutos IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}
	
    public List<DetalhesFatura> filtraFaturaPorSituacaoMasPegaSoOrdem(boolean baixada) {
        return manager.createQuery(
                "FROM DetalhesFatura WHERE baixada = ?1 AND saidaDeProdutos IS NULL ORDER BY dataVencimento",
                DetalhesFatura.class).setParameter(1, baixada).getResultList();
    }
	
    public List<DetalhesFatura> filtraFaturaPorSituacaoMasPegaSoSaida(boolean baixada) {
        return manager.createQuery(
                "FROM DetalhesFatura WHERE baixada = ?1 AND ordemDeServico IS NULL ORDER BY dataVencimento",
                DetalhesFatura.class).setParameter(1, baixada).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorSituacaoMasPegaSoSaida() {
    	// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND ordemDeServico IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND ordemDeServico IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaMasPegaSoSaida() {
    	// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE ordemDeServico IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE ordemDeServico IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
    }
    
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorIdOrdemAndSituacao() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND ordemDeServico.id = :id "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND ordemDeServico.id = :id "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorIdOrdem() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE ordemDeServico.id = :id "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE ordemDeServico.id = :id "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}
	
	public List<DetalhesFatura> filtraFaturaPorIdOrdemAndSituacao(Long idOrdemDeServico, boolean baixada) {
		return manager
				.createQuery(
						"FROM DetalhesFatura WHERE ordemDeServico.id = ?1 AND baixada = ?2 ORDER BY dataVencimento",
						DetalhesFatura.class)
				.setParameter(1, idOrdemDeServico).setParameter(2, baixada).getResultList();
	}

	public List<DetalhesFatura> filtraFaturaPorIdSaidaAndSituacao(Long idSaidaDeProduto, boolean baixada) {
		return manager
				.createQuery(
						"FROM DetalhesFatura WHERE saidaDeProdutos.id = ?1 AND baixada = ?2 ORDER BY dataVencimento",
						DetalhesFatura.class)
				.setParameter(1, idSaidaDeProduto).setParameter(2, baixada).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorIdSaidaAndSituacao() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND ordemDeServico IS NULL AND saidaDeProdutos.id = :id "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND ordemDeServico IS NULL AND saidaDeProdutos.id = :id "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorIdSaida() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE ordemDeServico IS NULL AND saidaDeProdutos.id = :id "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE ordemDeServico IS NULL AND saidaDeProdutos.id = :id "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPeloImeiAndSituacao() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND saidaDeProdutos IS NULL AND ordemDeServico.imei = :imei "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND saidaDeProdutos IS NULL AND ordemDeServico.imei = :imei "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPeloImei() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE saidaDeProdutos IS NULL AND ordemDeServico.imei = :imei "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE saidaDeProdutos IS NULL AND ordemDeServico.imei = :imei "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorNomeClienteDaOrdemAndSituacao() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND LOWER(ordemDeServico.cliente.nome) LIKE :nomeCliente "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND LOWER(ordemDeServico.cliente.nome) LIKE :nomeCliente "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorNomeClienteDaOrdem() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE LOWER(ordemDeServico.cliente.nome) LIKE :nomeCliente "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE LOWER(ordemDeServico.cliente.nome) LIKE :nomeCliente "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorNomeClienteDaSaidaAndSituacao() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND LOWER(saidaDeProdutos.cliente.nome) LIKE :nomeCliente "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND LOWER(saidaDeProdutos.cliente.nome) LIKE :nomeCliente "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorNomeClienteDaSaida() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE LOWER(saidaDeProdutos.cliente.nome) LIKE :nomeCliente "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE LOWER(saidaDeProdutos.cliente.nome) LIKE :nomeCliente "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorCpfCnpjClienteMasPegaSoOrdem() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND saidaDeProdutos IS NULL AND ordemDeServico.cliente.cpfCnpj = :cpfCnpj "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND saidaDeProdutos IS NULL AND ordemDeServico.cliente.cpfCnpj = :cpfCnpj "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorCpfCnpjClienteMasPegaSoOrdemSemSituacao() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE saidaDeProdutos IS NULL AND ordemDeServico.cliente.cpfCnpj = :cpfCnpj "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE saidaDeProdutos IS NULL AND ordemDeServico.cliente.cpfCnpj = :cpfCnpj "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorCpfCnpjClienteMasPegaSoSaida() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND ordemDeServico IS NULL AND saidaDeProdutos.cliente.cpfCnpj = :cpfCnpj "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND ordemDeServico IS NULL AND saidaDeProdutos.cliente.cpfCnpj = :cpfCnpj "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> filtraFaturaPorCpfCnpjClienteMasPegaSoSaidaSemSituacao() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE ordemDeServico IS NULL AND saidaDeProdutos.cliente.cpfCnpj = :cpfCnpj "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE ordemDeServico IS NULL AND saidaDeProdutos.cliente.cpfCnpj = :cpfCnpj "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}

	public List<DetalhesFatura> intervaloEntreDatasPegaSoOrdem(boolean baixada, Date dataInicio, Date dataFim) {
		return manager.createQuery(
				"FROM DetalhesFatura WHERE saidaDeProdutos IS NULL AND baixada = ?1 AND dataVencimento BETWEEN ?2 AND ?3 ORDER BY dataVencimento",
				DetalhesFatura.class).setParameter(1, baixada).setParameter(2, dataInicio).setParameter(3, dataFim)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> intervaloEntreDatasPegaSoOrdem() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND saidaDeProdutos IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND saidaDeProdutos IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> intervaloEntreDatasPegaSoOrdemSemSituacao() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE saidaDeProdutos IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE saidaDeProdutos IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}

	public List<DetalhesFatura> intervaloEntreDatasPegaSoSaida(boolean baixada, Date dataInicio, Date dataFim) {
		return manager.createQuery(
				"FROM DetalhesFatura WHERE ordemDeServico IS NULL AND baixada = ?1 AND dataVencimento BETWEEN ?2 AND ?3 ORDER BY dataVencimento",
				DetalhesFatura.class).setParameter(1, baixada).setParameter(2, dataInicio).setParameter(3, dataFim)
				.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> intervaloEntreDatasPegaSoSaida() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND ordemDeServico IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE baixada = :baixada AND ordemDeServico IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<DetalhesFatura> intervaloEntreDatasPegaSoSaidaSemSituacao() {
		// o que ajudou a fazer o método
		// https://www.guj.com.br/t/select-left-outer-join-com-jpa-resolvido/291093/3
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM DetalhesFatura "
				+ "WHERE ordemDeServico IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim");
		
		query = manager.createQuery("FROM DetalhesFatura "
				+ "WHERE ordemDeServico IS NULL "
				+ "AND dataVencimento BETWEEN :dataInicio AND :dataFim ORDER BY dataVencimento", DetalhesFatura.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
					.setMaxResults(quantRegistros).getResultList();
	}
	
	public ClasseAuxiliarTotaisFaturas totais() {
		if(quantLinhas != guardaQuanLinhasAnterior) {
			guardaQuanLinhasAnterior = quantLinhas;
			classeAuxiliarTotaisFaturas = new ClasseAuxiliarTotaisFaturas();
			if(filtros.keySet().contains("baixada") && (boolean) filtros.get("baixada") == false) {
				classeAuxiliarTotaisFaturas.setReceber(totais((boolean) filtros.get("baixada"), true, false));
				classeAuxiliarTotaisFaturas.setVencidas(totais((boolean) filtros.get("baixada"), true, true));
			}
			else if(filtros.keySet().contains("baixada") && (boolean) filtros.get("baixada") == true) {
				classeAuxiliarTotaisFaturas.setRecebidas(totais((boolean) filtros.get("baixada"), true, false));
			}
			else {
				classeAuxiliarTotaisFaturas.setRecebidas(totais(true, true, false));
				classeAuxiliarTotaisFaturas.setReceber(totais(false, true, false));
				classeAuxiliarTotaisFaturas.setVencidas(totais(false, true, true));
			}
			if(filtros.keySet().contains("baixada")) {
				classeAuxiliarTotaisFaturas.setTotal(totais((boolean) filtros.get("baixada"), true, false));
			}
			else {
				classeAuxiliarTotaisFaturas.setTotal(totais(false, false, false));
			}
		}
		return classeAuxiliarTotaisFaturas;
	}
	
	public BigDecimal totais(boolean baixada, boolean usaBaixada, boolean usaDataHoje) {
		String sql = "SELECT COALESCE(sum(d.valorParcela), 0) as total FROM DetalhesFatura d "
				+ "WHERE d.dataVencimento BETWEEN :dataInicio AND :dataFim ";
		Date dataHoje = null;
		sql += completaSql();
		if(usaBaixada) {
			sql += "AND d.baixada = :baixada ";
		}
		if(usaDataHoje) {
			sql += "AND d.dataVencimento < :dataHoje ";
			dataHoje = new Date();
		}
		if(filtros.keySet().contains("venda")) {
			sql += "AND d.saidaDeProdutos IS NULL ";
		}
		if(filtros.keySet().contains("ordem")) {
			sql += "AND d.ordemDeServico IS NULL ";
		}
		query = manager.createQuery(sql);
		if(usaBaixada) {
			query.setParameter("baixada", baixada);
		}
		if(usaDataHoje) {
			query.setParameter("dataHoje", dataHoje);
		}
		for (String chave : filtros.keySet()) {
			if(!chave.equals("baixada") && !chave.equals("venda") && !chave.equals("ordem")) {
				query.setParameter(chave, filtros.get(chave));
			}
		}
		Number result = (Number) query.getSingleResult();
		return new BigDecimal(result.toString());
	}
	
	public String completaSql() {
		if (filtros.keySet().contains("id") && filtros.keySet().contains("venda")) {
			return "AND d.saidaDeProdutos.id = :id ";
		} 
		else if (filtros.keySet().contains("cpfCnpj") && filtros.keySet().contains("venda")) {
			return "AND d.saidaDeProdutos.cliente.cpfCnpj = :cpfCnpj ";
		}
		else if (filtros.keySet().contains("nomeCliente") && filtros.keySet().contains("venda")) {
			return "AND LOWER(d.saidaDeProdutos.cliente.nome) LIKE :nomeCliente ";
		}
		
		if (filtros.keySet().contains("id") && filtros.keySet().contains("ordem")) {
			return "AND d.ordemDeServico.id = :id ";
		} 
		else if (filtros.keySet().contains("cpfCnpj") && filtros.keySet().contains("ordem")) {
			return "AND d.ordemDeServico.cliente.cpfCnpj = :cpfCnpj ";
		}
		else if (filtros.keySet().contains("nomeCliente") && filtros.keySet().contains("ordem")) {
			return "AND LOWER(d.ordemDeServico.cliente.nome) LIKE :nomeCliente ";
		}
		return "";
	}

	public int getGuardaQuanLinhasAnterior() {
		return guardaQuanLinhasAnterior;
	}

	public void setGuardaQuanLinhasAnterior(int guardaQuanLinhasAnterior) {
		this.guardaQuanLinhasAnterior = guardaQuanLinhasAnterior;
	}

	@Override
	public DetalhesFatura getEntidade() {
		return this.entidade;
	}

}
