# SistemaGerenciamentoBackend

Este é o backend do **Sistema de Gerenciamento**, desenvolvido com **Spring Boot** e **MySQL**. Ele fornece uma API RESTful para gerenciar usuários, projetos, atividades e lançamentos de horas.

## Requisitos

- Java 17 ou superior instalado
- MySQL instalado e rodando
- Maven instalado
- Postman ou Insomnia para testar os endpoints

## Começando

1. Clone o repositório e acesse a pasta do projeto:

```bash
git clone https://github.com/HermesLeonardo/ConectaTraineeBackend
cd ConectaTraineeBackend
```


Configure o banco de dados MySQL e crie o banco chamado sistema_gerenciamento.

No arquivo src/main/resources/application.properties, configure as credenciais do banco de dados:

```properties
spring.application.name=ConectaTraineeBackend

spring.datasource.url=jdbc:mysql://localhost:3306/sistema_gerenciamento
spring.datasource.username=root
spring.datasource.password=SUA_SENHA

```

Após configurar o banco de dados e o arquivo application.properties, execute a aplicação com o seguinte comando:

```bash
mvn spring-boot:run
```

##
### **Usuário Admin Padrão**

Por padrão, um usuário admin será criado automaticamente quando a aplicação for iniciada, com as seguintes credenciais:

- Email: admin@admin.com
- Senha: senhasegura

##

**Tabelas no Banco de Dados**

As tabelas necessárias para o funcionamento do sistema serão criadas automaticamente pela JPA/Hibernate na inicialização da aplicação. As tabelas são:

- usuarios: Armazena informações de usuários.
- projetos: Armazena informações de projetos.
- atividades: Armazena as atividades vinculadas aos projetos.
- lancamentos_horas: Controla os registros de horas trabalhadas nas atividades.

### Endpoints Principais

**🔑 Autenticação**
- POST /auth/login: Autentica um usuário e retorna um token JWT.
- POST /auth/register: Cria um novo usuário.

**👤 Usuários**
- GET /usuarios: Lista todos os usuários (apenas admin).
- GET /usuarios/{id}: Obtém detalhes de um usuário.
- POST /usuarios: Cria um novo usuário.
- PUT /usuarios/{id}: Atualiza um usuário.
- DELETE /usuarios/{id}: Remove um usuário.

**📌 Projetos**
- GET /projetos: Lista todos os projetos.
- GET /projetos/{id}: Obtém detalhes de um projeto.
- POST /projetos: Cria um novo projeto.
- PUT /projetos/{id}: Atualiza um projeto.
- DELETE /projetos/{id}: Remove um projeto.

**✅ Atividades**
- GET /atividades: Lista todas as atividades.
- GET /atividades/{id}: Obtém detalhes de uma atividade.
- POST /atividades: Cria uma nova atividade.
- PUT /atividades/{id}: Atualiza uma atividade.
- DELETE /atividades/{id}: Remove uma atividade.

***⏳ Lançamento de Horas**
- GET /lancamentos: Lista todos os lançamentos de horas.
- GET /lancamentos/{id}: Obtém detalhes de um lançamento.
- POST /lancamentos: Registra horas trabalhadas.
- PUT /lancamentos/{id}: Atualiza um lançamento.
- DELETE /lancamentos/{id}: Remove um lançamento.

##

### Autenticação e Segurança 

A API usa JWT (JSON Web Token) para autenticação. Após um login bem-sucedido, o token JWT será retornado. Esse token deve ser enviado no header Authorization das requisições para acessar as rotas protegidas.

Exemplo de Header:

```http
Authorization: Bearer SEU_TOKEN_JWT
```

##

### Testes
Para testar a API, você pode usar o Postman ou o Insomnia. Basta importar a coleção de testes disponível no repositório para testar os endpoints de forma fácil.

### Testando Endpoints via Postman:

**Login: Envie um POST para /auth/login com as credenciais do usuário.**
```
http://localhost:8080/api/auth/login
```

Usuários: Teste os endpoints de CRUD (criação, leitura, atualização e exclusão) para os usuários.

Projetos: Teste a criação, atualização, exclusão e listagem de projetos.**

Atividades e Lançamentos de Horas: Teste as funcionalidades de gerenciamento de atividades e horas.

## 

***Considerações Finais***

JWT: O token de autenticação tem um tempo de expiração, portanto, você pode precisar se autenticar novamente depois de um período.
