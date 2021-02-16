package com.alex.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.alex.modelo.ItemProdSaida;
import com.alex.modelo.OrdemDeServico;

public class ManipData {
	
	private static Calendar dataVeioBanco = Calendar.getInstance();
	
	public static Calendar checaFDS(Calendar data) {
        // se for domingo
        if (data.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            data.add(Calendar.DATE, 1);
        } // se for sábado
        else if (data.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            data.add(Calendar.DATE, 2);
        }
        return data;
    }

    public static Date acrescenta30Dias(Date data, int dia) {
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = formatador.format(data);
        dataString = dia + dataString.substring(2, 10);
        Date d = null;
        try {
            d = new SimpleDateFormat("dd/MM/yyyy").parse(dataString);
        } catch (Exception e) {
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.add(Calendar.MONTH, 1);
        return cal.getTime();
    }
    
    //analisa se está na garantia ou não
  	public static String garantia(ItemProdSaida i) {
  		if(i == null || i.getQuantMesesGarantia().getCod() == 0) {
  			return "Não";
  		}
  		Calendar dataAtual = Calendar.getInstance();
  		dataAtual.setTime(dataAtual.getTime());
  		dataVeioBanco.setTime(i.getSaidaDeProdutos().getData());
  		dataVeioBanco.add(Calendar.DAY_OF_MONTH, i.getQuantMesesGarantia().getCod() * 30 + 1);
  		if(dataAtual.before(dataVeioBanco)) {
  			return "Sim";
  		}		
  		return "Não";
  	}
  	
  	//analisa se está na garantia ou não
  	public static String garantiaOS(OrdemDeServico o) {
  		if(o == null || o.getDataConclusao() == null || o.getQuantMesesGarantia().getCod() == 0) {
  			return "Não";
  		}
  		Calendar dataAtual = Calendar.getInstance();
  		dataAtual.setTime(dataAtual.getTime());
  		dataVeioBanco.setTime(o.getDataConclusao());
  		dataVeioBanco.add(Calendar.DAY_OF_MONTH, o.getQuantMesesGarantia().getCod() * 30 + 1);
  		if(dataAtual.before(dataVeioBanco)) {
  			return "Sim";
  		}		
  		return "Não";
  	}

}
