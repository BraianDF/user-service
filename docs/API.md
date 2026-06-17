# API Documentation

## Informações Gerais

### URL Base

```http
http://localhost:8082
```

### Autenticação

Esta API utiliza autenticação JWT.

Endpoints protegidos exigem o envio do token no header:

```http
Authorization: Bearer SEU_TOKEN
```

#### Permissões Disponíveis

| Permissão | Descrição                |
|-----------|--------------------------|
| ADMIN     | Administrador do Sistema |
| USER      | Usuário padrão           |
| EDITOR    | Pode editar conteúdos    |
| VIEWER    | Apenas visualização      |

### Endpoints Públicos

Os seguintes endpoints podem ser acessados sem autenticação:

| Método | Endpoint            | Descrição                     |
|---------|--------------------|-------------------------------|
| GET     | /auth/login        | Autenticação de usuário       |
| POST    | /auth/register     | Cadastro público              |
| GET     | /actuator/health   | Verificação de saúde da API   |
| GET     | /actuator/info     | Informações da aplicação      |

---

## Padrão de Erro

Todas as respostas de erro seguem o formato:

```json
{
"timestamp": "2026-05-28T14:30:00",
"status": 409,
"error": "Violação de regra de negócio",
"message": "Este e-mail já está sendo utilizado."
}
```

### Possíveis Erros

| Código | Erro                             | Situação                          |
|--------|----------------------------------|-----------------------------------|
| 400    | Erro de validação                | Dados inválidos                   |
| 400    | JSON inválido                    | Corpo da requisição mal formatado |
| 400    | Parâmetro inválido               | Tipo de parâmetro incorreto       |
| 401    | Não autenticado                  | Token ausente ou inválido         |
| 401    | Erro de autenticação             | Login inválido                    |
| 403    | Acesso negado                    | Usuário sem permissão             |
| 403    | Usuário desabilitado             | Usuário bloqueado                 |
| 404    | Recurso não encontrado           | Registro inexistente              |
| 404    | Rota não encontrada              | Endpoint inexistente              |
| 409    | Violação de regra de negócio     | Regra da aplicação violada        |
| 409    | Violação de integridade de dados | Restrição do banco                |
| 500    | Erro interno do servidor         | Erro inesperado                   |

---

# Monitoramento

Endpoints utilizados para monitoramento da aplicação.

## Health Check

Retorna o estado geral da aplicação.

### Endpoint

```http
GET /actuator/health
```

### Permissão

Pública

### Descrição

Este endpoint é utilizado por ferramentas de monitoramento, balanceadores de carga e orquestradores (Docker, Kubernetes, etc.) para verificar se a aplicação está disponível.

Como a configuração:

```properties
management.endpoint.health.show-details=never
```

está habilitada, apenas o status geral da aplicação é retornado.

### Response 200 - Aplicação saudável

```json
{
  "status": "UP"
}
```

### Possíveis Respostas

| Código | Descrição                    |
|---------|-----------------------------|
| 200     | Aplicação disponível        |
| 503     | Aplicação indisponível      |

---

## Informações da Aplicação

Retorna informações públicas da aplicação.

### Endpoint

```http
GET /actuator/info
```

### Permissão

Pública

### Descrição

Este endpoint disponibiliza informações institucionais da aplicação configuradas através do Spring Boot Actuator.

As informações retornadas dependem das propriedades configuradas no projeto.

### Exemplo de Response 200

```json
{
  "app": {
    "name": "user-service",
    "version": "1.0.0",
    "description": "Servico de gerenciamento de usuarios"
  }
}
```

### Possíveis Respostas

| Código | Descrição                          |
|---------|------------------------------------|
| 200     | Informações retornadas com sucesso |

---

## Regras de Negócio

### Usuários

- O e-mail deve ser único.
- O e-mail é utilizado como login.
- O e-mail não diferencia letras maiúsculas e minúsculas.
- O status define se o usuário pode acessar o sistema.
- Um usuário pode possuir múltiplas roles.

### Cadastro Público

O endpoint:

```http
POST /auth/register
```

sempre cria usuários com a role:

```json
["USER"]
```

As permissões não podem ser informadas pelo usuário.

### Senhas

- As senhas são armazenadas criptografadas.
- Não é permitido informar senha vazia.
- A troca de senha do usuário autenticado exige a senha atual.

---

# Autenticação

## Login

### Endpoint

