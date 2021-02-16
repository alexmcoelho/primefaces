package com.alex.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListadeInfoTabelas {
	
	private static List<InfoTable> listaCompleta;
	private static InfoTable infoTable;
	private static List<InfoTable> resultado = new ArrayList<InfoTable>();
	
	private ListadeInfoTabelas() {
        
    }
	
	public static void preencheLista() {
		listaCompleta = new ArrayList<InfoTable>(Arrays.asList(
                new InfoTable("aparelho","marca","UK_MARCA"),// 1
                new InfoTable("cliente","cpf_cnpj","UK_CPF_CNPJ"), // 2
                new InfoTable("cliente","email","UK_EMAIL_CLIENTE"), // 3
                new InfoTable("cliente_telefone","telefone_id","UK_lal8v8169xallcg2ji5nrlgtx"),
                new InfoTable("cliente_telefone","cliente_id","FK9mctflnsv94li2fjlvcskl8tu"),
                new InfoTable("cliente_telefone","telefone_id","FKc8jcmhn73io2avuma7ypqh11c"),
                new InfoTable("detalhes_conta_a_pagar","entrada_de_produtos_id","FKocn8eq9onearfc14qmxd879qr"),
                new InfoTable("detalhes_fatura","ordem_de_servico_id","FK_DETALHES_FATURA_PARA_ORDEM_DE_SERVICO"), // 4
                new InfoTable("detalhes_fatura","saida_de_produtos_id","FK_DETALHES_FATURA_PARA_SAIDA_DE_PRODUTOS"), // 5
                new InfoTable("entrada_de_produtos","codigo_nota","UK_CODIGO_NOTA"), // 6
                new InfoTable("entrada_de_produtos","fornecedor_id","FK_ENTRADA_DE_PRODUTOS_PARA_FORNECEDOR"), // 7
                new InfoTable("fornecedor","email","UK_EMAIL_FORNECEDOR"), // 8
                new InfoTable("fornecedor_telefone","telefone_id","UK_7a5h2fc6dpxmsccjbigo3wdc4"),
                new InfoTable("fornecedor_telefone","fornecedor_id","FKiwwrcvjlgw9xn5v3uasblv0me"),
                new InfoTable("fornecedor_telefone","telefone_id","FKki7rt3h9rljho8lqr7mycx70p"),
                new InfoTable("item_entrada","entrada_de_produtos_id","FK_ITEM_ENTRADA_PARA_ENTRADA_DE_PRODUTOS"), // 9 esse está como CASCADE
                new InfoTable("item_entrada","produto_id","FK_ITEM_ENTRADA_PARA_PRODUTO"), // 10
                new InfoTable("item_prod_saida","produto_id","FK_ITEM_PROD_SAIDA_PARA_PRODUTO"), // 11
                new InfoTable("item_prod_saida","saida_de_produtos_id","FK_ITEM_PROD_SAIDA_PARA_SAIDA_DE_PRODUTOS"), // 12 esse está como CASCADE
                new InfoTable("item_produto","ordem_de_servico_id","FK_ITEM_PRODUTO_PARA_ORDEM_DE_SERVICO"), // 13 esse está como CASCADE
                new InfoTable("item_produto","produto_id","FK_ITEM_PRODUTO_PARA_PRODUTO"), // 14 
                new InfoTable("modelo","modelo","UK_MODELO"), // 15 
                new InfoTable("modelo","aparelho_id","FK_MODELO_PARA_APARELHO"), // 16
                new InfoTable("ordem_de_servico","cliente_id","FK_ORDEM_DE_SERVICO_PARA_CLIENTE"), // 17 
                new InfoTable("ordem_de_servico","modelo_id","FK_ORDEM_DE_SERVICO_PARA_MODELO"), // 18 
                new InfoTable("ordem_de_servico","servico_id","FK_ORDEM_DE_SERVICO_PARA_SERVICO"), // 19 
                new InfoTable("ordem_de_servico","usuario_id","FK_ORDEM_DE_SERVICO_PARA_USUARIO"), // 20 
                new InfoTable("perfil","tipo_perfil","UK_TIPO_PERFIL"), // 21 
                new InfoTable("perfil_usuario","perfil_id","FKehpyjfa7ypmqsalbhmba2hdg1"),
                new InfoTable("perfil_usuario","usuario_id","FKp00rgyejcbm4s19gwux7uymem"),
                new InfoTable("produto","descricao","UK_DESCRICAO"), // 22
                new InfoTable("saida_de_produtos","cliente_id","FK_SAIDA_DE_PRODUTOS_PARA_CLIENTE"), // 23
                new InfoTable("saida_de_produtos","usuario_id","FK_SAIDA_DE_PRODUTOS_PARA_USUARIO"), // 24 
                new InfoTable("servico","descricao","UK_DESCRICAO_SERVICO"), // 25 
                new InfoTable("sub_menu","menu_id","FK_SUB_MENU_PARA_MENU"), // 26 
                new InfoTable("sub_menu_tipo_usuario","sub_menu_id","FK5jym8c60xky8fn0qf8jc6sie4"),
                new InfoTable("sub_menu_tipo_usuario","tipo_usuario_id","FKmcp11tqd0y4uxexfrkxmh5xg4"),
                new InfoTable("tipo_usuario","tipo","UK_TIPO_USUARIO"), // 27 
                new InfoTable("tipo_usuario_usuario","usuario_id","FKdhypjaoy5gsnj1apqh7501xfy"),
                new InfoTable("tipo_usuario_usuario","tipo_usuario_id","FKmuptevx678ce90ul5wo4qcehv"),
                new InfoTable("usuario","login","UK_LOGIN"), // 28 
                new InfoTable("usuario","email","UK_EMAIL_USUARIO"), // 29
                new InfoTable("item_pedido","pedido_id","FK_ITEM_PEDIDO_PARA_PEDIDO"), // 30
                new InfoTable("item_pedido","produto_id","FK_ITEM_PEDIDO_PARA_PRODUTO"), // 31
                new InfoTable("item_pedido","usuario_id","FK_ITEM_PEDIDO_PARA_USUARIO"), // 32
                new InfoTable("item_servico","ordem_de_servico_id","FK_ITEM_SERVICO_PARA_ORDEM_DE_SERVICO"), // 33
                new InfoTable("item_servico","servico_id","FK_ITEM_SERVICO_PARA_SERVICO"), // 34
                new InfoTable("categoria","descricao","UK_DESCRICAO_CATEGORIA"), // 35
                new InfoTable("produto","categoria_id","FK_PRODUTO_PARA_CATEGORIA"), // 36
                new InfoTable("produto","imei","UK_IMEI_PRODUTO"), // 37
                new InfoTable("produto","descrição","UK_DESCRICAO_LOWER_CASE") // 38
                ));
        setaPhrase("UK_MARCA",                                  "A marca informada"); // 1
        setaPhrase("UK_CPF_CNPJ",                               "O CPF/CNPJ informado"); // 2
        setaPhrase("UK_EMAIL_CLIENTE",                          "O email informado"); // 3
        setaPhrase("FK_DETALHES_FATURA_PARA_ORDEM_DE_SERVICO",  "uma fatura"); // 4
        setaPhrase("FK_DETALHES_FATURA_PARA_SAIDA_DE_PRODUTOS", "uma fatura"); // 5
        setaPhrase("UK_CODIGO_NOTA",                            "O código nota informado"); // 6
        setaPhrase("FK_ENTRADA_DE_PRODUTOS_PARA_FORNECEDOR",    "uma entrada de produto(s)"); // 7
        setaPhrase("UK_EMAIL_FORNECEDOR",                       "O email informado"); // 8
        setaPhrase("FK_ITEM_ENTRADA_PARA_ENTRADA_DE_PRODUTOS",  "um dos itens de uma entrada de produtos"); //9 Não seria necessário, pois essa relação está como CASCADE
        setaPhrase("FK_ITEM_ENTRADA_PARA_PRODUTO",              "um dos itens de uma entrada de produtos"); // 10
        setaPhrase("FK_ITEM_PROD_SAIDA_PARA_PRODUTO",           "um dos itens de uma venda"); // 12
        setaPhrase("FK_ITEM_PROD_SAIDA_PARA_SAIDA_DE_PRODUTOS", "um dos itens de uma venda"); // 13 Não seria necessário, pois essa relação está como CASCADE
        setaPhrase("FK_ITEM_PRODUTO_PARA_ORDEM_DE_SERVICO",     "um dos itens de uma ordem de serviço"); // 14 Não seria necessário, pois essa relação está como CASCADE
        setaPhrase("FK_ITEM_PRODUTO_PARA_PRODUTO",              "um dos itens de uma ordem de serviço"); // 15
        setaPhrase("UK_MODELO",                                 "O modelo informado"); // 16
        setaPhrase("FK_MODELO_PARA_APARELHO",                   "um modelo de celular"); // 17
        setaPhrase("FK_ORDEM_DE_SERVICO_PARA_CLIENTE",          "uma ordem de serviço"); // 18
        setaPhrase("FK_ORDEM_DE_SERVICO_PARA_MODELO",           "uma ordem de serviço"); // 19
        setaPhrase("FK_ORDEM_DE_SERVICO_PARA_SERVICO",          "uma ordem de serviço"); // 20
        setaPhrase("UK_TIPO_PERFIL",                            "O tipo de perfil informado"); // 21
        setaPhrase("UK_DESCRICAO",                              "A descrição informada"); // 22
        setaPhrase("FK_SAIDA_DE_PRODUTOS_PARA_CLIENTE",         "uma venda"); // 23
        setaPhrase("FK_SAIDA_DE_PRODUTOS_PARA_USUARIO",         "uma venda"); // 24
        setaPhrase("UK_DESCRICAO_SERVICO",                      "A descrição informada"); // 25
        setaPhrase("FK_SUB_MENU_PARA_MENU",                     "um sub-menu"); // 26
        setaPhrase("UK_TIPO_USUARIO",                           "O tipo de usuário informado"); // 27
        setaPhrase("UK_LOGIN",                                  "O login informado"); // 28
        setaPhrase("UK_EMAIL_USUARIO",                          "O email informado"); // 29
        setaPhrase("FK_ITEM_PEDIDO_PARA_PEDIDO",                "um dos itens de um pedido"); // 30
        setaPhrase("FK_ITEM_PEDIDO_PARA_PRODUTO",               "um dos itens de um pedido"); // 31
        setaPhrase("FK_ITEM_PEDIDO_PARA_USUARIO",               "um dos itens de um pedido"); // 32
        setaPhrase("FK_ITEM_SERVICO_PARA_ORDEM_DE_SERVICO",     "um dos itens de uma ordem de serviço"); // 33
        setaPhrase("FK_ITEM_SERVICO_PARA_SERVICO",              "um dos serviços feitos em uma ordem de serviço"); // 34
        setaPhrase("UK_DESCRICAO_CATEGORIA",                    "A descrição informada"); // 35
        setaPhrase("FK_PRODUTO_PARA_CATEGORIA",                 "um produto"); // 36
        setaPhrase("UK_IMEI_PRODUTO",                           "O IMEI informado"); // 37
        setaPhrase("UK_DESCRICAO_LOWER_CASE",                   "A descrição informada"); // 38
	}
	
	public static InfoTable pesquisaItem(String nameConstraint) {
		
		if(listaCompleta == null || listaCompleta.size() == 0) {
			preencheLista();
		}
		
		resultado = listaCompleta.stream()                // convert list to stream
                .filter(line -> nameConstraint.equalsIgnoreCase(line.getNameConstraint()))     // we dont like nameConstraint
                .collect(Collectors.toList());
		if(resultado != null && resultado.size() > 0) {
			return resultado.get(0);
		}
		return null;
	}
	
	public static void setaPhrase(String constraint, String phrase) {
		infoTable = new InfoTable();
		infoTable.setNameConstraint(constraint);
		int i = listaCompleta.indexOf(infoTable);
		if(i != -1) {
			listaCompleta.get(i).setComplemenPhrase(phrase);
		}
	}

}
