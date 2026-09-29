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

/**
 * Tratador global de exceções da API (via {@code @RestControllerAdvice}).
 * <p>
 * Converte as exceções de negócio definidas em {@link ApiExceptions}, além
 * de exceções comuns do Spring (validação, JSON inválido, violação de
 * integridade do banco de dados etc.), em respostas HTTP padronizadas no
 * formato {@link MessageResponse}, evitando expor detalhes internos ao
 * cliente da API.
 */
@RestControllerAdvice
public class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    /**
     * Trata {@link ApiExceptions.NotFoundException}, retornando HTTP 404.
     *
     * @param ex exceção lançada
     * @return resposta HTTP 404 com a mensagem da exceção
     */
    @ExceptionHandler(ApiExceptions.NotFoundException.class)
    ResponseEntity<MessageResponse> notFound(ApiExceptions.NotFoundException ex) { return response(HttpStatus.NOT_FOUND, ex.getMessage()); }

    /**
     * Trata {@link ApiExceptions.ConflictException}, retornando HTTP 409.
     *
     * @param ex exceção lançada
     * @return resposta HTTP 409 com a mensagem da exceção
     */
    @ExceptionHandler(ApiExceptions.ConflictException.class)
    ResponseEntity<MessageResponse> conflict(ApiExceptions.ConflictException ex) { return response(HttpStatus.CONFLICT, ex.getMessage()); }

    /**
     * Trata {@link ApiExceptions.BadRequestException} e
     * {@link ConstraintViolationException}, retornando HTTP 400.
     *
     * @param ex exceção lançada
     * @return resposta HTTP 400 com a mensagem da exceção
     */
    @ExceptionHandler({ApiExceptions.BadRequestException.class, ConstraintViolationException.class})
    ResponseEntity<MessageResponse> badRequest(Exception ex) { return response(HttpStatus.BAD_REQUEST, ex.getMessage()); }

    /**
     * Trata {@link ApiExceptions.UnauthorizedException}, retornando HTTP 401.
     *
     * @param ex exceção lançada
     * @return resposta HTTP 401 com a mensagem da exceção
     */
    @ExceptionHandler(ApiExceptions.UnauthorizedException.class)
    ResponseEntity<MessageResponse> unauthorized(ApiExceptions.UnauthorizedException ex) { return response(HttpStatus.UNAUTHORIZED, ex.getMessage()); }

    /**
     * Trata {@link ApiExceptions.ForbiddenException}, retornando HTTP 403.
     *
     * @param ex exceção lançada
     * @return resposta HTTP 403 com a mensagem da exceção
     */
    @ExceptionHandler(ApiExceptions.ForbiddenException.class)
    ResponseEntity<MessageResponse> forbidden(ApiExceptions.ForbiddenException ex) { return response(HttpStatus.FORBIDDEN, ex.getMessage()); }

    /**
     * Trata {@link ApiExceptions.BusinessRuleException}, retornando HTTP 422 (Unprocessable Entity).
     *
     * @param ex exceção lançada
     * @return resposta HTTP 422 com a mensagem da exceção
     */
    @ExceptionHandler(ApiExceptions.BusinessRuleException.class)
    ResponseEntity<MessageResponse> businessRule(ApiExceptions.BusinessRuleException ex) { return response(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage()); }

    /**
     * Trata falhas de validação de campos anotados com Bean Validation
     * (ex.: {@code @NotBlank}, {@code @Email}), retornando HTTP 400 com o
     * primeiro erro de campo encontrado.
     *
     * @param ex exceção lançada pelo Spring durante a validação do corpo da requisição
     * @return resposta HTTP 400 indicando o campo e a mensagem de validação
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<MessageResponse> validation(MethodArgumentNotValidException ex) {
        FieldError error = ex.getBindingResult().getFieldError();
        return response(HttpStatus.BAD_REQUEST, error == null ? "Dados inválidos." : error.getField() + ": " + error.getDefaultMessage());
    }

    /**
     * Trata erros de leitura do corpo da requisição (JSON malformado ou
     * campo em formato incorreto), retornando HTTP 400.
     *
     * @param ex exceção lançada ao tentar ler o corpo da requisição
     * @return resposta HTTP 400 com mensagem genérica sobre o JSON inválido
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<MessageResponse> unreadable(HttpMessageNotReadableException ex) {
        return response(HttpStatus.BAD_REQUEST, "JSON inválido ou campo em formato incorreto.");
    }

    /**
     * Trata erros de conversão de tipo em parâmetros da requisição (ex.:
     * texto não numérico enviado para um parâmetro do tipo {@code Long}),
     * retornando HTTP 400.
     *
     * @param ex exceção lançada pelo Spring ao converter o parâmetro
     * @return resposta HTTP 400 indicando qual parâmetro possui formato inválido
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<MessageResponse> typeMismatch(MethodArgumentTypeMismatchException ex) {
        return response(HttpStatus.BAD_REQUEST, "Parâmetro '" + ex.getName() + "' possui formato inválido.");
    }

    /**
     * Trata violações de integridade no banco de dados (chave única, chave
     * estrangeira, coluna obrigatória ou restrição de verificação),
     * inspecionando o {@link SQLState} e a mensagem da causa raiz para
     * retornar uma mensagem amigável e o código HTTP mais adequado ao tipo
     * de violação.
     *
     * @param ex exceção de integridade lançada pelo Spring/JPA
     * @return resposta HTTP com status e mensagem apropriados ao tipo de violação encontrada
     */
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

    /**
     * Percorre a cadeia de causas da exceção em busca de uma {@link SQLException}.
     *
     * @param ex exceção original
     * @return a {@link SQLException} encontrada na cadeia de causas, ou {@code null} se não houver
     */
    private SQLException findSqlException(Throwable ex) {
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause instanceof SQLException sqlException) return sqlException;
        }
        return null;
    }

    /**
     * Concatena as mensagens de toda a cadeia de causas de uma exceção,
     * usada para inspecionar detalhes da falha de integridade do banco.
     *
     * @param ex exceção original
     * @return texto com todas as mensagens da cadeia de causas
     */
    private String exceptionDetails(Throwable ex) {
        StringBuilder details = new StringBuilder();
        for (Throwable cause = ex; cause != null; cause = cause.getCause()) {
            if (cause.getMessage() != null) details.append(' ').append(cause.getMessage());
        }
        return details.toString();
    }

    /**
     * Tratador de última instância para qualquer exceção não mapeada
     * explicitamente, retornando HTTP 500 com uma mensagem genérica (para
     * não expor detalhes internos ao cliente).
     *
     * @param ex exceção não tratada
     * @return resposta HTTP 500 com mensagem genérica
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<MessageResponse> unknown(Exception ex) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor.");
    }

    /**
     * Monta uma resposta HTTP padronizada contendo o status informado e o
     * corpo {@link MessageResponse} com a mensagem de erro.
     *
     * @param status código de status HTTP da resposta
     * @param message mensagem a ser retornada ao cliente
     * @return a resposta HTTP montada
     */
    private ResponseEntity<MessageResponse> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new MessageResponse(message));
    }
}
