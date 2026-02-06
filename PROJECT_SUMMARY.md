# IPCB Car Pooling - Resumo do Projeto

## Integrantes do Grupo
- Aluno numero 20220103 Dercio Sinione Domingos
- Aluno numero 20221985 Joel Dialamicua

## Visao Geral
Plataforma de partilha de boleias baseada em microservicos, com frontend web, autenticacao por JWT e orquestracao via Docker Swarm. O sistema cobre:
- registo e autenticacao de utilizadores;
- perfis e papeis (Condutor, Passageiro e Administrador);
- publicacao e pesquisa de viagens;
- reservas e aprovacoes;
- simulacao de pagamentos e gestao de despesas;
- integracao de localizacao (GPS);
- notificacoes;
- painel administrativo com monitorizacao e gestao global.

## Arquitetura de Microservicos (Backend)

### 1) Service Discovery (Eureka)
Responsavel por:
- registo automatico de todos os servicos;
- descoberta dinamica (service registry);
- base para balanceamento e resiliencia.

### 2) Cloud Gateway
Responsavel por:
- ponto unico de entrada para todas as APIs;
- roteamento para microservicos por prefixo;
- centralizacao de seguranca e politicas;
- exposicao de endpoints publicos e privados.

### 3) Identity Service
Responsavel por:
- autenticacao (login/registro) e JWT;
- gestao de utilizadores e perfis (Condutor, Passageiro, Admin);
- atribuicao/remocao de papeis;
- avaliacao/ratings entre utilizadores;
- endpoints administrativos (criar admin, bloquear/ativar contas).

### 4) Vehicles Service
Responsavel por:
- registo e gestao de veiculos;
- listagem de marcas;
- associacao de veiculo ao condutor.

### 5) Trips Service
Responsavel por:
- criacao e listagem de viagens;
- pesquisa e sugestao de viagens;
- reservas e gestao de pedidos (aceitar/rejeitar/cancelar);
- gestao de despesas e custo por pessoa;
- metricas de viagens por perfil;
- estado da viagem (ex: aberta, iniciada, concluida).

### 6) Payments Service
Responsavel por:
- simulacao de pagamentos por viagem;
- registo e consulta de pagamentos;
- apoio a relatorios financeiros.

### 7) GPS Service
Responsavel por:
- pesquisa de locais;
- reverse geocoding;
- calculo de distancia;
- gestao de localizacoes recentes do utilizador;
- suporte a itinerarios e mapa.

### 8) Notifications Service
Responsavel por:
- criacao e entrega de notificacoes;
- marcacao como lida;
- eliminacao;
- filtragem por utilizador (isolamento de dados).

## Frontend (Web)
Aplicacao Spring Boot + Thymeleaf com:
- area publica (pesquisa e detalhes);
- dashboards de Passageiro e Condutor;
- publicacao de viagem em etapas;
- paginas de notificacoes e pagamentos;
- area de perfil;
- painel administrativo com paginas separadas:
  - utilizadores;
  - viagens;
  - veiculos;
  - pagamentos;
  - relatorios;
  - monitorizacao.

## Orquestracao (Docker Swarm)
A stack e implantada por Docker Swarm com:
- todos os microservicos;
- frontend;
- monitorizacao e logs;
- descoberta via Eureka;
- rede overlay compartilhada.

Arquivo principal:
- `backend/docker-stack.yml`

Ponto unico do gateway:
- `http://localhost:8888`

Rotas base por servico:
- `/identity`, `/vehicles`, `/trips`, `/payments`, `/gps`, `/notifications`

## Endpoints e Documentacao
Todos os endpoints, URLs, Swagger e Actuator estao documentados em:
- `Endpoints.md`

## Monitorizacao e Observabilidade
Incluido na stack:
- **Prometheus** para metricas: `http://localhost:9090`
- **Grafana** para dashboards: `http://localhost:3000`
- **Loki + Promtail** para logs centralizados
- **Actuator** para health/metrics/circuit breakers

## Como Executar (Swarm)
Script recomendado:
- `backend/deploy_swarm.sh`

Este script:
- faz build das imagens;
- garante base de dados e redes;
- faz deploy da stack completa.
