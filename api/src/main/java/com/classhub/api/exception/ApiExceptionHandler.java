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
import java.sql.SQLException;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    @ExceptionHandler(ApiExceptions.NotFoundException.class)
    ResponseEntity<MessageResponse> notFound(ApiExceptions.NotFoundException ex) { return response(HttpStatus.NOT_FOUND, ex.getMessage()); }

    @ExceptionHandler(ApiExceptions.ConflictException.class)
    ResponseEntity<MessageResponse> conflict(ApiExceptions.ConflictException ex) { return response(HttpStatus.CONFLICT, ex.getMessage()); }

    @ExceptionHandler({ApiExceptions.BadRequestException.class, ConstraintViolationException.class})
    ResponseEntity<MessageResponse> badRequest(Exception ex) { return response(HttpStatus.BAD_REQUEST, ex.getMessage()); }

    @ExceptionHandler(ApiExceptions.UnauthorizedException.class)
    ResponseEntity<MessageResponse> unauthorized(ApiExceptions.UnauthorizedException ex) { return response(HttpStatus.UNAUTHORIZED, ex.getMessage()); }

    @ExceptionHandler(ApiExceptions.ForbiddenException.class)
    ResponseEntity<MessageResponse> forbidden(ApiExceptions.ForbiddenException ex) { return response(HttpStatus.FORBIDDEN, ex.getMessage()); }

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
        SQLException sqlException = findSqlException(ex);
        String sqlState = sqlException == null ? null : sqlException.getSQLState();
        String details = exceptionDetails(ex).toLowerCase(Locale.ROOT);
        log.error("Falha de integridade no banco (SQLState={}): {}", sqlState, ex.getMessage(), ex);

        if ("23505".equals(sqlState) || details.contains("unique") || details.contains("duplicate")) {
            return response(HttpStatus.CONFLICT, "Já existe um registro com esses dados.");
        }
        if ("23503".equals(sqlState) || details.contains("foreign key") || details.contains("fk_")) {
            if (details.contains("still referenced") || details.contains("referenced from table") || details.contains("child record found")) {
                return response(HttpStatus.CONFLICT, "O registro não pode ser removido porque possui dados relacionados.");
            }
            return response(HttpStatus.NOT_FOUND, "Um registro relacionado não foi encontrado.");
        }
        if ("23502".equals(sqlState) || details.contains("not-null") || details.contains("not null")) {
            return response(HttpStatus.UNPROCESSABLE_ENTITY, "Um campo obrigatório não foi informado.");
        }
        if ("23514".equals(sqlState) || details.contains("check constraint")) {
            return response(HttpStatus.UNPROCESSABLE_ENTITY, "Os dados não atendem às regras do cadastro.");
        }
        return response(HttpStatus.CONFLICT, "Não foi possível salvar devido a uma restrição de integridade dos dados.");
    }

    private SQLException findSqlException(Throwable ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sqlException) return sqlException;
        }
        return null;
    }

    private String exceptionDetails(Throwable ex) {
        StringBuilder details = new StringBuilder();
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause.getMessage() != null) details.append(' ').append(cause.getMessage());
        }
        return details.toString();
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<MessageResponse> unknown(Exception ex) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
    }

    private ResponseEntity<MessageResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new MessageResponse(message));
    }
}
