package com.classhub.api.exception;

import com.classhub.api.exception.ApiExceptions.ForbiddenException;
import com.classhub.api.exception.ApiExceptions.NotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTests {
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void uniqueViolationIsConflictAndSpecific() {
        var response = handler.integrity(new DataIntegrityViolationException("duplicate", new SQLException("duplicate key", "23505")));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().mensagem()).isEqualTo("Já existe um registro com esses dados.");
    }

    @Test
    void foreignKeyViolationIsNotFound() {
        var response = handler.integrity(new DataIntegrityViolationException("foreign key", new SQLException("missing reference", "23503")));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().mensagem()).isEqualTo("Um registro relacionado não foi encontrado.");
    }

    @Test
    void foreignKeyRestrictionOnDeleteRemainsConflict() {
        var response = handler.integrity(new DataIntegrityViolationException(
            "foreign key", new SQLException("Key is still referenced from table api_ocorrencias", "23503")));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().mensagem()).contains("não pode ser removido");
    }

    @Test
    void notNullViolationIsUnprocessableEntity() {
        var response = handler.integrity(new DataIntegrityViolationException("not null", new SQLException("required", "23502")));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody().mensagem()).isEqualTo("Um campo obrigatório não foi informado.");
    }

    @Test
    void sharedHandlerStillMapsNotFoundAndForbidden() {
        assertThat(handler.notFound(new NotFoundException("missing")).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(handler.forbidden(new ForbiddenException("forbidden")).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
