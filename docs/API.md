# API Documentation

## Informações Gerais

### URL Base

http://localhost:8082

### Autenticação

Esta API utiliza autenticação JWT.

Endpoints protegidos exigem o envio do token no header:

Authorization: Bearer SEU_TOKEN

#### Permissões Disponíveis

| Permissão | Descrição                |
|-----------|--------------------------|
| ADMIN     | Administrador do Sistema |
| USER      | Usuário padrão           |
| EDITOR    | Pode editar conteúdos    |
| VIEWER    | Apenas visualização      |

---

## Padrão de Erro

Todas as respostas de erro seguem o formato:

{
"timestamp": "2026-05-28T14:30:00",
"status": 400,
"error": "Bad Request",
"message": "Mensagem do erro"
}

---

# Autenticação

## Login

### Endpoint

POST /auth/login

### Permissão

Pública

### Request Body

{
"email": "usuario@email.com",
"senha": "string"
}

#### Campos

| Campo  | Tipo   | Obrigatório | Descrição         |
|--------| ------ | ----------- |-------------------|
| email  | String | Sim         | E-mail do usuário |
| senha  | String | Sim         | Senha do usuário  |

### Response 200 - Sucesso

{
"token": "jwt-token"
}

#### Campos da Response

| Campo     | Tipo   | Descrição                   |
| --------- | ------ | --------------------------- |
| token     | String | Token JWT para autenticação |

### Possíveis Respostas

| Código | Descrição                   |
| ------ | --------------------------- |
| 200    | Login realizado com sucesso |
| 400    | Dados inválidos             |
| 401    | Usuário ou senha inválidos  |

## Registrar Usuário

### Endpoint

POST /auth/register

### Permissão

Pública

### Request Body

{
"email": "joao123@email.com",
"senha": "123456"
}

#### Campos

| Campo | Tipo   | Obrigatório | Descrição         |
|-------| ------ | ----------- |-------------------|
| email | String | Sim         | E-mail do usuário |
| senha | String | Sim         | Senha do usuário  |

### Response 201 - Usuário criado

{
"idUsuario": 550e8400-e29b-41d4-a716-446655440000,
"email": "joao123@email.com",
"roles": ["USER"],
"status": true
}

#### Campos da Response

| Campo     | Tipo          | Descrição             |
|-----------|---------------|-----------------------|
| idUsuario | UUID          | ID do usuário         |
| email     | String        | E-mail do usuário     |
| roles     | Lista de Enum | Permissões do usuário |
| status    | Boolean       | Status ativo/inativo  |

### Possíveis Respostas

| Código | Descrição                  |
| ------ | -------------------------- |
| 201    | Cliente criado com sucesso |
| 400    | Dados inválidos            |
| 409    | Usuário já cadastrado      |

---

# Usuários

## Cadastrar Usuário

### Endpoint

POST /users

### Permissão

ADMIN

### Headers

Authorization: Bearer SEU_TOKEN

### Request Body

{
"email": "joao123@email.com",
"senha": "123456",
"roles": ["USER"]
}

#### Valores possíveis para permissão

| Valor  |
|--------|
| ADMIN  |
| USER   |
| EDITOR |
| VIEWER |

### Response 201

{
"idUsuario": 550e8400-e29b-41d4-a716-446655440000,
"email": "joao123@email.com",
"roles": ["USER"],
"status": true
}

## Excluir Usuário

### Endpoint

DELETE /users/{idUsuario}

### Permissão

ADMIN

### Response 204

Sem conteúdo.

## Listar Usuários

### Endpoint

GET /users

### Permissão

ADMIN

### Query Params

| Param | Tipo    | Obrigatório | Descrição             |
|-------| ------- | ----------- |-----------------------|
| page  | Integer | Não         | Página atual          |
| size  | Integer | Não         | Quantidade por página |
| sort  | String  | Não         | Campo de ordenação    |
| email | String  | Não         | Filtro por e-mail     |

### Exemplo

GET /users?page=0&size=10&email=joao

### Response 200

{
"content": [
{
"idUsuario": 1,
"email": "joao123@email.com",
"status": true
}
],
"totalElements": 1,
"totalPages": 1,
"size": 10,
"number": 0
}

## Buscar Usuário por ID

### Endpoint

GET /users/{idUsuario}

### Permissão

ADMIN

### Response 200

{
"idUsuario": 550e8400-e29b-41d4-a716-446655440000,
"email": "joao123@email.com",
"roles": ["CLIENTE"],
"status": true
}

## Buscar Usuário Logado

### Endpoint

GET /users/me

### Permissão

Usuário autenticado

### Response 200

{
"idUsuario": 550e8400-e29b-41d4-a716-446655440000,
"email": "joao123@email.com",
"roles": ["CLIENTE"],
"status": true
}

## Atualizar E-mail do Usuário por ID

### Endpoint

PATCH /users/{idUsuario}/email

### Permissão

ADMIN

### Request Body

{
"email": "novo_email"
}

### Response 200

{
"idUsuario": 550e8400-e29b-41d4-a716-446655440000,
"email": "joao123@email.com",
"roles": ["CLIENTE"],
"status": true
}

## Atualizar E-mail do Usuário Logado

### Endpoint

PATCH /users/me/email

### Permissão

Usuário autenticado

### Request Body

{
"email": "novo_email"
}

## Atualizar Status do Usuário por ID

### Endpoint

PATCH /users/{idUsuario}/status

### Permissão

ADMIN

### Request Body

{
"status": true
}

## Atualizar Status do Usuário Logado

### Endpoint

PATCH /users/me/status

### Permissão

Usuário autenticado

### Request Body

{
"status": false
}

## Atualizar Senha do Usuário por ID

### Endpoint

PATCH /users/{idUsuario}/senha

### Permissão

ADMIN

### Request Body

{
"senhaNova": "12345678"
}

### Response 200

{
"message": "Senha atualizada com sucesso."
}

## Atualizar Senha do Usuário Logado

### Endpoint

PATCH /users/me/senha

### Permissão

Usuário autenticado

### Request Body

{
"senhaAtual": "123456",
"senhaNova": "987654"
}

### Response 200

{
"message": "Senha atualizada com sucesso."
}

## Atualizar Permissão do Usuário

### Endpoint

PATCH /users/{idUsuario}/roles

### Permissão

ADMIN

### Request Body

{
"roles": ["USER"]
}

### Valores possíveis

| Valor  |
|--------|
| ADMIN  |
| USER   |
| EDITOR |
| VIEWER |

### Response 200

{
"idUsuario": 550e8400-e29b-41d4-a716-446655440000,
"email": "joao123@email.com",
"roles": ["CLIENTE"],
"status": true
}

---

## Possíveis Erros do Módulo de Usuários

| Código | Descrição                    |
| ------ | ---------------------------- |
| 400    | Dados inválidos              |
| 401    | Não autenticado              |
| 403    | Sem permissão                |
| 404    | Usuário não encontrado       |
| 409    | Violação de regra de negócio |

---