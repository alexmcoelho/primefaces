package com.alex.modelo.enums;

public enum TipoConstraint{	
	
	FK(1, "FK"),
	UK(2, "UK");
	
	private int cod;
	private String descricao;
	
	private TipoConstraint(int cod, String descricao) {
		this.cod = cod;
		this.descricao = descricao;
	}
	
	public int getCod() {
		return cod;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static TipoConstraint toEnum(Integer cod) {
		if(cod == null) {
			return null;
		}
		
		for (TipoConstraint x : TipoConstraint.values()) {
			if(cod.equals(x.getCod())) {
				return x;
			}
		}
		
		throw new IllegalArgumentException("Id não econtrado: " + cod);
	}

}
