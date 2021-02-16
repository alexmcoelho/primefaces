package com.alex.modelo;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DataAtualLocale {
	static Locale locale;
	static GregorianCalendar calendar;
	static SimpleDateFormat formatador = null;

	public static String data() {
		calendar = new GregorianCalendar();
		if(locale == null) {
			locale = new Locale("pt", "BR");
		}
		if(formatador == null) {
			formatador = new SimpleDateFormat("dd/MM/yyyy" + " -" + " " + "h:mm-a", locale);
		}
		return formatador.format(calendar.getTime());
	}
}
