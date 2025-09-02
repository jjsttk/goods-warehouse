# Goods Warehouse

Spring Boot приложение для управления складом товаров с REST API.

## Функциональность

- CRUD операции для товаров
- Валидация данных
- Документация API (Swagger/OpenAPI)
- Поддержка разных профилей (dev/prod)
- Docker контейнеризация
- Интеграция с PostgreSQL

## Быстрый старт

### Предварительные требования

- Java 21+
- Gradle 8+
- Docker (опционально)
- PostgreSQL (для prod профиля)

### Быстрый старт с профилем dev и базой h2 для локальной разработки и тестирования:
````
git clone -b [branch name] --single-branch https://github.com/jjsttk/goods-warehouse && cd goods-warehouse && ./gradlew bootRun
````
### 1. Клонирование репозитория

````
git clone -b [branch name] --single-branch https://github.com/jjsttk/goods-warehouse
cd goods-warehouse
````

### 2. Запуск в режиме разработки (с H2 базой)
````
# Сборка проекта
./gradlew clean build

# Запуск приложения
./gradlew bootRun
````

Приложение будет доступно по адресу: http://localhost:8080

### 3. Доступные endpoints

- API: http://localhost:8080/api/v1/products
- Swagger UI: http://localhost:8080/swagger-ui.html 
- OpenAPI: http://localhost:8080/v3/api-docs 
- H2 Console: http://localhost:8080/h2-console (только dev профиль)

### 4. Запуск через Docker с указанием ссылки на запущенную бд
````
# Сборка Docker образа
docker build -t goods-warehouse .
````
````
# Запуск контейнера
# Переменные заменить своими значениями.

docker run -p 8080:8080 \
-e SPRING_PROFILES_ACTIVE=prod \
-e DATABASE_URL="jdbc:postgresql://[HOST]:[PORT]/[DATABASE]?user=[USERNAME]&password=[PASSWORD]" \
goods-warehouse
````

## Конфигурация

### Профили Spring Boot
- dev - профиль разработки с H2 базой (по умолчанию)
- prod - продакшн профиль с PostgreSQL

### Переменные окружения


| Переменная | Описание                                                  | Пример                                                                                             | Обязательная                     |
|---|-----------------------------------------------------------|----------------------------------------------------------------------------------------------------|----------------------------------|
| `SPRING_PROFILES_ACTIVE` | Активный профиль Spring Boot                              | `prod`, `dev`                                                                                      | Нет (default: `dev`)               |
| `DATABASE_URL` | URL подключения к базе данных                             | `jdbc:postgresql://jdbc:postgresql://[HOST]:[PORT]/[DATABASE]?user=[USERNAME]&password=[PASSWORD]` | Да (для профиля prod)            |
| `APP_MAPPER_TYPE` | Выбор активного маппера для entity <-> dto преобразований | `mapstruct`, `conversion-service`                                                                   | Нет (default: `conversion-service` |


## Настройка базы данных
### dev профиль (H2)
- spring.datasource.url=jdbc:h2:mem:test
- spring.datasource.username=sa
- spring.datasource.password=password
- spring.jpa.hibernate.ddl-auto=create-drop
- spring.jpa.show-sql=true
- spring.h2.console.enabled=true
- spring.h2.console.path=/h2-console

### prod профиль (PostgreSQL)
- spring.datasource.url=${DATABASE_URL}
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.show-sql=false

## API Документация

### Swagger/OpenAPI
После запуска приложения доступна интерактивная документация:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI spec: http://localhost:8080/v3/api-docs

## Тестирование
### Запуск тестов
````
# Все тесты
./gradlew test
````
### Тестовая коллекция Postman
В папке docs/api/postman/ находится коллекция для тестирования API.

Импортируйте goods-warehouse-api.postman_collection.json в Postman.

