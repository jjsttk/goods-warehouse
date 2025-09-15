# Goods Warehouse

Spring Boot приложение для управления складом товаров с REST API.

## Функциональность

- CRUD операции для товаров
- Валидация данных
- Документация API (Swagger/OpenAPI)
- Поддержка разных профилей (dev, prod)
- Реализованы планировщики (simple, optimized). Планировщики способны увеличивать
цену всех продуктов через определенное время в настройках.
- Реализованы аспекты для логирования времени выполнения методов, а так же методов помеченных аннотацией @Transactional(Spring)
- Docker контейнеризация
- Подробный отчет об ошибках с указанием поля/полей, которые по каким то причинам не прошли валидацию
- Интеграция с PostgreSQL
- Использованы скрипты миграции Liquibase

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
# Значения из "[]" заменить на свои.
# Пример: {[PORT] -> 5432, [PASSWORD] -> password.}

docker run -p 8080:8080 \
-e SPRING_PROFILES_ACTIVE=prod \
-e DATABASE_URL=jdbc:postgresql://[HOST]:[PORT]/[DATABASE]?user=[USERNAME]&password=[PASSWORD]" \
goods-warehouse
````

## Конфигурация

### Профили Spring Boot
- dev - профиль разработки с H2 базой (по умолчанию)
- prod - продакшн профиль с PostgreSQL

### Переменные окружения


| Переменная                                        | Описание                                                                                   | Пример                                                                                             | Обязательная                        |
|---------------------------------------------------|--------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------|-------------------------------------|
| `SPRING_PROFILES_ACTIVE`                          | Активный профиль Spring Boot                                                               | `prod`, `dev`                                                                                      | Нет (default: `dev`)                |
| `DATABASE_URL`                                    | URL подключения к базе данных                                                              | `jdbc:postgresql://jdbc:postgresql://[HOST]:[PORT]/[DATABASE]?user=[USERNAME]&password=[PASSWORD]` | Да (для профиля prod)               |
| `APP_MAPPER_TYPE`                                 | Выбор активного маппера для entity <-> dto преобразований                                  | `mapstruct`, `conversion-service`                                                                  | Да                                  |
| `APP_ASPECT_TRANSACTIONAL_MEASURE_EXECUTION_TIME` | Использовать ли подсчет времени выполнения методов помеченных аннотацией @Transactional    | `true`, `false`                                                                                    | Нет                                 |
| `APP_SCHEDULING_ENABLED`                          | Использовать ли планировщик для повышения цены                                             | `true`, `false`                                                                                    | Нет                                 |
| `APP_SCHEDULING_PERIOD`                           | Период времени, спустя которое будет выполнено новое повышение цен в миллисекундах         | `60000`                                                                                            | Да (при использовании планировщика) |
| `APP_SCHEDULING_PRICE_INCREASE_PERCENTAGE`        | Процент на который будет увеличена текущая цена каждого продукта                           | `10`                                                                                               | Да (при использовании планировщика) |
| `APP_SCHEDULING_OPTIMIZATION_ENABLED`             | Использовать ли оптимизированный планировщик вместо обычного                               | `true`, `false`                                                                                    | Нет (рекомендуется: `true`)         |
| `APP_SCHEDULING_OPTIMIZATION_USE_EXCLUSIVE_LOCK`  | Использовать ли эксклюзивную блокировку базы на время работы планировщика повышающего цену | `true`, `false`                                                                                    | Нет (рекомендуется: `true`)         |


## Настройка базы данных
### dev профиль (H2)
- spring.datasource.url=jdbc:h2:mem:test
- spring.datasource.username=sa
- spring.datasource.password=password
- spring.jpa.hibernate.ddl-auto=validate
- spring.jpa.show-sql=true
- spring.h2.console.enabled=true
- spring.h2.console.path=/h2-console

### prod профиль (PostgreSQL)
- spring.datasource.url=${DATABASE_URL}
- spring.jpa.hibernate.ddl-auto=validate
- spring.jpa.show-sql=false

## API Документация

### Swagger/OpenAPI
После запуска приложения доступна интерактивная документация:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI spec: http://localhost:8080/v3/api-docs

## Тестирование
### Запуск тестов
Для тестов по дефолту используется база данных H2.
````
# Все тесты
./gradlew test
````
### Тестовая коллекция Postman
В папке docs/api/postman/ находится коллекция для тестирования API.

Импортируйте goods-warehouse-api.postman_collection.json в Postman.

