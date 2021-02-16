package com.alex.modelo.enums;

public enum Estado{	
	
	ORCAMENTO(1, "Orçamento"), 
	APROVADO(2, "Aprovado/Em andamento"), 
	FINALIZADO(3, "Finalizado"),
	FALTA_PECA(5, "Falta peça"),
	NAO_APROVADO(6, "Não aprovado"), 
	SEM_CONSERTO(7, "Sem conserto"),
	SERVICO_RELIAZADO(9, "Serviço realizado"),
	GARANTIA_ANDAMENTO(10, "Garantia em andamento"),
	GARANTIA_FALTA_PECA(11, "Garantia em falta peça"),
	GARANTIA_CONCLUIDA(12, "Garantia concluída"),
	TODOS(8, "Todos");
	
	private int cod;
	private String descricao;
	
	private Estado(int cod, String descricao) {
		this.cod = cod;
		this.descricao = descricao;
	}
	
	public int getCod() {
		return cod;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public static Estado toEnum(Integer cod) {
		if(cod == null) {
			return null;
		}
		
		for (Estado x : Estado.values()) {
			if(cod.equals(x.getCod())) {
				return x;
			}
		}
		
		throw new IllegalArgumentException("Id não econtrado: " + cod);
	}

}
