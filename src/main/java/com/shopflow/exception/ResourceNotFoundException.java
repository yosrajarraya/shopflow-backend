package com.shopflow.exception;

// Exception lancée quand une ressource n'est pas trouvée (404)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String ressource, Long id) {
        super(ressource + " avec l'id " + id + " n'existe pas");
    }
}
