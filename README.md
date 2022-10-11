# Filmorate

![Films API Tests](https://github.com/agluh/java-filmorate/actions/workflows/api-tests.yml/badge.svg)

Кинопоиск для своих. Мини социальная сеть, которая поможет выбрать кино на основе того, 
какие фильмы вы и ваши друзья смотрите и какие оценки им ставите.

## Стек
- Java 11
- Spring Boot 2.7.0
- H2 2.1
- Maven сборка

## API

После запуска приложения Swagger документация будет доступна по адресу
```http://localhost:8080/swagger-ui/index.html```

## Сборка и развёртывание
Требуется установленный Apache Maven

```
git clone git@github.com:agluh/java-filmorate.git
cd java-filmorate
mvn spring-boot:run
```

