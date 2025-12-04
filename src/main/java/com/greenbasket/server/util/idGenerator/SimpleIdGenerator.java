package com.greenbasket.server.util.idGenerator;

import com.greenbasket.core.util.IdGenerator;

public class SimpleIdGenerator implements IdGenerator {
    private Long currentId = 0L;

    public SimpleIdGenerator() {}

    @Override
    public Long generateId() {
        return ++currentId; // 1, 2, 3, 4, ...
    }

}
