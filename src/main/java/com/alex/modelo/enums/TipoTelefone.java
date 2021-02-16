package com.alex.modelo.enums;

public enum TipoTelefone {
	CELULAR(1, "Celular"),
	RESIDENCIAL(2, "Residencial"),
	TRABALHO(3, "Trabalho"),
	PARENTE(4, "Parente"),
	MORA_MESMA_RESIDENCIA(5, "Mora na mesma residência"),
	VIZINHO(6, "Vizinho");
	
	private int cod;
	private String descricao;
	
	private TipoTelefone(int cod, String descricao) {
		this.cod = cod;
		this.descricao = descricao;
	}
	
	public int getCod() {
		return cod;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static TipoTelefone toEnum(Integer cod) {
		if(cod == null) {
			return null;
		}
		
		for (TipoTelefone x : TipoTelefone.values()) {
			if(cod.equals(x.getCod())) {
				return x;
			}
		}
		
		throw new IllegalArgumentException("Id não econtrado: " + cod);
	}
}
