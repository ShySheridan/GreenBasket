package com.greenbasket.server.persistence.memory;

import com.greenbasket.core.domain.Comment;
import com.greenbasket.core.domain.Order;
import com.greenbasket.core.domain.Product;
import com.greenbasket.core.domain.User;
import com.greenbasket.core.repository.OrderInterface;

import java.util.List;
import java.util.Optional;

public class OrderInMemory implements OrderInterface {

    @Override
    public Order save(Order order) {
        return null;
    }

    @Override
    public void remove(Long id){

    }


    public Optional<Order> findById(Long id){
        return Optional.empty();
    }

    public List<Order> findAll(){
        return null;
    }

    public List<Order> findByUserId(Long userId){
        return null;
    }

    public List<Order> findByStatus(Order.OrderStatus status){
        return null;
    }
}
