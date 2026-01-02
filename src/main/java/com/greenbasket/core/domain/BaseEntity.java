package com.greenbasket.core.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.management.InstanceAlreadyExistsException;


// в Main.java передаем реализацию интерфейса классу
//          IdGenerator idGenerator = new SimpleIdGenerator();
//
// Создаем продукт с передачей idGenerator
//        Product product = new Product(idGenerator, "Milk", 100);


@Getter
@ToString
@NoArgsConstructor(force = true)
//@SuperBuilder // Поддержка передачи параметров в родительский класс через idGenerator,
// Создание строителя для всех классов в иерархии

public abstract class BaseEntity {
    private Long id; // не final поле тк генерируется бд и должно быть присвоено только после сохранения
    // в базу данных, и оно не может быть установлено в момент создания объекта

// // базовая сущность не должна знать откуда берется айди, а только хранить его и защищать от второго присвоения
//    private IdGenerator idGenerator;
//
//    public BaseEntity(IdGenerator idGenerator) {
//        this.idGenerator = idGenerator; //
//    }

    public final void assignId(Long id) throws InstanceAlreadyExistsException { // не позволяем менять id
        // не запрашивает параметры, тк работает с полем своего класса
        if (this.id != null) // если ссылка != null значит id уже был задан
            throw new InstanceAlreadyExistsException("id already set");
        if (id == null) throw new IllegalArgumentException("id is null");
        this.id = id; // передаем реализацию интерфейса IdGenerator в конструктор базового класса
    }


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return this.id != null && this.id.equals(that.id);
    }


    @Override
    public final int hashCode() {
        // значение будет задаваться программой после сохранения
        return 52;
    }

}
