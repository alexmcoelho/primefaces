package com.alex.modelo.enums;

public enum FormaPagamento{	
	
	DINHEIRO(1, "Dinheiro"),
	CARTAO_DE_CREDITO(2, "Cartão de crédido"),
	CHEQUE(3, "Cheque"),
	DEBITO(4, "Débito"),
	DEPOSITO(5, "Depósito");
	
	private int cod;
	private String descricao;
	
	private FormaPagamento(int cod, String descricao) {
		this.cod = cod;
		this.descricao = descricao;
	}
	
	public int getCod() {
		return cod;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static FormaPagamento toEnum(Integer cod) {
		if(cod == null) {
			return null;
		}
		
		for (FormaPagamento x : FormaPagamento.values()) {
			if(cod.equals(x.getCod())) {
				return x;
			}
		}
		
		throw new IllegalArgumentException("Id não econtrado: " + cod);
	}

}
