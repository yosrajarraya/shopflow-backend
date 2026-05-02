package com.shopflow.exception;

// Exception pour les erreurs métier (400)
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
