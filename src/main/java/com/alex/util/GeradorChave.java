package com.alex.util;

import java.util.Random;

public class GeradorChave {
	public static String[] letras = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
		    "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
		  };
	
	public static String formaKey() {
		String key = "";
		Random gerador = new Random();
		for (int i = 0; i < 3; i++) {
			key += letras[gerador.nextInt(letras.length)];
		}
		for (int i = 0; i < 3; i++) {
			key += gerador.nextInt(9);
		}
		return key;
	}
}
