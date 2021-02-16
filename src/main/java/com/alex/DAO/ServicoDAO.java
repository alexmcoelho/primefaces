package com.alex.DAO;

import java.io.Serializable;
import java.util.List;

import com.alex.modelo.Servico;

public class ServicoDAO extends ControleDaoGenerico<Servico, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public ServicoDAO() {
		super(Servico.class);
	}

	public List<Servico> listAll() {

		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Servico");

		return manager.createQuery("FROM Servico ORDER BY descricao", Servico.class).setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Servico> porId() {

		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Servico WHERE id = :id");

		query = manager.createQuery("FROM Servico WHERE id = :id", Servico.class);

		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}

		return query.setFirstResult(primeiroReg).setMaxResults(quantRegistros).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Servico> pordescricaoSemelhante() {

		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Servico WHERE LOWER(descricao) like :descricao");

		query = manager.createQuery("FROM Servico WHERE LOWER(descricao) like :descricao", Servico.class);

		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}

		return query.setFirstResult(primeiroReg).setMaxResults(quantRegistros).getResultList();
	}

	public List<Servico> listAllDepreciado() {
		return manager.createQuery("FROM Servico ORDER BY descricao", Servico.class).getResultList();
	}

	public List<Servico> listAllLast() {
		return manager.createQuery("FROM Servico ORDER BY id DESC", Servico.class).getResultList();
	}

	@Override
	public Servico getEntidade() {
		return this.entidade;
	}

}
