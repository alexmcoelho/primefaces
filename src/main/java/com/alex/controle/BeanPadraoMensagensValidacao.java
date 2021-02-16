package com.alex.controle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.alex.modelo.Mensagem;

//bean que será usado para as mensagens que não conseguem ser encontradados no ValidationMessage
//como por exemplo quando é usado <f:validateDoubleRange minimum="0.01" />
@Named
@ViewScoped
public class BeanPadraoMensagensValidacao implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Inject
	private Mensagem mensangem;
	
	private List<Mensagem> list = new ArrayList<Mensagem>();
	
	public BeanPadraoMensagensValidacao() {
		list.add(new Mensagem("javax.faces.validator.MINIMUM", "não pode ser inferior à %s"));
		list.add(new Mensagem("javax.faces.validator.INTERVAL", "o valor deve estar entre %s e %s"));
		list.add(new Mensagem("javax.faces.validator.BeanValidator.MESSAGE", "O preenchimento do campo %s é obrigatório"));
	}
	
	public String retorno(String chave, Object ... campos) {
		mensangem.setChave(chave);
		int i = list.indexOf(mensangem);
		return i >= 0 ? String.format(list.get(i).getValor(), campos) : null;
	}

}
