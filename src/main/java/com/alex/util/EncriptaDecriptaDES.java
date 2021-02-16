package com.alex.util;

import java.text.Normalizer;

import com.alex.controle.criptografia.CriptografiaBase64;

public class EncriptaDecriptaDES {

	private String senha = "1234";

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public static String removerAcentos(String str) {
		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
	
	/* Método que vai ajudar a formar a frase criptografada, fazendo a
	troca de letra por letra*/
	public static char substCifrar(char letraInicial, int k) {
				
		char let = (char) (letraInicial + k);

		if (letraInicial == ' ')
			return ' ';

		if (let > 'z') {
			int delta = let - 'z';
			let = (char) ('0' + delta - 1);
		}
		return let;
	}

	// Método que vai formar a frase completa - criptografada
	public static String cifrar(String palavra, int k) {
		String result = "";
		char temp[] = removerAcentos(palavra).toCharArray();

             k = k + palavra.length();

             while (k > 37) {
		     k = k - 36;
		}

		for (int a = 0; a < palavra.length(); a++) {
		     result += substCifrar(temp[a], k - a);
		}
		return result;
	}

	public static char substDecifrar(char letraInicial, int k) {

		char let = (char) (letraInicial - k);

		if (letraInicial == ' ')
			return ' ';

		if (let < '0') {
			int delta = '0' - let;
			let = (char) ('z' - delta + 1);
		}
		return let;
	}

	public static String decifrar(String palavra, int k) {
		String result = "";
		char temp[] = removerAcentos(palavra).toCharArray();

		k = k + palavra.length();

		while (k > 37) {
			k = k - 36;
		}

		for (int a = 0; a < palavra.length(); a++) {
			result += substDecifrar(temp[a], k - a);
		}
		return result;
	}
	
	public static void main(String[] args) {
		String texto = "045873El$";
		texto = CriptografiaBase64.encode(texto);
		texto = EncriptaDecriptaDES.cifrar(texto, 10);
		System.out.println("Criptografada: " + texto);
		
		texto = EncriptaDecriptaDES.decifrar(texto, 10);
		texto = CriptografiaBase64.decode(texto);
		System.out.println("Descriptografada: " + texto);
	}

}