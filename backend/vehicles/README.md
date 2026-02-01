# Vehicles API

Microserviço responsável pela gestão de veículos na plataforma de Car Pooling. Esta API permite que os utilizadores registem, consultem e gerenciem os seus veículos.

## 📋 Índice

- [Visão Geral](#visao-geral)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pre-requisitos)
- [Configuração](#configuracao)
- [Como Rodar](#como-rodar)
    - [Localmente](#localmente)
    - [Docker](#docker)
- [Documentação da API](#documentacao-da-api)

## 🚀 Visão Geral <a name="visao-geral"></a>

A **Vehicles API** faz parte do ecossistema de Car Pooling e foca na entidade "Veículo".  
Suporta operações de CRUD (Create, Read, Update, Delete) e integra-se com serviços de autenticação via JWT para garantir que apenas proprietários possam gerenciar seus veículos.

## 🛠 Tecnologias Utilizadas <a name="tecnologias-utilizadas"></a>

- **Java 21**
- **Spring Boot 3.4.1**
    - Spring Web
    - Spring Data JPA
    - Spring Security
- **PostgreSQL 42.7.8**
- **Lombok**
- **SpringDoc OpenAPI** (Swagger UI)
- **Docker**

## 📋 Pré-requisitos <a name="pre-requisitos"></a>

Antes de começar, certifique-se de ter instalado:

- [Java JDK 21](https://www.oracle.com/java/technologies/downloads/#java21)
- [Maven](https://maven.apache.org/) - Opcional se usar o wrapper `mvnw`
- [Docker](https://www.docker.com/) & Docker Compose
- [PostgreSQL](https://www.postgresql.org/) (Apenas se rodar sem Docker)

## ⚙️ Configuração <a name="configuracao"></a>

As configurações da aplicação residem no arquivo `src/main/resources/application.properties`.

**Variáveis de Ambiente Importantes (Defaults):**

| Variável | Descrição | Valor Padrão |
| :--- | :--- | :--- |
| `server.port` | Porta da aplicação | `8081` |
| `spring.datasource.url` | URL de Agendamento do Banco | `jdbc:postgresql://localhost:5432/vehicles_db` |
| `spring.datasource.username` | Utilizador do Banco | `admin` |
| `spring.datasource.password` | Senha do Banco | `learnJava!2025` |

## 🏃 Como Rodar <a name="como-rodar"></a>

### Localmente <a name="localmente"></a>

1. **Configurar a Base de Dados:**
   Certifique-se de que o PostgreSQL está a correr na porta `5432` e crie um banco de dados chamado `vehicles_db`.

2. **Executar a Aplicação:**
   Na raiz do projeto, execute:
   ```bash
   ./mvnw clean spring-boot:run
   ```

### Docker <a name="docker"></a>

Para rodar a aplicação em containers, utilize o `docker-compose.yml` fornecido.

> **Nota:** O ficheiro `docker-compose.yml` atual espera que o serviço de banco de dados (`db_server`) esteja disponível na rede `carpooling_network`. Certifique-se de que esse serviço está a correr ou ajuste o arquivo para incluir um serviço postgres.

Comando para subir o container da API:

```bash
docker-compose up -d --build
```

A API estará disponível em `http://localhost:8081`.

## 📖 Documentação da API <a name="documentacao-da-api"></a>

A documentação interativa (Swagger UI) pode ser acessada através do navegador:

```
http://localhost:8081/swagger-ui.html
```
