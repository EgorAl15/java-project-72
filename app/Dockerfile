FROM node:24-alpine AS styles

WORKDIR /app

COPY package.json package-lock.json ./

RUN npm ci

COPY src/main/resources/application.css ./src/main/resources/application.css
COPY src/main/resources/templates ./src/main/resources/templates

RUN mkdir -p ./src/main/resources/static \
    && npx @tailwindcss/cli \
    -i ./src/main/resources/application.css \
    -o ./src/main/resources/static/application.css


FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY . .

COPY --from=styles \
    /app/src/main/resources/static/application.css \
    ./src/main/resources/static/application.css

RUN chmod +x gradlew
RUN ./gradlew shadowJar --no-daemon


FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/build/libs/app-1.0-SNAPSHOT-all.jar app.jar

CMD ["java", "-jar", "app.jar"]