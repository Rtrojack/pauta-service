# pauta-service #

Serviço para criação e votação de pautas dos associados.

### Tecnologias utilizadas

- [Java 11](https://www.oracle.com/java/)

- [Spring Boot](https://spring.io/projects/spring-boot)
  
- [MongoDb](https://www.mongodb.com/)

- [Gradle](https://gradle.org/)

- [Swagger 2](https://springfox.github.io/springfox/)
  
- [Docker](https://www.docker.com/)

- [IntelliJ IDEA Community Edition](https://www.jetbrains.com/pt-br/idea/)

## Pré-requisitos

- Docker e Docker Compose instalados.

## Utilizando Docker

    $ docker-compose up --build -d 

- Após download das imagens e compilação do projeto, acesse http://localhost:8080/swagger-ui.html

## Ambiente de demonstração

O serviço foi deployado em uma máquina EC2, na AWS.

Acesse: [http://pauta-service.trojack.com.br](http://pauta-service.trojack.com.br/swagger-ui.html)