package com.alex.exception;

import com.alex.modelo.enums.TipoOperacaoCRUD;
import com.alex.modelo.enums.TipoConstraint;
import com.alex.util.FacesUtil;
import com.alex.util.ListadeInfoTabelas;

public class NegocioException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private boolean erroPadrao;
	
	public NegocioException(String msg) {
		super(msg);
		FacesUtil.addWarnMessage(msg, "");
	}
	
	public NegocioException(String msg, TipoOperacaoCRUD tipoCRUD){
		super(msg);
		//trata mensagem de erro caso for um insert
		if(tipoCRUD == TipoOperacaoCRUD.INSERT_UPDATE) {
			erroPadrao = false;
			if(msg.contains("uk") || msg.contains("UK")) {
				ExtracaoFKeUK.tipoConstraint = TipoConstraint.UK;
				msg = ExtracaoFKeUK.returnPhraseFKAndUK(ListadeInfoTabelas.pesquisaItem(
								ExtracaoFKeUK.extractErrorFKAndUK(msg))
								);
			}
			else {
				erroPadrao = true;
				msg = FacesUtil.mensagemPadraoErroAoSalvar();
			}
		}
		
		//trata mensagem de erro caso for um delete
		else {
			erroPadrao = false;
			if(msg.contains("fk") || msg.contains("FK")) {
				ExtracaoFKeUK.tipoConstraint = TipoConstraint.FK;
				msg = ExtracaoFKeUK.returnPhraseFKAndUK(ListadeInfoTabelas.pesquisaItem(
								ExtracaoFKeUK.extractErrorFKAndUK(msg))
								);
			}
			else {
				erroPadrao = true;
				msg = FacesUtil.mensagemPadraoErroExclusao();
			}
		}
		
		/*apenas imprimi mensagem, se for erro CONSTRAINT UK ou FK aparece mensagem do tipo Warn, 
		agora se não for identificado aparece mensagem do tipo Error*/
		if(erroPadrao) {
			FacesUtil.addErrorMessage(msg, "");
		} 
		else {
			FacesUtil.addWarnMessage(msg, "");
		}
	}

}
