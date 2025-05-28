package com.shopsphere.order_service.exceptions;

public class ResourceAlreadyExistException extends RuntimeException {

    final String resourceName;

    final String fieldName;

    final String fieldValue;

    public ResourceAlreadyExistException(final String resourceName, final String fieldName, final String fieldValue) {
        super(String.format("%s with %s: '%s' already exists.", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
