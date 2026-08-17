# 1. Estágio de Build (Compilação)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Baixa as dependências e gera o arquivo .jar (ignorando testes para ser mais rápido)
RUN mvn clean package -DskipTests

# 2. Estágio de Execução (O servidor rodando)
FROM eclipse-temurin:21-jre
WORKDIR /app
# Pega o .jar gerado no passo anterior e joga para cá
COPY --from=build /app/target/*.jar app.jar
# Libera a porta padrão do Spring Boot
EXPOSE 8080
# Liga o servidor!
ENTRYPOINT ["java", "-jar", "app.jar"]