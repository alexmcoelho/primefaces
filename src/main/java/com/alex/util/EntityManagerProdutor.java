package com.alex.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.alex.controle.criptografia.CriptografiaBase64;

@ApplicationScoped
public class EntityManagerProdutor {

	private EntityManagerFactory factory;
	private Map<String, String> properties = new HashMap<String, String>();
	FacesContext context = FacesContext.getCurrentInstance();
	public static Date dataInicio;
	public static Date dataFim;
	private Calendar calendar;

	public EntityManagerProdutor() {

		properties.put("javax.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/gessis");
		properties.put("javax.persistence.jdbc.user", "power_user");
		if (Dominio.retornaDominio(FacesContext.getCurrentInstance()).contains("vidalcell")) {
			properties.put("hibernate.default_schema", "vidalcell");
		} else if (Dominio.retornaDominio(FacesContext.getCurrentInstance()).contains(".tk")) {
			properties.put("hibernate.default_schema", "gessis_teste");
		} else {
			properties.put("hibernate.default_schema", "gessis01");
		}

		if (context.getApplication().getProjectStage().equals(ProjectStage.Development)) {
			properties.put("javax.persistence.jdbc.password", "1234");
		} else {
			properties.put("javax.persistence.jdbc.password",
					CriptografiaBase64.decode(EncriptaDecriptaDES.decifrar("hGjC\\UY;[aaw", 10)));
		}

		this.factory = Persistence.createEntityManagerFactory("crudPU", properties);

		/* Calcula primeiro dia do mes e ultimo dia do mes */
		if (calendar == null) {
			calendar = Calendar.getInstance();
		}
		int ultimoDiaDoMes = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), ultimoDiaDoMes);
		dataFim = calendar.getTime();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
		dataInicio = calendar.getTime();
	}

	@Produces
	@RequestScoped
	public EntityManager createEntityManager() {
		return this.factory.createEntityManager();
	}

	public void closeEntityManager(@Disposes EntityManager manager) {
		manager.close();
	}

}
