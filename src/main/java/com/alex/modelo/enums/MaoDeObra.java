package com.alex.modelo.enums;

public enum MaoDeObra{	
	
	SEM_PRODUTO(0, "Sem produto"),
	COM_PRODUTO(1, "Com produto");
	
	private int cod;
	private String descricao;
	
	private MaoDeObra(int cod, String descricao) {
		this.cod = cod;
		this.descricao = descricao;
	}
	
	public int getCod() {
		return cod;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static MaoDeObra toEnum(Integer cod) {
		if(cod == null) {
			return null;
		}
		
		for (MaoDeObra x : MaoDeObra.values()) {
			if(cod.equals(x.getCod())) {
				return x;
			}
		}
		
		throw new IllegalArgumentException("Id não econtrado: " + cod);
	}

}
