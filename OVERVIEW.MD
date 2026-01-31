# 🚗 Projeto: Plataforma de Partilha de Viagens (Car Pooling)

## 1️⃣ Overview (Visão Geral)

O projeto consiste no desenvolvimento de uma **aplicação web baseada em microserviços** que simula uma plataforma de  **partilha de viagens (Car Pooling)** , onde:

* Condutores publicam viagens com origem, destino, data/hora e lugares disponíveis
* Passageiros pesquisam viagens compatíveis
* Os utilizadores podem reservar boleias
* O sistema calcula automaticamente a divisão de custos
* Todo o sistema é seguro, escalável e containerizado

O objetivo principal é  **promover mobilidade sustentável** , reduzir custos e diminuir emissões, utilizando tecnologias como:

* Spring Boot
* Spring Cloud
* Docker
* Arquitetura de Microserviços
* APIs REST
* Front-end Web Responsivo

b05ef4fc-1254-4a2b-b51e-7814cec…

---

## 2️⃣ O que o Sistema de Car Pooling Deve Fazer

### Funcionalidades Nucleares

* Registo e autenticação de utilizadores
* Definição de perfil: passageiro, condutor ou ambos
* Criação de viagens por condutores
* Pesquisa de viagens por passageiros
* Pedido e confirmação de boleia
* Gestão do estado da viagem (aberta, em curso, concluída)
* Cálculo automático de custos (combustível, portagens, energia)
* Histórico de viagens e pagamentos
* Avaliação (rating) entre utilizadores

b05ef4fc-1254-4a2b-b51e-7814cec…

---

## 3️⃣ Casos de Uso Essenciais (Car Pooling)

### UC1 – Criar Viagem

Condutor cria viagem com:

* Origem
* Destino
* Data/hora
* Lugares disponíveis

### UC2 – Pesquisar Viagens

Passageiro pesquisa por:

* Origem
* Destino
* Data

### UC3 – Reservar Boleia

* Passageiro envia pedido
* Condutor confirma

### UC4 – Calcular Custos

* Sistema divide custo total pelos ocupantes

### UC5 – Histórico

* Utilizador consulta viagens realizadas e valores pagos

b05ef4fc-1254-4a2b-b51e-7814cec…

---

## 4️⃣ Microserviços Necessários (apenas Car Pooling)

### 🔐 Microserviço de Autenticação e Perfis

* Registo
* Login
* Papéis: ADMIN, CONDUTOR, PASSAGEIRO
* Atualização de perfil
* Ratings

### 🚘 Microserviço de Viagens

* Criar viagem
* Pesquisar
* Reservar
* Alterar estado

### 💰 Microserviço de Custos e Pagamentos

* Calcular custo total
* Dividir por passageiros
* Registar histórico

### 📍 Microserviço de Localização/GPS (simulado)

* Guardar coordenadas
* Calcular distância
* Sugerir viagens próximas

### 📢 Microserviço de Notificações (opcional)

* Confirmação de reserva
* Cancelamentos

### 🧑‍💼 Microserviço de Administração

* Ver utilizadores
* Bloquear contas
* Estatísticas


## 5️⃣ Arquitetura Recomendada

* Comunicação REST
* Feign Client
* Circuit Breakers
* Instâncias replicáveis
* Docker Compose


## 6️⃣ Tecnologias a Utilizar

* Spring Boot
* Spring Security
* Spring Cloud Gateway
* Eureka Server
* OpenFeign
* Docker
* Thymeleaf ou Front-end Web simples
* Base de Dados externa (PostgreSQL)


## 7️⃣ O Que Deve Ser Feito (Plano de Execução)

### Etapa 1 – Modelação

* Entidades (User, Trip, Reservation, Payment)

### Etapa 2 – Criar Infraestrutura

* Eureka Server
* API Gateway

### Etapa 3 – Criar Microserviços

* Auth Service
* Trip Service
* Cost Service
* GPS Service

### Etapa 4 – Segurança

* JWT
* Roles

### Etapa 5 – Front-End

* Páginas:
  * Login
  * Registo
  * Criar Viagem
  * Pesquisar Viagem
  * Reservar
  * Histórico

### Etapa 6 – Dockerização

* Dockerfile para cada serviço
* docker-compose.yml

### Etapa 7 – Testes e Demonstração

* Swagger
* Postman Collection
* Demonstração funcional
