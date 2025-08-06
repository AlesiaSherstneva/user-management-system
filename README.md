# user-management-system

Микросервисная система управления пользователями, состоящая из:
- **auth-service** - сервиса регистрации, аутентификации и CRUD-операций;
- **notification-service** - сервиса отправки уведомлений администраторам об изменениях профилей
пользователей.

## Technology stack

| Компонент                | Технологии                                                                    | Роль в системе                             |
|--------------------------|-------------------------------------------------------------------------------|--------------------------------------------|
| **auth-service**         | Java 17, Spring (Boot, Web, Data JPA, Security), PostgreSQL, Liquibase, Maven | Управление пользователями, Kafka‑producer  |
| **notification‑service** | Java 17, Spring (Boot, Web, JDBC), PostgreSQL, Maven, Thymeleaf               | Kafka‑consumer, email‑уведомления          |
| **infrastructure**       | Docker, Docker Compose                                                        | Оркестрация контейнеров                    |
| **containers**           | PostgreSQL, Kafka, MailHog                                                    | База данных, брокер сообщений, SMTP-сервер |

**Kafka**:
- auth-service публикует события в топик `user-events`;
- notification-service подписан на этот топик.

**MailHog**:
- перехватывает письма в dev-среде;
- web-интерфейс: `http://localhost:8025`.

**Liquibase**:
- автоматически применяет миграции при старте auth-service.

## API endpoints

| Метод  | Эндпоинт         | Описание                    | Требуемая авторизация |       Роль        | Пример тела запроса                                                                                                     |
|--------|------------------|-----------------------------|:---------------------:|:-----------------:|-------------------------------------------------------------------------------------------------------------------------|
| POST   | `/auth/register` | Регистрация пользователя    |          Нет          |         —         | {<br/> "firstName": "Олег",<br/> "lastName": "Петров",<br/> "email": "test@mail.ru",<br/> "password": "qwerty123"<br/>} |
| POST   | `/auth/login`    | Вход и получение JWT-токена |          Нет          |    USER, ADMIN    | {<br/> "email":"test@mail.ru",<br/> "password":"qwerty123"<br/>}                                                        |
| GET    | `/users/me`      | Получить свой профиль       |       Да (JWT)        |       USER        | —                                                                                                                       |
| GET    | `/users/{id}`    | Получить профиль по id      |       Да (JWT)        | USER(self), ADMIN | —                                                                                                                       |
| PUT    | `/users/me`      | Изменить свой профиль       |       Да (JWT)        |       USER        | {<br/> "email":"new@mail.ru",<br/> "firstName":"Иван"<br/>}                                                             |
| PUT    | `/users/{id}`    | Изменить профиль по id      |       Да (JWT)        | USER(self), ADMIN | {<br/> "firstName":"Иван"<br/>}                                                                                         |
| DELETE | `/users/me`      | Удалить свой профиль        |       Да (JWT)        |       USER        | —                                                                                                                       |
| DELETE | `/users/{id}`    | Удалить профиль по id       |       Да (JWT)        | USER(self), ADMIN | —                                                                                                                       |

**USER**:
   - управляет только своим аккаунтом;
   - может видеть только свой профиль.

**ADMIN**
   - управляет всеми аккаунтами;
   - видит все профили;
   - получает email-уведомления о создании, изменении и удалении пользователей. 

## Requirements & Setup

Предварительные требования:
- Docker 20.10+
- Docker Compose 2.0+
- Java 17

Инструкция по запуску:
1. Склонировать репозиторий:
```bash
git clone https://github.com/AlesiaSherstneva/user-management-system.git
```
2. Войти в директорию проекта из командной строки/терминала:
```bash
cd user-management-system
```
3. Собрать и запустить сервисы:
```bash
docker compose up --build
```

После успешного запуска будут доступны:
- **auth-service**: `http://localhost:8080/api`
- **MailHog-UI**: `http://localhost:8025` (просмотр отправленных email)

## System interaction

1. Получить JWT-токен (2 способа).

Способ 1: регистрация нового пользователя.
```bash
curl -X POST 'http://localhost:8080/api/auth/register' \
-H 'Content-Type: application/json' \
-d '{
  "firstName": "Олег",
  "lastName": "Петров",
  "email": "test@mail.ru",
  "password": "qwerty123"
}'
```

Способ 2: авторизация существующего пользователя.
```bash
curl -X POST 'http://localhost:8080/api/auth/login' \
-H 'Content-Type: application/json' \
-d '{
  "email": "ivanov@example.com",
  "password": "ivanov123"
}'
```

Пользователи, зарегистрированные в системе:

| Email               | Пароль          | Роль  |
|---------------------|-----------------|:-----:|
| ivanov@example.com  | ivanov123       | USER  |
| petrova@example.com | petrova123      | USER  |
| sidorov@example.com | sidorov123      | USER  |
| admin1@example.com  | AdminPass123$   | ADMIN |
| admin2@example.com  | SecureAdmin456% | ADMIN |

Успешный ответ (для обоих способов):
```bash
{
    "role": "ROLE_USER",
    "bearerToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGVzaWFAb..."
}
```

2. Использование токена в запросах.

Нужно добавить полученный токен в заголовок `Authorization`.
```bash
curl -X GET 'http://localhost:8080/api/users/me' \
-H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGVzaWFAb...' \
-H 'Content-Type: application/json'
```

Токен валиден 24 часа.

3. Проверка уведомлений.

Для просмотра отправленных писем следует открыть интерфейс MailHog по адресу `http://localhost:8025`.