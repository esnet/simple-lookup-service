package net.es.lookup.common;

import net.es.lookup.common.BadRequestException;

public class DuplicateKeyException extends BadRequestException {
    public DuplicateKeyException(String message) {
        super(message);
    }
}
