package com.shopsphere.order_service.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    protected final String resourceName;

    protected final String fieldName;

    protected final String value;

    public ResourceNotFoundException(String resourceName, String fieldName, String value) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, value));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.value = value;
    }

}
