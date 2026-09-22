package com.classhub.api.exception;

import com.classhub.api.dto.ApiDtos.MessageResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiExceptions.NotFoundException.class)
    ResponseEntity<MessageResponse> notFound(ApiExceptions.NotFoundException ex) { return response(HttpStatus.NOT_FOUND, ex.getMessage()); }

    @ExceptionHandler(ApiExceptions.ConflictException.class)
    ResponseEntity<MessageResponse> conflict(ApiExceptions.ConflictException ex) { return response(HttpStatus.CONFLICT, ex.getMessage()); }

    @ExceptionHandler({ApiExceptions.BadRequestException.class, ConstraintViolationException.class})
    ResponseEntity<MessageResponse> badRequest(Exception ex) { return response(HttpStatus.BAD_REQUEST, ex.getMessage()); }

    @ExceptionHandler(ApiExceptions.UnauthorizedException.class)
    ResponseEntity<MessageResponse> unauthorized(ApiExceptions.UnauthorizedException ex) { return response(HttpStatus.UNAUTHORIZED, ex.getMessage()); }

    @ExceptionHandler(ApiExceptions.BusinessRuleException.class)
    ResponseEntity<MessageResponse> businessRule(ApiExceptions.BusinessRuleException ex) { return response(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage()); }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<MessageResponse> validation(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        return response(HttpStatus.BAD_REQUEST, error == null ? "Dados inválidos." : error.getField() + ": " + error.getDefaultMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<MessageResponse> unreadable(HttpMessageNotReadableException ex) {
        return response(HttpStatus.BAD_REQUEST, "JSON inválido ou campo em formato incorreto.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<MessageResponse> typeMismatch(MethodArgumentTypeMismatchException ex) {
        return response(HttpStatus.BAD_REQUEST, "Parâmetro '" + ex.getName() + "' possui formato inválido.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<MessageResponse> integrity(DataIntegrityViolationException ex) {
        return response(HttpStatus.CONFLICT, "O registro já existe ou possui dados relacionados.");
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<MessageResponse> unknown(Exception ex) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
    }

    private ResponseEntity<MessageResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new MessageResponse(message));
    }
}
