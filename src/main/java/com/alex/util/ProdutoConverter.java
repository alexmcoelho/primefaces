package com.alex.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import com.alex.DAO.ProdutoDAO;
import com.alex.modelo.Produto;

@FacesConverter(forClass = com.alex.modelo.Produto.class)
public class ProdutoConverter implements Converter {
    @Inject
	ProdutoDAO dao;
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
    	Produto p = dao.porIDConverter(value);        
        return p;
    }
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Produto p = (Produto) value;
        return String.valueOf( p.getId() );
    }
}
