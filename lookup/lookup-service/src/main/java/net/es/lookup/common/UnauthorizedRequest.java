package net.es.lookup.common;

import net.es.lookup.common.ForbiddenRequestException;

public class UnauthorizedRequestException extends ForbiddenRequestException {
    public UnauthorizedRequestException(String message) {
        super(message);
    }
}