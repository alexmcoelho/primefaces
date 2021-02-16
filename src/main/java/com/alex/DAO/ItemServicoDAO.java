package com.alex.DAO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.alex.exception.NegocioException;
import com.alex.modelo.ItemServico;
import com.alex.modelo.enums.TipoOperacaoCRUD;

public class ItemServicoDAO extends ControleDaoGenerico<ItemServico, Long> implements Serializable{

	private static final long serialVersionUID = 1L;

	public ItemServicoDAO() {
		super(ItemServico.class);
	}

	List<ItemServico> lista = new ArrayList<>();
	
	/*Salva sem iniciar transação, pois aonde esse método foi chamado já foi iniciado a transação.
	E também não tem mensagem caso tenha sucesso ao salvar registro, só irá aparecer mensagem de sucesso no método que chama
	o método abaixo*/
	public void salvar(ItemServico itemServico){
		try {
			itemServico = manager.merge(itemServico);
			controle = true;
		} catch (Exception e) {
			controle = false;
			throw new NegocioException(e.getCause().getCause().getMessage(), TipoOperacaoCRUD.INSERT_UPDATE);
		}
	}

	public List<ItemServico> listAll(){
		return manager.createQuery("FROM ItemProduto", ItemServico.class).getResultList();
	}
	
	//lista filtrado por ordem de servico
	@SuppressWarnings("unchecked")
	public List<ItemServico> listaFiltradoPorOrdem(Long id){		
        String consulta;
        try {
            consulta = "FROM ItemServico i WHERE i.ordemDeServico.id = ?1 ";
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
	public List<ItemServico> porIdProduto(Long id) {
		return manager.createQuery("FROM ItemServico WHERE produto.id = :id", ItemServico.class)
			.setParameter("id", id)
			.getResultList();			
	}
	
	public int getQuantSaidaSO(Long idProduto) {
		Query q = manager.createQuery(
				"SELECT COALESCE(sum(i.quant), 0) as quantidade FROM "
				+ "ItemServico i WHERE produto_id = " + idProduto);
		Number result = (Number) q.getSingleResult();			
		return Integer.parseInt(result.toString());
			
	}

	@Override
	public ItemServico getEntidade() {
		return this.entidade;
	}

}
