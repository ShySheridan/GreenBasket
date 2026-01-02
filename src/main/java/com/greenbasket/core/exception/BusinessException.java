package com.greenbasket.core.exception;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

// классы наследники OutOfStockException(productId, requested, available), CategoryNotEmptyException(categoryId),
// BucketAlreadyCheckedOutException, ProductInactiveException(productId)