package com.alex.modelo.enums;

public enum TipoOperacaoCRUD{	
	
	INSERT_UPDATE(1, "Insert ou update"),
	DELETE(2, "Delete");
	
	private int cod;
	private String descricao;
	
	private TipoOperacaoCRUD(int cod, String descricao) {
		this.cod = cod;
		this.descricao = descricao;
	}
	
	public int getCod() {
		return cod;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static TipoOperacaoCRUD toEnum(Integer cod) {
		if(cod == null) {
			return null;
		}
		
		for (TipoOperacaoCRUD x : TipoOperacaoCRUD.values()) {
			if(cod.equals(x.getCod())) {
				return x;
			}
		}
		
		throw new IllegalArgumentException("Id não econtrado: " + cod);
	}

}
