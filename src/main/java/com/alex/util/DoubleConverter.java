package com.alex.util;

import java.text.NumberFormat;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("DoubleConverter")
public class DoubleConverter implements Converter {
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String valorTela) throws ConverterException {
		if (valorTela == null || valorTela.toString().trim().equals("")) {
			return 0.0d;
		} else {
			try {
				valorTela = valorTela.replace(".", "");
				return Double.parseDouble(valorTela.replace(",", "."));
			} catch (Exception e) {
				return 0.0d;
			}
		}
	}
	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object valorTela) throws ConverterException {
		if (valorTela == null || valorTela.toString().trim().equals("")) {
			return "0,00";
		} else {
			NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
			nf.setMinimumFractionDigits(2);
			return nf.format(Double.valueOf(valorTela.toString()));
		}
	}
}
