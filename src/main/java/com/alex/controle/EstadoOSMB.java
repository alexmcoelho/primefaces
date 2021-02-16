package com.alex.controle;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.alex.modelo.DataAtualLocale;
import com.alex.modelo.OrdemDeServico;
import com.alex.servico.ForcaBrutaControl;
import com.alex.servico.OrdemDeServicoLazyDataModel;
import com.alex.util.Dominio;
import com.alex.util.FacesUtil;
import com.alex.util.ManipuladorArquivo;

@Named
@ViewScoped
public class EstadoOSMB implements Serializable {

	private static final long serialVersionUID = 1L;

	//public static final String url = "https://www.google.com/recaptcha/api/siteverify";
	//public static final String secret = "6LcA_rgUAAAAAPsxws68dWOsJE587T1_9E7JV5ux";
	//private final static String USER_AGENT = "Mozilla/5.0";
	@Inject
	private OrdemDeServicoLazyDataModel model;
	@Inject
	private OrdemDeServico ordemDeServico;
	private String situacao = "";
	private boolean bloqueiaBotao = false;
	
	private long tempoMilisegundos = 1200000;
	private int numeroTentativas = 3;
	private HttpServletRequest request;
	private String enderecoIp;

	public static boolean verify(String gRecaptchaResponse) throws IOException {
		if (gRecaptchaResponse == null || "".equals(gRecaptchaResponse)) {
			return false;
		}
		
		try {
			/*
			 * URL obj = new URL(url); HttpsURLConnection con = (HttpsURLConnection)
			 * obj.openConnection(); // add reuqest header con.setRequestMethod("POST");
			 * con.setRequestProperty("User-Agent", USER_AGENT);
			 * con.setRequestProperty("Accept-Language", "en-US,en;q=0.5"); String
			 * postParams = "secret=" + secret + "&response=" + gRecaptchaResponse; // Send
			 * post request con.setDoOutput(true); DataOutputStream wr = new
			 * DataOutputStream(con.getOutputStream()); wr.writeBytes(postParams);
			 * wr.flush(); wr.close(); int responseCode = con.getResponseCode();
			 * System.out.println("\nSending 'POST' request to URL : " + url);
			 * System.out.println("Post parameters : " + postParams);
			 * System.out.println("Response Code : " + responseCode); BufferedReader in =
			 * new BufferedReader(new InputStreamReader(con.getInputStream())); String
			 * inputLine; StringBuffer response = new StringBuffer(); while ((inputLine =
			 * in.readLine()) != null) { response.append(inputLine); } in.close(); // print
			 * result System.out.println(response.toString()); // parse JSON response and
			 * return 'success' value JsonReader jsonReader = Json.createReader(new
			 * StringReader(response.toString())); JsonObject jsonObject =
			 * jsonReader.readObject(); jsonReader.close(); return
			 * jsonObject.getBoolean("success");
			 */
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String mostrarEstado() {
		/*
		 * String gRecaptchaResponse =
		 * FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap
		 * () .get("g-recaptcha-response"); boolean valida = false; try { valida =
		 * verify(gRecaptchaResponse); } catch (IOException e) { // TODO Auto-generated
		 * catch block e.printStackTrace(); }
		 */
		String texto;
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		request = (HttpServletRequest) externalContext.getRequest();
		enderecoIp = request.getHeader("X-Real-IP") != null ? request.getHeader("X-Real-IP") : request.getRemoteHost();
		
		/*INICIO - salvando log de ips que tem usar essa função*/
		texto = ManipuladorArquivo.lerArquivo(FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/configuracoes/log.csv"));
		if(Dominio.retornaDominio(FacesContext.getCurrentInstance()).contains(Dominio.contemDominio)) {
			texto += "vidalcell;estado;";
		}
		else {
			texto += "elitte;estado;";
		}
		texto += enderecoIp + ";" + DataAtualLocale.data() + ";";
		texto += "codigo:" + ordemDeServico.getId() + "-chave:" + ordemDeServico.getChave(); 
		ManipuladorArquivo.escritor(FacesContext.getCurrentInstance().getExternalContext()
				.getRealPath("/resources/configuracoes/log.csv"), texto);
		/*FIM - salvando log de ips que tem usar essa função*/
		
		ForcaBrutaControl control = null;
		
		if (externalContext.getSessionMap().get("ForcaBrutaControl") == null) {
			control = new ForcaBrutaControl(tempoMilisegundos, numeroTentativas);
			externalContext.getSessionMap().put("ForcaBrutaControl", control);
		} else {
			control = (ForcaBrutaControl) externalContext.getSessionMap().get("ForcaBrutaControl");
		}
		
		if (!control.podeLoggar(enderecoIp)) {
			FacesUtil.addWarnMessage("Por motivos de segurança seu IP está bloqueado, aguarde " 
					+ control.minutosRestantes(enderecoIp) + " minutos e tente novamente.", "");
			return "";
		}
		
		if(!bloqueiaBotao) {
			model.setOrdemDeServico(model.getObjDAO().porIdAndChave(ordemDeServico.getId(), ordemDeServico.getChave()));
		}
		if(model.getObjDAO().isControle()) {
			bloqueiaBotao = true;
			situacao = "\r\n<ul>\r\n" + 
					"											<!--#aparelho-->\r\n" +
					"											<!--#situacao-->\r\n" + 
					"											<!--#defeitoInformado-->\r\n" +
					"                                           <!--#laudoTecnico-->\r\n" + 
					"											<!--#observacoes-->\r\n" +
					"										</ul>\r\n";
			
			situacao = situacao.replace("<!--#aparelho-->", "<li>\r\n"
					+ "												<p style=\"text-align: justify;\"><b>Aparelho:</b> <br/>"
					+ model.getOrdemDeServico().getModelo().getAparelho().getMarca() + " - " 
					+ model.getOrdemDeServico().getModelo().getModelo() + ".\r\n"
					+ "												</p>\r\n"
					+ "											</li>");
			situacao = situacao.replace("<!--#situacao-->", "<li>\r\n"
					+ "												<p style=\"text-align: justify;\"><b>Situação:</b> <br/>"
					+ model.getOrdemDeServico().getEstado().getDescricao() + ".\r\n"
					+ "												</p>\r\n"
					+ "											</li>");
			situacao = situacao.replace("<!--#defeitoInformado-->", "<li>\r\n"
					+ "												<p style=\"text-align: justify;\"><b>Defeito informado:</b> <br/>"
					+ model.getOrdemDeServico().getDefeitoInformado() + ".\r\n"
					+ "												</p>\r\n"
					+ "											</li>");
			if (model.getOrdemDeServico().getLaudoTecnico() != null && model.getOrdemDeServico().getLaudoTecnico().length() > 0) {
				situacao = situacao.replace("<!--#laudoTecnico-->", "<li>\r\n"
						+ "												<p style=\"text-align: justify;\"><b>Laudo técnico:</b> <br/>"
						+ model.getOrdemDeServico().getLaudoTecnico() + ".\r\n"
						+ "												</p>\r\n"
						+ "											</li>");
			}
			// Faz essa validação pois o a observação pode ser null
			if (model.getOrdemDeServico().getObservacoes() != null && model.getOrdemDeServico().getObservacoes().length() > 0) {
				situacao = situacao.replace("<!--#observacoes-->", "<li>\r\n"
						+ "												<p style=\"text-align: justify;\"><b>Observações:</b> <br/>"
						+ model.getOrdemDeServico().getObservacoes() + ".\r\n"
						+ "												</p>\r\n"
						+ "											</li>");
			}
		}
		else {
			FacesUtil.addWarnMessage(
					"Não foi encontrado nenhuma ordem de serviço com essas informações!",
					"");
			control.registraFalha(enderecoIp);
		}
		return null;
		
	}

	public OrdemDeServicoLazyDataModel getModel() {
		return model;
	}

	public void setModel(OrdemDeServicoLazyDataModel model) {
		this.model = model;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public boolean isBloqueiaBotao() {
		return bloqueiaBotao;
	}

	public void setBloqueiaBotao(boolean bloqueiaBotao) {
		this.bloqueiaBotao = bloqueiaBotao;
	}

	public OrdemDeServico getOrdemDeServico() {
		return ordemDeServico;
	}

	public void setOrdemDeServico(OrdemDeServico ordemDeServico) {
		this.ordemDeServico = ordemDeServico;
	}

}
