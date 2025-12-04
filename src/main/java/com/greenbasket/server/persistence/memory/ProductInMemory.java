package com.greenbasket.server.persistence.memory;

import com.greenbasket.core.domain.Product;
import com.greenbasket.core.repository.ProductInterface;
import com.greenbasket.core.util.IdGenerator;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;

public class ProductInMemory implements ProductInterface {
    private final Map<Long, Product> storage = new HashMap<>();
    private final IdGenerator idGenerator;

    public ProductInMemory(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }


    @Override
    public Product save(Product product) throws InstanceAlreadyExistsException{
        if (product.getId() == null) {
            product.assignId(idGenerator.generateId());
        }
        storage.put(product.getId(), product);
        return product;
    }


    @Override
    public void remove(Long id){
        storage.remove(id);
    }


    @Override
    public Optional<Product> findByName(String name){
        if (name == null) {
            return null;
        }

        for (Product product : storage.values()) {
            if (name.equals(product.getName())) {
                return Optional.of(product);
            }
        }

        return Optional.empty();
    }


    @Override
    public Optional<Product> findById(Long id){
        if (storage.containsKey(id)){ return Optional.of(storage.get(id)); }
        return Optional.empty();
    }


    @Override
    public List<Product> findByCategoryId(Long categoryId){
        if (categoryId == null) {
            return new ArrayList<>();
        }

        List<Product> products = new ArrayList<>();

        for (Product product : storage.values()) {
            if (categoryId.equals(product.getCategory().getId())) {
                products.add(product);
            }
        }
        return products;
    }


    @Override
    public List<Product> findAll(){
        List<Product> products = new ArrayList<>();
        for (Product product : storage.values()) {
            products.add(product);
        }
        return products;
    }


    @Override
    public int countByCategoryId(Long categoryId){
//        if (categoryId == null) {
//            throw new IllegalArgumentException("айди категории не может быть пустым");
//        }

        List<Product> products = new ArrayList<>();

        for (Product product : storage.values()) {
            if (categoryId.equals(product.getCategory().getId())) {
                products.add(product);
            }
        }
        return products.size();
    }


    @Override
    public boolean isProductExistsByFields(String name, String brand){
        return findAll().stream().anyMatch(p ->
                p.getName().equalsIgnoreCase(name) &&
                        p.getBrand().equalsIgnoreCase(brand));
    }

}
