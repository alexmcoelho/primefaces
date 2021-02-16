package com.alex.exception;

import com.alex.modelo.enums.TipoConstraint;
import com.alex.util.InfoTable;

public class ExtracaoFKeUK {
	
	public static TipoConstraint tipoConstraint;
	
	//método funciona, mas por enquanto não será aplicado
	public static String extractErrorFKAndUK(String messageError){
		messageError = messageError.toUpperCase();
        if(tipoConstraint == TipoConstraint.FK) {
        	String[] divOne = messageError.split("CONSTRAINT");
            String[] divTwo = divOne[1].split("\""); // (MySQL = ` e PostGres = ")
            return divTwo[1];
        }
        //para quando é uma UK
        String[] arrayMessageComplete = messageError.split("\""); //unica coisa que não deu para padronizar tando para MySQL como PostGres (MySQL = ' e PostGres = ")
        return arrayMessageComplete.length >= 2 ? arrayMessageComplete[1] : null; //MySQL = arrayMessageComplete.length >= 4 ? arrayMessageComplete[3] : null;
    }
	
	public static String returnPhraseFKAndUK(InfoTable t){
        if(t != null && tipoConstraint == TipoConstraint.FK){
        	return "Esse registro não pode ser excluído pois está sendo utilizado por " +
                    t.getComplemenPhrase() + "!";
        }else if(t != null && tipoConstraint == TipoConstraint.UK){
        	return t.getComplemenPhrase() + " já existe para algum registro cadastrado!";
        }
        return null;
    }

}
