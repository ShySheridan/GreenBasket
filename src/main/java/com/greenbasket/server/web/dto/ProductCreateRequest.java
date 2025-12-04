/*
Объект входа (что клиент может прислать в API).

Что внутри:

Публичные поля или геттеры/сеттеры (Gson/Jackson-friendly).

Только то, что можно менять снаружи (не id, не системные поля).

Что изучить: Стабильность контрактов, обратная совместимость, семантика обязательных полей.

Что можно дописать:

Поля пагинации/сортировки в запросах поиска (лучше отдельные DTO)
 */

package com.greenbasket.dto;

public class ProductCreateRequest {
    public String name;
    public String description;
    public Integer priceCents;
}