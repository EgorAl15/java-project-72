# Анализатор страниц (Java)

[![hexlet-check](https://github.com/EgorAl15/java-project-72/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/EgorAl15/java-project-72/actions)
[![codecov](https://codecov.io/github/EgorAl15/java-project-72/graph/badge.svg?token=QIE8OF96O1)](https://codecov.io/github/EgorAl15/java-project-72)

Веб-приложение для анализа SEO-параметров веб-страниц.

Пользователь может добавить сайт, запустить его проверку и получить основные данные страницы:

- HTTP-код ответа;
- содержимое тега `<h1>`;
- содержимое тега `<title>`;
- значение `<meta name="description">`;
- дату проверки.

Результаты сохраняются в базе данных и отображаются в истории проверок для каждого сайта.

Учебный проект Хекслета: https://ru.hexlet.io/programs/java

Пример работы приложения: https://files.hexlet.app/a/f9wlja

## Демо

Приложение развернуто на Render:

https://java-project-72-j66h.onrender.com

## Возможности

- Добавление URL для анализа
- Нормализация URL перед сохранением
- Защита от добавления дубликатов
- Хранение сайтов в базе данных
- Проверка доступности сайта
- Получение HTTP-кода ответа
- SEO-анализ HTML с помощью Jsoup
- Извлечение `h1`, `title` и `description`
- Хранение истории проверок
- Отображение последней проверки в списке сайтов
- Сокращение длинных SEO-полей до 200 символов
- Flash-сообщения об успешных и неуспешных операциях
- Автоматические тесты без реальных сетевых запросов

## Стек

- Java 21
- Javalin
- JTE
- Gradle
- PostgreSQL
- H2
- HikariCP
- Unirest
- Jsoup
- Tailwind CSS
- JUnit 5
- AssertJ
- MockWebServer
- JaCoCo
- Codecov
- GitHub Actions
- Docker
- Render

## Установка

Клонируйте репозиторий:

```bash
git clone https://github.com/EgorAl15/java-project-72.git
cd java-project-72/app