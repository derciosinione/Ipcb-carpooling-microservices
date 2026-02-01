# Sistema de Microserviços de Carpooling

Este projeto é uma plataforma de carpooling baseada em arquitetura de microserviços, desenvolvida com Java e Spring Boot. O sistema utiliza Docker para containerização e orquestração dos serviços.

## 🚀 Tecnologias Utilizadas

- **Java**: 21
- **Spring Boot**: 3.4.1
- **Spring Cloud**: 2024.0.0
- **Base de Dados**: PostgreSQL (via Docker)
- **Containerização**: Docker & Docker Compose
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway

## 🏗️ Arquitetura

O sistema é composto pelos seguintes serviços:

| Serviço | Porta | Descrição |
|---------|-------|-----------|
| **Service Discovery** | `8761` | Servidor Eureka para registo e descoberta de serviços. |
| **Cloud Gateway** | `8888` | Gateway único de entrada para a API. |
| **Identity Service** | `8080` | Gestão de utilizadores e autenticação. |
| **Vehicles Service** | `8081` | Gestão de veículos e marcas. |
| **Database** | `5432` | Instância PostgreSQL partilhada. |

## 🛠️ Como Executar

### Pré-requisitos
- Docker e Docker Compose instalados.
- Java 21 (opcional, apenas para desenvolvimento local fora do Docker).

### Execução Automática (Recomendado)

Foi criado um script de conveniência que cria a rede Docker, compila os projetos e inicia todos os contentores.

1. Dê permissão de execução ao script (apenas na primeira vez):
   ```bash
   chmod +x start_all.sh
   # Executar Docker Compose (Standard)
   ./start_all.sh
   ```

### Execução com Docker Swarm (Orquestração & Portainer)

Para utilizar orquestração, réplicas e gestão visual com Portainer:

1. Dê permissão de execução:
   ```bash
   chmod +x deploy_swarm.sh
   ```

2. Execute o script de deployment:
   ```bash
   ./deploy_swarm.sh
   ```
   *Este script inicializa o Swarm (se necessário), constrói as imagens e faz o deploy da stack.*

3. **Portainer (Gestão Visual)**:
   - Aceda a [http://localhost:9000](http://localhost:9000)
   - Crie a conta de administrador inicial.
   - Gerencie o cluster "local".

### Execução Manual (Docker Compose)

Se preferir executar passo a passo:

1. **Criar a rede partilhada:**
   ```bash
   docker network create carpooling_network
   ```

2. **Iniciar a Base de Dados:**
   ```bash
   docker compose -f carpooling_docker_compose_db/docker-compose.yml up -d --build
   ```

3. **Iniciar o Service Discovery:**
   ```bash
   docker compose -f service-discovery/docker-compose.yml up -d --build
   ```

4. **Iniciar os Microserviços:**
   ```bash
   docker compose -f identity/docker-compose.yml up -d --build
   docker compose -f vehicles/docker-compose.yml up -d --build
   docker compose -f cloud-gateway-service/docker-compose.yml up -d --build
   ```

## 🔍 Verificar o Estado

Após iniciar, pode aceder aos painéis e APIs:

- **Eureka Dashboard**: [http://localhost:8761](http://localhost:8761)
  - Verifique se `CAR-POOLING-IDENTITY-API`, `CAR-POOLING-VEHICLES-API` e `CLOUD-GATEWAY-SERVICE` estão registados.

- **API Gateway**: [http://localhost:8888](http://localhost:8888)
  - Rotas disponíveis (exemplo):
    - `/identity/**` -> Redireciona para Identity Service
    - `/vehicles/**` -> Redireciona para Vehicles Service

- **Identity API**: [http://localhost:8080](http://localhost:8080)
- **Vehicles API**: [http://localhost:8081](http://localhost:8081)

---

### 📖 Documentação de Endpoints
Para uma lista detalhada de todos os endpoints disponíveis via Gateway, consulte o ficheiro:
👉 **[endpoints.md](file:///Users/derciosinione/Developer/IPCB/AD/TrabalhoPratico/endpoints.md)**

## 📦 Estrutura do Projeto

- `/carpooling_docker_compose_db`: Configuração da base de dados.
- `/cloud-gateway-service`: API Gateway.
- `/identity`: Microserviço de Identidade.
- `/service-discovery`: Servidor Eureka.
- `/vehicles`: Microserviço de Veículos.
- `/trips`: Microserviço de Viagens.
- `/payments`: Microserviço de Pagamentos.
- `start_all.sh`: Script de automação.
