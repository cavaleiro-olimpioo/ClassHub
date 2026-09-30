package com.classhub.api.exception;

/**
 * Classe utilitária que reúne todas as exceções de negócio customizadas da
 * API. Cada exceção é tratada por {@link ApiExceptionHandler} e convertida
 * em uma resposta HTTP com o código de status apropriado.
 */
public final class ApiExceptions {
    /** Construtor privado: classe utilitária, não deve ser instanciada. */
    private ApiExceptions() { }

    /** Lançada quando um recurso solicitado não é encontrado (resulta em HTTP 404). */
    public static class NotFoundException extends RuntimeException {
        /**
         * Cria a exceção com a mensagem informada.
         *
         * @param message mensagem descrevendo o recurso não encontrado
         */
        public NotFoundException(String message) { super(message); }
    }
    /** Lançada quando uma operação conflita com o estado atual dos dados (resulta em HTTP 409). */
    public static class ConflictException extends RuntimeException {
        /**
         * Cria a exceção com a mensagem informada.
         *
         * @param message mensagem descrevendo o conflito
         */
        public ConflictException(String message) { super(message); }
    }
    /** Lançada quando os dados informados na requisição são inválidos (resulta em HTTP 400). */
    public static class BadRequestException extends RuntimeException {
        /**
         * Cria a exceção com a mensagem informada.
         *
         * @param message mensagem descrevendo o problema na requisição
         */
        public BadRequestException(String message) { super(message); }
    }
    /** Lançada quando a autenticação falha, ex.: credenciais inválidas (resulta em HTTP 401). */
    public static class UnauthorizedException extends RuntimeException {
        /**
         * Cria a exceção com a mensagem informada.
         *
         * @param message mensagem descrevendo a falha de autenticação
         */
        public UnauthorizedException(String message) { super(message); }
    }
    /** Lançada quando o usuário autenticado não tem permissão para a operação (resulta em HTTP 403). */
    public static class ForbiddenException extends RuntimeException {
        /**
         * Cria a exceção com a mensagem informada.
         *
         * @param message mensagem descrevendo a restrição de acesso
         */
        public ForbiddenException(String message) { super(message); }
    }
    /** Lançada quando uma regra de negócio impede a conclusão da operação (resulta em HTTP 422). */
    public static class BusinessRuleException extends RuntimeException {
        /**
         * Cria a exceção com a mensagem informada.
         *
         * @param message mensagem descrevendo a regra de negócio violada
         */
        public BusinessRuleException(String message) { super(message); }
    }
}
