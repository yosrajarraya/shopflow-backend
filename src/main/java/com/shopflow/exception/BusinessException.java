package com.shopflow.exception;

//Cette classe sert à gérer les erreurs de logique métier 400
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
