package com.greenbasket.config;

import com.greenbasket.repository.jdbc.*;
import com.greenbasket.service.*;
import com.greenbasket.service.impl.*;
import javax.sql.DataSource;

//Зачем: Ручной “DI-контейнер”. Создаёт DataSource, репозитории, сервисы.
//
//Что внутри:
//
//Приватные поля: DataSource, ProductService, CategoryService.
//
//        Конструктор: DataSourceFactory.create(), создание Jdbc*Repository и *ServiceImpl.
//
//Геттеры для сервисов.
//
//Что изучить: Паттерны DI без фреймворков, жизненный цикл объектов, где хранить конфиги.
//
//Что можно дописать:
//
//Чтение конфигов (URL БД, логин/пароль) из env/файла.
//
//        Кэш singletons (если добавятся новые сервисы).
//
//Отложенную инициализацию тяжёлых компонентов.

//public class AppContext {
//    private final DataSource ds;
//    private final ProductService productService;
//    private final CategoryService categoryService;
//
//    public AppContext() {
//        this.ds = DataSourceFactory.create(); // простая фабрика
//        var prodRepo = new JdbcProductRepository(ds);
//        var catRepo  = new JdbcCategoryRepository(ds);
//        this.productService  = new ProductServiceImpl(prodRepo, catRepo);
//        this.categoryService = new CategoryServiceImpl(catRepo);
//    }
//
//    public ProductService productService()  { return productService; }
//    public CategoryService categoryService(){ return categoryService; }
//}
