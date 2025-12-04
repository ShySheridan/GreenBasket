/*
Разрешить фронтенду обращаться к API с другого домена.

Что внутри:

Заголовки: Access-Control-Allow-Origin, -Methods, -Headers, -Credentials.

Обработка OPTIONS (preflight) — вернуть 200 с заголовками.

Что изучить: CORS, preflight-запросы, безопасность cookies/JWT с CORS.

Что можно дописать:

Белый список доменов.

Тонкая настройка методов/заголовков.
 */
package com.greenbasket.web.filter;