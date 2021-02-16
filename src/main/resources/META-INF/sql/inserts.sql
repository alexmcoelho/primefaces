/*INSERTS TABLE USUARIO*/
INSERT INTO gessis01.usuario(ativo, email, nome, senha, login) VALUES (true, 'jean@gmail.com','Jean','[aU:','jean'),(true, 'flavia@gmail.com','Flávia','[aU:','flavia');
/*INSERTS TABLE MENU - 1: Administrador e 2: Operador */
INSERT INTO gessis01.tipo_usuario (tipo)  VALUES ('Administrador'),('Operador');
/*INSERTS TABLE tipousuariousuario - DEFINE O TIPO DE USUARIO*/
INSERT INTO gessis01.tipo_usuario_usuario (tipo_usuario_id, usuario_id) VALUES (1,1),(2,1),(1,2),(2,2);
INSERT INTO gessis01.menu VALUES (1,'fa fa-gavel','Ordem de serviço'),(2,'fa fa-shopping-cart','Venda'),(3,'fa fa-truck','Pedido'),(4,'fa fa-shopping-bag','Financeiro'),(5,'fa fa-leanpub','Estoque'),(6,'fa fa-user-circle','Cadastros gerais'),(7,'fa fa-shopping-cart','Produtos'),(8,'fa fa-paste','Relatário');
/*INSERTS TABLE SUBMENU*/
INSERT INTO gessis01.sub_menu VALUES (30,NULL,'/pages/ordemservico/entrada-os.jsf','Entrada OS',1),(31,NULL,'/pages/ordemservico/lista-os.jsf','Consulta OS',1),(50,NULL,'/pages/pedido/cadastro-pedido.jsf','Criar pedido',3),(51,NULL,'/pages/pedido/lista-pedido.jsf','Lista pedidos',3),(40,'','/pages/saida/cadastro-saida-produto.jsf','Efetuar venda',2),(41,NULL,'/pages/saida/lista-saida-produto.jsf','Listar vendas',2),(60,NULL,'/pages/fatura/gera-fatura.jsf','Gerar faturas',4),(61,NULL,'/pages/fatura/baixa-pagamentos.jsf','Efetuar baixa de pagamentos',4),(62,NULL,'/pages/fatura/lista-parcelas-geradas.jsf','Listar faturas geradas',4),(63,NULL,'/pages/relatorios/detalhes-vendas-relatorio.jsf','Consulta das vendas',4),(70,NULL,'/pages/estoque/cadastro-entrada-produto.jsf','Entrada',5),(71,NULL,'/pages/estoque/lista-entrada-produto.jsf','Listar entradas',5),(100,NULL,'/pages/relatorios/contas-a-receber-relatorio.jsf','Contas à receber',8),(101,NULL,'/pages/relatorios/estoque-relatorio.jsf','Estoque',8),(102,NULL,'/pages/relatorios/impressao-etiquetas.jsf','Impressão etiquetas',8),(80,NULL,'/pages/usuario/lista-usuario.jsf','Usuários',6),(81,NULL,'/pages/produto/lista-produto.jsf','Produtos',6),(82,NULL,'/pages/cliente/lista-cliente.jsf','Clientes',6),(83,NULL,'/pages/modelo/lista-modelo.jsf','Modelos',6),(84,NULL,'/pages/aparelho/lista-aparelho.jsf','Aparelhos',6),(85,NULL,'/pages/fornecedor/lista-fornecedor.jsf','Fornecedores',6),(86,NULL,'/pages/servico/lista-servico.jsf','Serviços',6),(87,NULL,'/pages/categoria/lista-categoria.jsf','Categorias de produtos',6),(10,'fa fa-home','/index.jsf','Início',NULL);
/*INSERTS TABLE TIPOSUSUARIO*/
/*INSERTS TABLE submenutipousuario - DEFINE QUAIS SUBMENUS O TIPO DE USUARIO (OPERARIO, ADMINISTRADOR,...) TERA ACESSO*/
INSERT INTO gessis01.sub_menu_tipo_usuario (sub_menu_id, tipo_usuario_id) VALUES (30,2),(31,2),(40,2),(41,2),(70,2),(71,2),(80,1),(81,1),(60,1),(61,1),(62,1),(63,1),(80,1),(81,2),(82,2),(83,2),(84,2),(85,2),(86,2),(87,2),(100,1),(101,2),(102,2),(50,2),(51,2);
/*INSERTS TABLE perfil - 1: Vendedor e 2: Técnico */
INSERT INTO gessis01.perfil (tipo_perfil) VALUES ('Vendedor');
INSERT INTO gessis01.perfil (tipo_perfil) VALUES ('Técnico');
/*INSERTS TABLE perfilusuario*/
INSERT INTO gessis01.perfil_usuario (perfil_id, usuario_id) VALUES ('1', '1');
INSERT INTO gessis01.perfil_usuario (perfil_id, usuario_id) VALUES ('2', '1');
INSERT INTO gessis01.perfil_usuario (perfil_id, usuario_id) VALUES ('1', '2');
/*INSERTS TABLE email*/
INSERT INTO gessis01.email (email, habilitado_ssl, habilitado_tls, host_name, porta, senha) VALUES ('telecomunicacoeselitte@gmail.com', TRUE, FALSE, 'smtp.googlemail.com', 465, 'cYeDaUs>`d8v');
