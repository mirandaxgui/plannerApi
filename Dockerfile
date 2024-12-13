# Etapa de construção
FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .

RUN apt-get install maven -y
RUN mvn clean install

# Etapa de execução
FROM openjdk:17-jdk-slim

# Exponha as portas necessárias (8080 e 443)
EXPOSE 8080 443

# Copiar o arquivo JAR gerado na construção
COPY --from=build /target/planner-0.0.1-SNAPSHOT.jar app.jar

# Copiar o keystore SSL para o container
COPY /etc/letsencrypt/live/guiplanner.hopto.org/keystore.p12 /opt/config/keystore.p12

# Definir o arquivo de propriedades da aplicação com as configurações SSL
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=443
ENV SERVER_SSL_KEY_STORE=/opt/config/keystore.p12
ENV SERVER_SSL_KEY_STORE_PASSWORD=gui12345
ENV SERVER_SSL_KEY_STORE_TYPE=PKCS12
ENV SERVER_SSL_KEY_ALIAS=guiplanner.hopto.org

# Configurar o entrypoint da aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
