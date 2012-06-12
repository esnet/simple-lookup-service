package net.es.lookup.common;

import net.es.lookup.common.NotFoundException;

public class ServiceNotFoundException extends NotFoundException {
    public ServiceNotFoundException(String message) {
        super(message);
    }
}
