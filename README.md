# User Service

## Sobre o Projeto

O **User Service** é uma aplicação desenvolvida em **Java com Spring Boot**, responsável pelo gerenciamento de usuários e autenticação da aplicação.

O projeto implementa autenticação e autorização utilizando **Spring Security** e **JWT (JSON Web Token)**, fornecendo uma base segura para o controle de acesso aos recursos da API.

A aplicação segue uma arquitetura organizada por responsabilidades, contendo módulos específicos para tratamento de exceções, configurações globais, utilitários e funcionalidades relacionadas ao domínio de usuários.

> **Observação:** Atualmente o projeto implementa apenas o módulo de usuários, servindo como base para a expansão futura de novos domínios e microsserviços.

---

## Tecnologias Utilizadas

* Java
* Spring Boot
* Spring Data JPA
* Spring Validation
* Spring Security
* JWT (JSON Web Token)
* PostgreSQL
* Spring Boot Actuator
* Maven

---

## Dependências Principais

### Persistência de Dados

* Spring Data JPA
* PostgreSQL Driver

### Validação

* Spring Validation

### API REST

* Spring Web MVC

### Segurança

* Spring Security
* Java JWT (Auth0)

### Monitoramento

* Spring Boot Actuator

### Testes

* Spring Data JPA Test
* Spring Validation Test
* Spring Security Test
* Spring Web MVC Test
* Spring Actuator Test

---

## Estrutura do Projeto

```text
src/main/java
│
├── br.com.exceptions
│   └── Tratamento global de exceções da aplicação
│
├── br.com.utils
│   └── Classes utilitárias compartilhadas
│
├── br.com.configuration
│   └── Configurações globais da aplicação
│
└── br.com.user_service
    └── Módulo principal responsável pelas funcionalidades de usuários
```

---

## Funcionalidades

* Cadastro de usuários
* Consulta de usuários
* Atualização de usuários
* Exclusão de usuários
* Autenticação via JWT
* Controle de acesso baseado em perfis (Roles)
* Validação de dados de entrada
* Tratamento padronizado de exceções
* Monitoramento da aplicação através do Actuator

---

## Segurança

A aplicação utiliza o Spring Security integrado com JWT para garantir:

* Autenticação segura de usuários;
* Proteção de endpoints;
* Controle de permissões através de Roles;
* Sessões stateless utilizando tokens JWT.

---

## Documentação da API

A documentação completa dos endpoints encontra-se no arquivo:

[Documentação da API](docs/API.md)

Neste documento estão descritos:

* Endpoints disponíveis;
* Métodos HTTP suportados;
* Estruturas de requisição e resposta;
* Regras de autenticação;
* Códigos de retorno e mensagens de erro.

---

## Banco de Dados

O projeto utiliza PostgreSQL como banco de dados relacional através do Spring Data JPA.

---

## Monitoramento

O Spring Boot Actuator foi configurado para disponibilizar informações sobre:

* Saúde da aplicação;
* Métricas;
* Informações do ambiente;
* Monitoramento operacional.

---

## Objetivo

Este projeto foi desenvolvido com o objetivo de fornecer uma base sólida para gerenciamento de usuários em aplicações Java modernas, seguindo boas práticas de:

* Arquitetura em camadas;
* Segurança com Spring Security;
* Autenticação baseada em JWT;
* Tratamento centralizado de exceções;
* Organização modular do código;
* Desenvolvimento de APIs REST.

---