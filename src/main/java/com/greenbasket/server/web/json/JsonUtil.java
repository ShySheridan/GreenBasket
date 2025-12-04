/*
Централизованный JSON read/write на Gson.

Что внутри:

readJson(req, Class<T>) — читает body, маппит в объект.

writeJson(resp, obj) — ставит Content-Type и сериализует.

Что изучить: Gson (или Jackson), кодировки, ошибки парсинга.

Что можно дописать:

Настройку Gson (LocalDateTime, null policy, pretty).

Единый формат дат/денег.

Ограничение размера тела запроса (безопасность).
 */
package com.greenbasket.web.json;