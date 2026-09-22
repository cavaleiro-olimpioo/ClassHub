package com.classhub.api.exception;

public final class ApiExceptions {
    private ApiExceptions() { }

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) { super(message); }
    }
    public static class ConflictException extends RuntimeException {
        public ConflictException(String message) { super(message); }
    }
    public static class BadRequestException extends RuntimeException {
        public BadRequestException(String message) { super(message); }
    }
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) { super(message); }
    }
    public static class BusinessRuleException extends RuntimeException {
        public BusinessRuleException(String message) { super(message); }
    }
}
