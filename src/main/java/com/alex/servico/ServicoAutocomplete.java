package com.alex.servico;

import java.io.Serializable;

public class ServicoAutocomplete implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String placeholder = "Pesquisa";
	
	private int maxResults = 15;
	
	private int minQueryLength = 3;
	
	private boolean cache = true;
	
	private int cacheTimeout = 30000;
	
	private int queryDelay = 1250;
	
	private boolean forceSelection = true;
	
	private boolean required = true;

	public String getPlaceholder() {
		return placeholder;
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	public int getMinQueryLength() {
		return minQueryLength;
	}

	public void setMinQueryLength(int minQueryLength) {
		this.minQueryLength = minQueryLength;
	}

	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public int getCacheTimeout() {
		return cacheTimeout;
	}

	public void setCacheTimeout(int cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}

	public int getQueryDelay() {
		return queryDelay;
	}

	public void setQueryDelay(int queryDelay) {
		this.queryDelay = queryDelay;
	}

	public boolean isForceSelection() {
		return forceSelection;
	}

	public void setForceSelection(boolean forceSelection) {
		this.forceSelection = forceSelection;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
