package com.alex.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

public class CarregaArquivo {
	
	//Se aplicacao for vidalcell vai abrir as configurações de vidalcell, se ser elitte vai abrir as configurações da elitte
	public static Map<String, Object> carregaConfiguracao(String aplicacao) {

		Map<String, Object> map = new HashMap<String, Object>();
		
		InputStream inputstream;
		try {
			String relativeWebPath = FacesContext.getCurrentInstance().getExternalContext()
					.getRealPath("/resources/configuracoes/" + aplicacao + ".txt");
			inputstream = new BufferedInputStream(new FileInputStream(relativeWebPath));

			BufferedReader in = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				map.put(inputLine.replaceAll(":.*", "").toString(), 
						inputLine.replaceAll(".*:", "").trim());
			in.close();
			inputstream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;

	}

}
