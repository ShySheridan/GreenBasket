/*
HTTP-слой для продуктов: список/поиск/получение/создание/обновление/удаление.

Что внутри:

В init() забирает ProductService из ServletContext.

doGet:

без pathInfo → список (?query=&page=&size=).

с /id → один продукт.

doPost → чтение ProductCreateRequest из JSON, валидация в сервисе, 201.

doPut → обновление по id.

doDelete → удаление по id.

Что изучить: Парсинг pathInfo, queryString, правильные коды ответов (201/204), Content-Type.

Что можно дописать:

Пагинация + заголовок X-Total-Count.

Сортировка по whitelist полей.

ETag/If-None-Match для кэширования GET.
 */
package com.greenbasket.web.servlet;