package com.greenbasket.config;
/*
Один вход для получения DataSource.

Что внутри:

create() — возвращает DataSource.

На старте можно DriverManager через прокси-реализацию.

        Лучше — HikariCP (pool: min/max, timeout).

Что изучить: JDBC DataSource, пулы соединений (HikariCP), параметры PostgreSQL (например, stringtype=unspecified).

Что можно дописать:

Конфигурирование из переменных окружения.

Метрики пула (JMX).

Валидацию соединения (test query).query*/
