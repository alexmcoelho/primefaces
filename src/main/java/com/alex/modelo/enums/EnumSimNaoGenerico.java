package com.alex.modelo.enums;

public enum EnumSimNaoGenerico{	
	
	NAO(0, "Não"),
	SIM(1, "Sim");
	
	private int cod;
	private String descricao;
	
	private EnumSimNaoGenerico(int cod, String descricao) {
		this.cod = cod;
		this.descricao = descricao;
	}
	
	public int getCod() {
		return cod;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static EnumSimNaoGenerico toEnum(Integer cod) {
		if(cod == null) {
			return null;
		}
		
		for (EnumSimNaoGenerico x : EnumSimNaoGenerico.values()) {
			if(cod.equals(x.getCod())) {
				return x;
			}
		}
		
		throw new IllegalArgumentException("Id não econtrado: " + cod);
	}

}
