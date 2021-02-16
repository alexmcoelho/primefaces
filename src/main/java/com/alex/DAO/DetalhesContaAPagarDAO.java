package com.alex.DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alex.exception.NegocioException;
import com.alex.modelo.DetalhesContaAPagar;
import com.alex.modelo.enums.TipoOperacaoCRUD;
import com.alex.util.Transacional;

public class DetalhesContaAPagarDAO extends ControleDaoGenerico<DetalhesContaAPagar, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public DetalhesContaAPagarDAO() {
		super(DetalhesContaAPagar.class);
	}

	private List<DetalhesContaAPagar> detalhesContasAPagarQueVemDoBanco = new ArrayList<>();

	@Transacional
	public void salvar(List<DetalhesContaAPagar> detalhesContaAPagarLista) {
		try {
			if (detalhesContaAPagarLista.size() > 0) { // essa condicao nao precisaria, mas so para garantir
				
				detalhesContasAPagarQueVemDoBanco = filtraFaturaPorIdEntrada(detalhesContaAPagarLista.get(0).getEntradaDeProdutos().getId());
							
				if (detalhesContasAPagarQueVemDoBanco.size() > 0) {
					//exclui tudo que tem no banco a nao ser que a parcela já tenha sido dado baixa
					for (DetalhesContaAPagar detalheExcluir : detalhesContasAPagarQueVemDoBanco) {
						detalheExcluir = porID(detalheExcluir.getId());
						if(detalheExcluir.isPaga() != true) {
							manager.remove(detalheExcluir);
							manager.flush();
						}						
					}
				}
			}
			for (DetalhesContaAPagar d : detalhesContaAPagarLista) {
				this.entidade = manager.merge(d);
				if (this.entidade == null) {
					break;
				}
			}
			controle = true;
		} catch (Exception e) {
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.INSERT_UPDATE);
		}

	}

	@Transacional
	public DetalhesContaAPagar salvarUnique(DetalhesContaAPagar detalhesContaAPagar) {
		return manager.merge(detalhesContaAPagar);
	}
	
	@Transacional
	public boolean excluizaoEmMassa(List<DetalhesContaAPagar> lista) {
		boolean retorno = false;
		for (DetalhesContaAPagar detalhesContaAPagar : lista) {
			try {
				detalhesContaAPagar = porID(detalhesContaAPagar.getId());
				manager.remove(detalhesContaAPagar);
				manager.flush();
				retorno = true;
			} catch (Exception e) {
				retorno = false;
				throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.DELETE);
			}
		}
		return retorno;
		
	}

	public List<DetalhesContaAPagar> listAll() {
		return manager.createQuery("FROM DetalhesContaAPagar ORDER BY id", DetalhesContaAPagar.class).getResultList();
	}
	
	public List<DetalhesContaAPagar> filtraDetalheContaPorSituacao(boolean paga) {
		return manager
				.createQuery("FROM DetalhesContaAPagar WHERE paga = ?1 ORDER BY id", DetalhesContaAPagar.class)
				.setParameter(1, paga).getResultList();
	}

	public List<DetalhesContaAPagar> filtraFaturaPorIdEntradaSituacao(boolean paga, Long idEntradaDeProdutos) {
		return manager.createQuery("FROM DetalhesContaAPagar WHERE paga = ?1 AND "
				+ "entradaDeProdutos.id = ?2 ORDER BY id", DetalhesContaAPagar.class)
				.setParameter(1, paga)
				.setParameter(2, idEntradaDeProdutos).getResultList();
	}
	
	public List<DetalhesContaAPagar> filtraFaturaPorIdEntrada(Long idSaidaDeServico) {
		return manager.createQuery("FROM DetalhesContaAPagar WHERE entradaDeProdutos.id = ?1 ORDER BY id", DetalhesContaAPagar.class)
				.setParameter(1, idSaidaDeServico).getResultList();
	}

	public List<DetalhesContaAPagar> filtraContaPorSituacao(boolean paga) {
		return manager
				.createQuery("FROM DetalhesContaAPagar WHERE paga = ?1 ORDER BY id", DetalhesContaAPagar.class)
				.setParameter(1, paga).getResultList();
	}

	public List<DetalhesContaAPagar> filtraContaPorFornecedor(Long idFornecedor, boolean paga) {
		return manager.createQuery(
				"FROM DetalhesContaAPagar WHERE entradaDeProdutos.fornecedor.id= ?1 AND paga = ?2",
				DetalhesContaAPagar.class).setParameter(1, idFornecedor).setParameter(2, paga).getResultList();
	}
	
	public List<DetalhesContaAPagar> intervaloEntreDatas(boolean baixada, Date dataInicio, Date dataFim) {
		return manager.createQuery("FROM DetalhesContaAPagar WHERE paga = ?1 AND data BETWEEN ?2 AND ?3", DetalhesContaAPagar.class)
				.setParameter(1, baixada)
				.setParameter(2, dataInicio)
				.setParameter(3, dataFim).getResultList();
	}

	@Override
	public DetalhesContaAPagar getEntidade() {
		return this.entidade;
	}

}
