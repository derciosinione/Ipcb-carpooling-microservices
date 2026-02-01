# Service Discovery

## Resumo
Este projeto é um servidor de Service Discovery baseado no **Spring Cloud Netflix Eureka**. Ele atua como um registro central onde microsserviços se registam e descobrem a localização uns dos outros para comunicação, facilitando a arquitetura de sistemas distribuídos e eliminando a necessidade de configuração estática de endereços.

## Acesso
Após iniciar a aplicação, o dashboard do Eureka estará disponível em:
[http://localhost:8761](http://localhost:8761)

## Como Rodar

### Pré-requisitos
- Java 21
- Maven

### Instruções

1. **Abrir o terminal na raiz do projeto.**

2. **Compilar e instalar dependências:**
   ```bash
   ./mvnw clean install
   ```

3. **Executar a aplicação:**
   Com o Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   
   Ou, após a compilação, rodando o JAR gerado:
   ```bash
   java -jar target/service-discovery-0.0.1-SNAPSHOT.jar
   ```

## Registar Microserviços

Para registar os teus microserviços (`identity-api`, `vehicle-api`, etc.) no Service Discovery, adiciona as seguintes configurações ao ficheiro `application.properties` ou `application.yml` de cada serviço.

### Dependência (Maven) de Cliente Eureka
Certifica-te que tens a dependência `spring-cloud-starter-netflix-eureka-client` no `pom.xml` dos teus microserviços.

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

### Configuração para Identity API (Porta 8080)

**No `application.properties`:**
```properties
spring.application.name=identity-api
server.port=8080

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

**Ou no `application.yml`:**
```yaml
spring:
  application:
    name: identity-api

server:
  port: 8080

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

### Configuração para Vehicle API (Porta 8081)

**No `application.properties`:**
```properties
spring.application.name=vehicle-api
server.port=8081

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true
```

**Ou no `application.yml`:**
```yaml
spring:
  application:
    name: vehicle-api

server:
  port: 8081

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```
