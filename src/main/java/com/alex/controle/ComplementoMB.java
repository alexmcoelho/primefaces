package com.alex.controle;

import java.util.HashMap;
import java.util.Map;

public abstract class ComplementoMB{
	
	protected String tituloBreadCrumbString;
	protected Map<String, Object> filtros = new HashMap<String, Object>();
	protected StringBuilder builder = new StringBuilder();
	protected String linkBreadCrumb;
	
	public abstract String getTitulo();
	public abstract String getLinkBreadCrumb();
	
	public String getTituloBreadCrumbString() {
		/*se caso fosse o registro de cadastro de parcelas teria que trabalhar a lógica para aparecer
		 * Editando registro 'id' da venda 'id' ou Editando registro 'id' da ordem de serviço 'id' ou
		 * Editando registro 'id' do cliente 'id'
		 */
		limpaStringBuilder();
		if(filtros != null && !filtros.isEmpty()) {
			builder.append("Editando ")
				.append(filtros.get("entidadeId"))
				.append(" ")
				.append(filtros.get("parametro01"));
			return builder.toString();
		}
		return getTitulo();
	}

	public void limpaStringBuilder() {
		if(builder != null) {
			builder.setLength(0);
		}
	}

}
