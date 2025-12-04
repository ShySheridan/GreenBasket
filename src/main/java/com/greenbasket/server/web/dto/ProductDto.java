/*
Объект ответа (что возвращаем наружу).

Что внутри:

Публичные поля: id, name, description, priceCents, у категории — name, description.

Без внутренней/служебной инфы.

Что изучить: Версионирование API (v1/v2), минимизация утечек домена.

Что можно дописать:

Ссылки (imageUrl, self).

Вложенные ресурсы по необходимости (или expand-механизм).
 */
package com.greenbasket.dto;

public class ProductDto {
    public long id;
    public String name;
    public String description;
    public int priceCents;
}