package com.alex.util;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ManipuladorArquivo {

	public static String lerArquivo(String relativeWebPath) {
		File file = new File(relativeWebPath);
		if(!file.exists()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		InputStream inputstream;
		try {
			inputstream = new BufferedInputStream(new FileInputStream(relativeWebPath));

			BufferedReader in = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				inputLine = inputLine.replaceAll("\\t", ";");
				builder.append(inputLine).append("\n");
			}
			in.close();
			inputstream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return builder.toString();

	}

	public static void escritor(String path, String texto) {
		BufferedWriter buffWrite;
		try {
			buffWrite = new BufferedWriter(new FileWriter(path));
			buffWrite.append(texto + "\n");
			buffWrite.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println(ManipuladorArquivo.lerArquivo("elitte.csv"));
		String texto = ManipuladorArquivo.lerArquivo("elitte.csv");
		String novoTexto = texto + "elitte;130.0.0.1;12-03-2016";
		ManipuladorArquivo.escritor("elitte.csv", novoTexto);
		System.out.println("Novo texto: " + ManipuladorArquivo.lerArquivo("elitte.csv"));
	}

}
