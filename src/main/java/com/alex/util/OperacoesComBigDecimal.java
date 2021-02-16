package com.alex.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;

public class OperacoesComBigDecimal {
	
	public static BigDecimal valorZero = BigDecimal.valueOf(0.00);
	
	private OperacoesComBigDecimal() {}
	
	public static BigDecimal soma(BigDecimal num1, BigDecimal num2) {
		return num1.add(num2, MathContext.DECIMAL128)
				.setScale(2, RoundingMode.HALF_EVEN);
	}
	
	public static BigDecimal subtracao(BigDecimal num1, BigDecimal num2) {
		return num1.subtract(num2, MathContext.DECIMAL128)
				.setScale(2, RoundingMode.HALF_EVEN);
	}
	
	public static BigDecimal multiplicacao(BigDecimal num1, BigDecimal num2) {
		return num1.multiply(num2, MathContext.DECIMAL128)
				.setScale(2, RoundingMode.HALF_EVEN);
	}
	
	public static BigDecimal divisao(BigDecimal num1, BigDecimal num2) {
		return num1.divide(num2, MathContext.DECIMAL128)
				.setScale(2, RoundingMode.HALF_EVEN);
	}
	
	public static boolean numUmMaiorQueNumDois(BigDecimal num1, BigDecimal num2) {
		if (num1.compareTo(num2) == 1){
			return true;
		}
		return false;
	}
	
	public static boolean numUmMaiorOuIgualNumDois(BigDecimal num1, BigDecimal num2) {
		if (num1.compareTo(num2) == 1 || (num1.compareTo(num2) != 1 && num2.compareTo(num1) != 1)){
			return true;
		}
		return false;
	}
	
	public static String converteValorTotal(BigDecimal valorBigDecimal) {
		// arredondando para cima
		valorBigDecimal = valorBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
		String valor = "" + valorBigDecimal;
		String valorInteiro = "";
		String vDec = "";

		int contador = 0;
		ArrayList<String> vetor = new ArrayList<>();

		if (valor.length() > 0) {

			valorInteiro = valor.substring(0, valor.length() - 3);
			contador = valorInteiro.length();

			for (int i = 0; i < valorInteiro.length(); i++) {
				vetor.add(valorInteiro.substring(i, i + 1));
			}

			contador = contador - 1;
			int cont2 = 0;

			while (contador >= 0) {
				if ((cont2) == 3) {
					vetor.set(contador, vetor.get(contador) + ".");
					cont2 = 0;
				}
				cont2++;
				contador--;
			}
			valorInteiro = "";
			for (int i = 0; i < vetor.size(); i++) {
				valorInteiro = valorInteiro + vetor.get(i);
			}

			String valorDecimal = valor.substring(vetor.size(), vetor.size() + 3);
			valorDecimal = valorDecimal.replace(".", ",");
			vDec = valorDecimal;
		}
		return valorInteiro + vDec;
	}

}
