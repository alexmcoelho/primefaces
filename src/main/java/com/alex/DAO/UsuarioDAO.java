package com.alex.DAO;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import com.alex.controle.criptografia.CriptografiaBase64;
import com.alex.exception.NegocioException;
import com.alex.modelo.Perfil;
import com.alex.modelo.Usuario;
import com.alex.modelo.enums.TipoOperacaoCRUD;
import com.alex.util.EncriptaDecriptaDES;
import com.alex.util.FacesUtil;
import com.alex.util.Transacional;

public class UsuarioDAO extends ControleDaoGenerico<Usuario, Long> implements Serializable {

	private static final long serialVersionUID = 1L;

	public UsuarioDAO() {
		super(Usuario.class);
	}

	@Transacional
	public String salvar(Usuario usuario, List<Perfil> perfis) {
		String mensagem = existeLogin(usuario, "login");

		if (mensagem.equals("")) {
			mensagem = existeLogin(usuario, "email");
			if (mensagem.equals("")) {
				usuario.setPerfis(perfis);
				usuario.setSenha(CriptografiaBase64.encode(usuario.getSenha()));
				usuario.setSenha(EncriptaDecriptaDES.cifrar(usuario.getSenha(), 10));
				usuario = manager.merge(usuario);
			}
		}
		return mensagem;
	}

	@Transacional
	public String alterar(Usuario usuario, List<Perfil> perfis) {
		String mensagem = existeLoginExcetoComEsseId(usuario, "login", usuario.getId());

		if (mensagem.equals("")) {
			mensagem = existeLoginExcetoComEsseId(usuario, "email", usuario.getId());
			if (mensagem.equals("")) {
				usuario.setPerfis(perfis);
				usuario.setSenha(CriptografiaBase64.encode(usuario.getSenha()));
				usuario.setSenha(EncriptaDecriptaDES.cifrar(usuario.getSenha(), 10));
				usuario = manager.merge(usuario);
			}
		}
		return mensagem;
	}

