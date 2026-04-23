# k8s deployment (Helm + Helmfile)

## Структура

```
k8s/
  chat-service/           # Helm chart
  notification-service/   # Helm chart
  server-service/         # Helm chart
  user-service/           # Helm chart
  voice-service/          # Helm chart

  ingress-chart/          # Gateway (nginx)

  helmfile.yaml           # точка входа для деплоя
  README.md
```

---

## Запуск

### 1. Запустить внешние зависимости (docker)

Postgres, Kafka, Redis и др. должны быть запущены локально (docker-compose).

---

### 2. Запустить minikube

```
minikube start
```

---

### 3. Деплой в k8s

```
helmfile apply
```

---

## Архитектура
* `ingress-chart` — принимает весь трафик
* gateway:

    * роутит запросы
    * проверяет JWT (кроме user-service)
    * прокидывает `X-User-Id`

---

## Конфигурация

* каждый сервис имеет:

    * `values.yaml` — базовые настройки
    * `values-dev.yaml` — dev окружение

* env делятся на:

    * ConfigMap — обычные переменные
    * Secret — пароли / ключи

---

## Внешние сервисы

Из k8s доступ к docker:

```
host.minikube.internal
```

Примеры:

* Postgres → `host.minikube.internal:5432`
* Kafka → `host.minikube.internal:9092`

---

## 💡 Примечание

* сервисы общаются между собой по DNS:

```
http://user-service:8081
```

---