```http
POST /auth/login
```

### Permissão

Pública

### Request Body

```json
{
"email": "usuario@email.com",
"senha": "string"
}
```

#### Campos

| Campo  | Tipo   | Obrigatório | Descrição         |
|--------| ------ | ----------- |-------------------|
| email  | String | Sim         | E-mail do usuário |
| senha  | String | Sim         | Senha do usuário  |

### Response 200 - Sucesso

```json
{
"token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

#### Campos da Response

| Campo     | Tipo   | Descrição                   |
| --------- | ------ | --------------------------- |
| token     | String | Token JWT para autenticação |

#### Informações do Token

O token JWT contém:

| Campo   | Descrição                    |
|---------|------------------------------|
| sub     | UUID do usuário              |
| roles   | Permissões do usuário        |
| iat     | Data de emissão              |
| exp     | Data de expiração            |
| jti     | Identificador único do token |
| iss     | user-service                 |

### Possíveis Respostas

| Código | Descrição                   |
| ------ | --------------------------- |
| 200    | Login realizado com sucesso |
| 400    | Dados inválidos             |
| 401    | Usuário ou senha inválidos  |

## Registrar Usuário

### Regras

- O e-mail deve ser único.
- O e-mail será normalizado para minúsculo.
- A role inicial será sempre USER.

### Endpoint

```http
POST /auth/register
```
        
### Permissão

Pública

### Request Body

```json
{
"email": "joao123@email.com",
"senha": "123456"
}
```

#### Campos

| Campo | Tipo   | Obrigatório | Descrição         |
|-------| ------ | ----------- |-------------------|
| email | String | Sim         | E-mail do usuário |
| senha | String | Sim         | Senha do usuário  |

### Response 201 - Usuário criado

```json
{
"idUsuario": "550e8400-e29b-41d4-a716-446655440000",
"email": "joao123@email.com",
"roles": ["USER"],
"status": true
}
```

#### Campos da Response

| Campo     | Tipo        | Descrição            |
|-----------|-------------|----------------------|
| idUsuario | UUID        | ID do usuário        |
| email     | String      | E-mail do usuário    |
| roles     | Array<Role> | Lista de permissões  |
| status    | Boolean     | Status ativo/inativo |

### Possíveis Respostas

| Código | Descrição                  |
| ------ |----------------------------|
| 201    | Usuário criado com sucesso |
| 400    | Dados inválidos            |
| 409    | Usuário já cadastrado      |

---

# Usuários

## Cadastrar Usuário

### Endpoint

```http
POST /users
```

### Permissão

ADMIN

### Headers

```http
Authorization: Bearer SEU_TOKEN
```

### Request Body

```json
{
"email": "joao123@email.com",
"senha": "123456",
"roles": ["USER"]
}
```

#### Valores possíveis para permissão

| Valor  |
|--------|
| ADMIN  |
| USER   |
| EDITOR |
| VIEWER |

### Response 201

```json
{
"idUsuario": "550e8400-e29b-41d4-a716-446655440000",
"email": "joao123@email.com",
"roles": ["USER"],
"status": true
}
```

## Excluir Usuário

### Endpoint

```http
DELETE /users/{idUsuario}
```

### Permissão

ADMIN

### Possíveis Respostas

| Código | Descrição              |
|--------|------------------------|
| 204    | Usuário removido       |
| 401    | Não autenticado        |
| 403    | Sem permissão          |
| 404    | Usuário não encontrado |

## Listar Usuários

### Endpoint

```http
GET /users
```

### Permissão

ADMIN

### Query Params

| Param | Tipo    | Obrigatório | Descrição                    |
|-------| ------- | ----------- |------------------------------|
| page  | Integer | Não         | Página atual                 |
| size  | Integer | Não         | Quantidade por página        |
| sort  | String  | Não         | Campo e direção de ordenação |
| email | String  | Não         | Filtro parcial por e-mail    |

### Exemplos

```http
GET /users?page=0&size=10
```

```http
GET /users?page=0&size=10&email=joao
```

```http
GET /users?page=0&size=10&sort=email,asc
```

```http
GET /users?page=0&size=10&sort=email,desc
```

### Response 200

```json
{
"content": [
{
"idUsuario": "550e8400-e29b-41d4-a716-446655440000",
"email": "joao123@email.com",
"status": true
}
],
"totalElements": 1,
"totalPages": 1,
"size": 10,
"number": 0
}
```

## Buscar Usuário por ID

### Endpoint

```http
GET /users/{idUsuario}
```

### Permissão

ADMIN

### Response 200

```json
{
"idUsuario": "550e8400-e29b-41d4-a716-446655440000",
"email": "joao123@email.com",
"roles": ["USER"],
"status": true
}
```

## Buscar Usuário Logado

### Endpoint

```http
GET /users/me
```

### Permissão

Usuário autenticado

### Response 200

```json
{
"idUsuario": "550e8400-e29b-41d4-a716-446655440000",
"email": "joao123@email.com",
"roles": ["USER"],
"status": true
}
```

## Atualizar E-mail do Usuário por ID

### Regras

- O e-mail não pode estar vazio.
- O e-mail deve ser único.
- Caso o e-mail informado seja igual ao atual, nenhuma alteração será realizada.

### Endpoint

```http
PATCH /users/{idUsuario}/email
```

### Permissão

ADMIN

### Request Body

```json
{
"email": "novo_email"
}
```

### Response 200

```json
{
"idUsuario": "550e8400-e29b-41d4-a716-446655440000",
"email": "joao123@email.com",
"roles": ["USER"],
"status": true
}
```

## Atualizar E-mail do Usuário Logado

### Regras

- O e-mail não pode estar vazio.
- O e-mail deve ser único.
- Caso o e-mail informado seja igual ao atual, nenhuma alteração será realizada.

### Endpoint

```http
PATCH /users/me/email
```

### Permissão

Usuário autenticado

### Request Body

```json
{
"email": "novo_email"
}
```

## Atualizar Status do Usuário por ID

### Regras

- O status é obrigatório.
- Caso o status informado seja igual ao atual, nenhuma alteração será realizada.

### Endpoint

```http
PATCH /users/{idUsuario}/status
```

### Permissão

ADMIN

### Request Body

```json
{
"status": true
}
```

### Response 200

```json
{
"idUsuario": "550e8400-e29b-41d4-a716-446655440000",
"email": "joao123@email.com",
"roles": ["USER"],
"status": true
}
```

## Atualizar Status do Usuário Logado

### Regras

- O status é obrigatório.
- Caso o status informado seja igual ao atual, nenhuma alteração será realizada.

### Endpoint

```http
PATCH /users/me/status
```

### Permissão

Usuário autenticado

### Request Body

```json
{
"status": false
}
```

### Response 200

```json
{
"idUsuario": "550e8400-e29b-41d4-a716-446655440000",
"email": "joao123@email.com",
"roles": ["USER"],
"status": true
}
```

## Atualizar Senha do Usuário por ID

### Regras

- A nova senha é obrigatória.
- Caso a nova senha seja igual à atual, nenhuma alteração será realizada.

### Endpoint

```http
PATCH /users/{idUsuario}/senha
```

### Permissão

ADMIN

### Request Body

```json
{
"senhaNova": "12345678"
}
```

### Response 200

```json
{
"message": "Senha atualizada com sucesso."
}
```

## Atualizar Senha do Usuário Logado

### Regras

- A senha atual deve ser informada.
- A senha atual deve ser válida.
- A nova senha é obrigatória.
- Caso a nova senha seja igual à atual, nenhuma alteração será realizada.

### Endpoint

```http
PATCH /users/me/senha
```

### Permissão

Usuário autenticado

### Request Body

```json
{
"senhaAtual": "123456",
"senhaNova": "987654"
}
```

### Response 200

```json
{
"message": "Senha atualizada com sucesso."
}
```

## Possíveis Erros

| Código | Descrição             |
|--------|-----------------------|
| 401    | Senha atual inválida  |

## Atualizar Permissão do Usuário

### Regras

- A lista de roles é obrigatória.
- Deve conter ao menos uma role.
- Caso as roles sejam iguais às atuais, nenhuma alteração será realizada.

### Endpoint

```http
PATCH /users/{idUsuario}/roles
```

### Permissão

ADMIN

### Request Body

```json
{
"roles": ["USER"]
}
```

### Valores possíveis

| Valor  |
|--------|
| ADMIN  |
| USER   |
| EDITOR |
| VIEWER |

### Response 200

```json
{
"idUsuario": "550e8400-e29b-41d4-a716-446655440000",
"email": "joao123@email.com",
"roles": ["USER"],
"status": true
}
```

---