	@Transacional
	public void excluir(Usuario usuario) {
		try {
			// excluíndo Usuario
			usuario = porID(usuario.getId());
			if (usuario != null) {
				manager.remove(usuario);
				manager.flush();
			}
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.DELETE);
		}
		FacesUtil.addInfoMessage(FacesUtil.mensagemPadraoAoExcluir(), "");
	}

	public Usuario porID(Long id) {
		entidade = manager.find(Usuario.class, id);
		entidade.setSenha(EncriptaDecriptaDES.decifrar(entidade.getSenha(), 10));
		entidade.setSenha(CriptografiaBase64.decode(entidade.getSenha()));
		return entidade;
	}
	
	public Usuario porIDExtra(Long id) {
		return manager.find(Usuario.class, id);
	}

	public List<Usuario> listAll() {
		return manager.createQuery("FROM Usuario", Usuario.class).getResultList();
	}

	public List<Usuario> porTipoDoPerfil(String tipoPefil) {
		return manager
				.createQuery("SELECT DISTINCT u FROM Usuario u JOIN FETCH u.perfis per "
						+ "WHERE LOWER(per.tipoPerfil) LIKE ?1", Usuario.class)
				.setParameter(1, tipoPefil.toLowerCase() + "%").getResultList();
	}
	
	//caso o usuario utilizado em uma venda ou OS esteja inativo
	public List<Usuario> porTipoDoPerfilAndUsuario(String tipoPefil, Long idUsuario) {
		return manager
				.createQuery("SELECT DISTINCT u FROM Usuario u JOIN FETCH u.perfis per "
						+ "WHERE LOWER(per.tipoPerfil) LIKE ?1 "
						+ "AND u.ativo = true "
						+ "OR u.id = ?2", Usuario.class)
				.setParameter(1, tipoPefil.toLowerCase() + "%")
				.setParameter(2, idUsuario).getResultList();
	}

	/*
	 * public List<PerfilUsuario> porTipoDoPerfil2(String perfil) { String consulta;
	 * List<PerfilUsuario> lista = new ArrayList<>(); try { consulta =
	 * "FROM PerfilUsuario pu " + "INNER JOIN Usuario u " +
	 * "ON u.id = pu.usuario_id " + "INNER JOIN Perfil p " +
	 * "ON p.id = pu.perfil_id WHERE p.tipo_perfil = ?1"; Query query =
	 * manager.createQuery(consulta); query.setParameter(1, perfil); lista =
	 * query.getResultList();
	 * 
	 * } catch (Exception e) {
	 * 
	 * System.out.println("Imprimi erro: " + e); } finally { // manager.close(); }
	 * return lista; }
	 */

	public Usuario iniciarSessao(Usuario us) {
		us.setSenha(CriptografiaBase64.encode(us.getSenha()));
		us.setSenha(EncriptaDecriptaDES.cifrar(us.getSenha(), 10));
		try {
			query = manager.createQuery("FROM Usuario u WHERE u.login = ?1 AND u.senha = ?2 AND u.ativo = true");
			query.setParameter(1, us.getLogin());
			query.setParameter(2, us.getSenha());
			return (Usuario) query.getSingleResult();
		} catch (Exception e) {
			System.out.println("Imprimi erro: " + e);
			us.setSenha(EncriptaDecriptaDES.decifrar(us.getSenha(), 10));
			us.setSenha(CriptografiaBase64.decode(us.getSenha()));
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public String existeLogin(Usuario us, String tipo) {
		String mensagem = "";
		String consulta = "";
		Query query = null;
		if (tipo.equalsIgnoreCase("login")) {
			consulta = "FROM Usuario u WHERE u.login = ?1";
			query = manager.createQuery(consulta);
			query.setParameter(1, us.getLogin());
		} else if (tipo.equalsIgnoreCase("email")) {
			consulta = "FROM Usuario u WHERE u.email = ?1";
			query = manager.createQuery(consulta);
			query.setParameter(1, us.getEmail());
		}
		try {
			List<Usuario> lista = query.getResultList();
			if (!lista.isEmpty()) {
				if (tipo.equalsIgnoreCase("login")) {
					mensagem = "Este Login está sendo utilizado por outro usuário!";
				} else if (tipo.equalsIgnoreCase("email")) {
					mensagem = "Este Email está sendo utilizado por outro usuário!";
				}
			}
		} catch (Exception e) {
			System.out.println("Imprimi erro: " + e);
		} finally {
			// manager.close();
		}
		return mensagem;
	}

	@SuppressWarnings("unchecked")
	public String existeLoginExcetoComEsseId(Usuario us, String tipo, Long idUsuario) {
		String mensagem = "";
		String consulta = "";
		Query query = null;
		if (tipo.equalsIgnoreCase("login")) {
			consulta = "FROM Usuario u WHERE u.login = ?1 AND u.id <> ?2";
			query = manager.createQuery(consulta);
			query.setParameter(1, us.getLogin());
			query.setParameter(2, us.getId());
		} else if (tipo.equalsIgnoreCase("email")) {
			consulta = "FROM Usuario u WHERE u.email = ?1 AND u.id <> ?2";
			query = manager.createQuery(consulta);
			query.setParameter(1, us.getEmail());
			query.setParameter(2, us.getId());
		}
		try {
			List<Usuario> lista = query.getResultList();
			if (!lista.isEmpty()) {
				if (tipo.equalsIgnoreCase("login")) {
					mensagem = "Este Login está sendo utilizado por outro usuário!";
				} else if (tipo.equalsIgnoreCase("email")) {
					mensagem = "Este Email está sendo utilizado por outro usuário!";
				}
			}
		} catch (Exception e) {
			System.out.println("Imprimi erro: " + e);
		} finally {
			// manager.close();
		}
		return mensagem;
	}

	@SuppressWarnings("unchecked")
	public List<Usuario> porNomeUsuario() {
		
		quantLinhas = contadorRegistros("SELECT COUNT(*) FROM Usuario WHERE LOWER(nome) LIKE :nome");
		
		query = manager.createQuery("FROM Usuario WHERE LOWER(nome) LIKE :nome", Usuario.class);
		
		for (String chave : filtros.keySet()) {
			query.setParameter(chave, filtros.get(chave));
		}
		
		return query.setFirstResult(primeiroReg)
				.setMaxResults(quantRegistros).getResultList();	
	}

	@Override
	public Usuario getEntidade() {
		return this.entidade;
	}

}
