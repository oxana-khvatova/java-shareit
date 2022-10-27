# java-shareit
Сервис позволяющий совместное использование вещей.

## Возможности
Сервис обеспечивает пользователям возможность рассказывать, какими вещами они готовы поделиться,
находить нужную вещь и брать её в аренду на какое-то время.

Позволяет бронировать вещь на определённые даты и закрывает к ней доступ на время бронирования от других желающих.

На случай, если нужной вещи на сервисе нет, можно оставлять запросы.

## Архитектура
Приложение состоит из 2 сервисов:
- Основной сервис (размещение вещей, запросов, бронирований и пр.);
- Сервис-шлюз, в котором происходит валидация запросов.
