package com.alex.DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.alex.exception.NegocioException;
import com.alex.modelo.ItemProduto;
import com.alex.modelo.enums.TipoOperacaoCRUD;

public class ItemProdutoDAO extends ControleDaoGenerico<ItemProduto, Long> implements Serializable{

	private static final long serialVersionUID = 1L;

	public ItemProdutoDAO() {
		super(ItemProduto.class);
	}

	List<ItemProduto> lista = new ArrayList<>();
	
	/*Salva sem iniciar transação, pois aonde esse método foi chamado já foi iniciado a transação.
	E também não tem mensagem caso tenha sucesso ao salvar registro, só irá aparecer mensagem de sucesso no método que chama
	o método abaixo*/
	public void salvar(ItemProduto itemProduto){
		try {
			itemProduto = manager.merge(itemProduto);
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.INSERT_UPDATE);
		}
	}

	public List<ItemProduto> listAll(){
		return manager.createQuery("FROM ItemProduto", ItemProduto.class).getResultList();
	}
	
	//lista filtrado por ordem de servico
	@SuppressWarnings("unchecked")
	public List<ItemProduto> listaFiltradoPorOrdem(Long id){		
        String consulta;
        try {
            consulta = "FROM ItemProduto i WHERE i.ordemDeServico.id = ?1 ";
            Query query = manager.createQuery(consulta);
            query.setParameter(1, id);
            lista = query.getResultList();
        } catch (Exception e) {            
            System.out.println("Imprimi erro: " + e);
        }finally{
            //manager.close();
        }
        return lista;
    }
	
	//filtra por produto
	public List<ItemProduto> porIdProduto(Long id) {
		return manager.createQuery("FROM ItemProduto "
				+ "WHERE produto.id = :id ", ItemProduto.class)
			.setParameter("id", id)
			.getResultList();			
	}
	
	// filtra por produto sem incluir a ordemDeServico informada
	public List<ItemProduto> porIdProdutoSemDeterminadaOrdem(Long id, Long idOrdem) {
		return manager.createQuery("FROM ItemProduto "
				+ "WHERE produto.id = :id "
				+ "AND ordemDeServico.id <> :idOrdem ", ItemProduto.class)
				.setParameter("id", id)
				.setParameter("idOrdem", idOrdem).getResultList();
	}
	
	public int getQuantSaidaSO(Long idProduto) {
		Query q = manager.createQuery(
				"SELECT COALESCE(sum(i.quant), 0) as quantidade FROM "
				+ "ItemProduto i WHERE produto_id = " + idProduto);
		Number result = (Number) q.getSingleResult();			
		return Integer.parseInt(result.toString());
			
	}

	@Override
	public ItemProduto getEntidade() {
		return this.entidade;
	}

}
