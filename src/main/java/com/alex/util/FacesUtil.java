package com.alex.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class FacesUtil {
	//
	public static void addInfoMessage(String titulo, String mensagem){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO , titulo, mensagem));
	}
	
	public static void addWarnMessage(String titulo, String mensagem){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN , titulo, mensagem));
	}
	
	public static void addErrorMessage(String titulo, String mensagem){
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, titulo, mensagem));
	}
	
	public static String mensagemPadraoErroExclusao() {
		return "Este registro não pode ser excluído, provavelmente compõem outro registro.";
	}
	
	//mensagem padrão de erro de exclusão, quando não é chave estrangeira para nenhuma entidade
	public static String mensagemPadraoErroExclusaoNaoChaveEstrangeira() {
		return "Este registro não pode ser excluído, provavelmente compõem outro registro.";
	}
	
	public static String mensagemPadraoErroSalvar() {
		return "Não foi possível salvar este registro, verifique se os campos foram informados de forma correta!";
	}
	
	public static String mensagemPadraoAoSalvar() {
		return "Registro salvo com sucesso!";
	}
	
	public static String mensagemPadraoAoExcluir() {
		return "Registro excluído com sucesso!";
	}
	
	public static String mensagemPadraoErroAoSalvar() {
		return "Erro ao salvar registro!";
	}
	
	public static String mensagemPadraoRegistroNaoEncontrado() {
		return "Nenhum registro encontrado para essa pesquisa!";
	}
	
	public static String mensagemPadraoNaoEncontrouProduto() {
		return "Produto não encontrado!";
	}
}
