package com.alex.modelo.enums;

public enum QuatMesesGarantia {
	SEM(0, "Sem garantia"),
	UM_MES(1, "1 mês"),
	TRES_MESES(3, "3 meses"),
	QUATRO_MESES(4, "4 meses"),
	CINCO_MESES(5, "5 meses"),
	SEIS_MESES(6, "6 meses"),
	UM_ANO(12, "1 ano");
	
	private int cod;
	private String descricao;
	
	private QuatMesesGarantia(int cod, String descricao) {
		this.cod = cod;
		this.descricao = descricao;
	}
	
	public int getCod() {
		return cod;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static QuatMesesGarantia toEnum(Integer cod) {
		if(cod == null) {
			return null;
		}
		
		for (QuatMesesGarantia x : QuatMesesGarantia.values()) {
			if(cod.equals(x.getCod())) {
				return x;
			}
		}
		
		throw new IllegalArgumentException("Id não econtrado: " + cod);
	}
	
}
