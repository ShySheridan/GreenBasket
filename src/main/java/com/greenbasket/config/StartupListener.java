package com.greenbasket.config;

import jakarta.servlet.*;


//Точка инициализации приложения при старте контейнера (Tomcat/Jetty).
//Создаёт AppContext и кладёт сервисы в ServletContext, чтобы сервлеты их забрали в init().

//Реализация ServletContextListener.
//В contextInitialized — new AppContext(), ctx.setAttribute("productService", …) и т.д.
//(Опционально) логирование старта/завершения.

//Что изучить: Servlet lifecycle (ServletContextListener, web.xml vs @WebListener), порядок инициализации фильтров/сервлетов.



//Что можно дописать:
//Проверку подключения к БД (health-check при старте).
//Миграции БД (вызов Flyway).
//Загрузку конфигурации из application.properties.


//public class StartupListener implements ServletContextListener {
//    @Override
//    public void contextInitialized(ServletContextEvent appStart) {
//        // appStart=sce: событие запуска, пришло от контейнера, из него можно достать глобальный контекст приложения
//        // это конверт с уведомлением “ресторан открылся”. Из конверта можно достать ключ от шкафчика.
//
//        AppContext serviceRegistry = new AppContext();
//        // serviceRegistry=app: из него получаем готовые сервисы (productService(), categoryService()),
//        // которые потом кладем в ServletContext. ящик с инуструментами к шкафчику.
//
//        ServletContext applicationScope = appStart.getServletContext();
//        // applicationScope=ctx: глобальное хранилище приложения,
//        // переменная, в которую кладем глобальный контекст. ссылка на ServletContext
//
//        // Шкафчик = ServletContext
//
//        applicationScope.setAttribute("productService", serviceRegistry.productService());
//        applicationScope.setAttribute("categoryService", serviceRegistry.categoryService());
//        // Кладём в контекст два объекта-сервиса под ключами "productService" и "categoryService".
//        // Эти объекты становятся доступны всем сервлетам в приложении.
//    }
//}
