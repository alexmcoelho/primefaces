package com.alex.util.converters;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.alex.DAO.ProdutoDAO;
import com.alex.modelo.Produto;
@FacesConverter("converterProduto")
public class ConverterProduto implements Converter {
	
	@Inject
	ProdutoDAO dao;
	
	public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
		if(value != null && StringUtils.isNumericSpace(value) && value.trim().length() > 0) {
            try {
            	//FacesContext.getCurrentInstance().getExternalContext().getApplicationMap().get("produtoServico");
                return dao.porIDConverter(value);
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro na conversão", ""));
            }
		}
        else {
            return null;
        }
       
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
        	Produto p = (Produto) object;
            return String.valueOf( p.getId() );
        }
        else {
            return null;
        }
    } 

}
