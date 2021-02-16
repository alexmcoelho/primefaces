package com.alex.controle.criptografia;

/*import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;*/
import java.util.Base64;

import com.alex.util.EncriptaDecriptaDES;

public class CriptografiaBase64 {
	
	/*private static MessageDigest algorithm;
	private static byte messageDigestSenhaAdmin[];
	private static StringBuilder hexStringSenhaAdmin;
	private static String senhahex = null;*/
	private static byte[] decodedBytes;

	/*public static String criptografia(String texto) {

		senhahex = null;
		try {
			algorithm = MessageDigest.getInstance("SHA-256");
			messageDigestSenhaAdmin = algorithm.digest(texto.getBytes("UTF-8"));

			hexStringSenhaAdmin = new StringBuilder();
			for (byte b : messageDigestSenhaAdmin) {
				hexStringSenhaAdmin.append(String.format("%02X", 0xFF & b));
			}
			senhahex = hexStringSenhaAdmin.toString();

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return senhahex;
	}*/
	
	public static String encode(String texto) {
		if(texto != null && texto.length() > 0) {
			return Base64.getEncoder().encodeToString(texto.getBytes());
		}
		return null;
	}
	
	public static String decode(String texto) {
		if(texto != null && texto.length() > 0) {
			decodedBytes = Base64.getDecoder().decode(texto);
			return new String(decodedBytes);
		}
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String teste = encode("1");
		EncriptaDecriptaDES encriptaDecriptaDES = new EncriptaDecriptaDES();
		System.out.println(encriptaDecriptaDES.cifrar(teste, 10));
		teste = encriptaDecriptaDES.cifrar(teste, 10);
		System.out.println("Cripgrafada: " + teste);
		teste = encriptaDecriptaDES.decifrar(teste, 10);
		teste = decode(teste);
		System.out.println(teste);
	}
	

}
