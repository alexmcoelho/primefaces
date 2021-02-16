package com.alex.modelo;

public class RegistroFalha {

	private String ip;
	private int tentativa;
	private long ultimaTentativa;

	public static RegistroFalha criarRegistroFalha(String request) {
		RegistroFalha falha = new RegistroFalha();
		falha.setIp(request);
		falha.setTentativa(1);
		falha.setUltimaTentativa(System.currentTimeMillis());
		return falha;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getTentativa() {
		return tentativa;
	}

	public void setTentativa(int tentativa) {
		this.tentativa = tentativa;
	}

	public long getUltimaTentativa() {
		return ultimaTentativa;
	}

	public void setUltimaTentativa(long ultimaTentativa) {
		this.ultimaTentativa = ultimaTentativa;
	}

	public long getTempo() {
		return System.currentTimeMillis() - ultimaTentativa;
	}
}
