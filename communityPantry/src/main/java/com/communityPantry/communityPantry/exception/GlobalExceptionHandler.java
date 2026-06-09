package com.communityPantry.communityPantry.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // logger to log exceptions that didnt get handled by the specific exception
    // handlers
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /*
     * Business exceptions: these are exceptions that are thrown when there is a
     * problem with the business logic
     * so it will have custom exceptions e.g. UserAlreadyExistsException when a user
     * tries to register with a username that already exists in the database
     */

    // handles the case when a user tries to register with a username that already
    // exists in the database
    // returns a 409 Conflict status code with a message indicating that the
    // username is already taken
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleUserAlreadyExists(
            UserAlreadyExistsException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), "USER_ALREADY_EXISTS");
    }

    // handles the case when an entity is not found in the database
    // returns a 404 Not Found status code with a message indicating that the entity
    // was not found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI(), "ENTITY_NOT_FOUND");
    }

    // handles the case when there is not enough quantity of a food item available
    // returns a 400 Bad Request status code with a message indicating that the
    // quantity is insufficient
    @ExceptionHandler(InsufficientQuantityException.class)
    public ResponseEntity<ApiError> handleInsufficientQuantity(
            InsufficientQuantityException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), "INSUFFICIENT_QUANTITY");
    }

    // handles the case when a food item has already expired
    // returns a 400 Bad Request status code with a message indicating that the
    // food is expired
    @ExceptionHandler(AlreadyExpiredException.class)
    public ResponseEntity<ApiError> handleAlreadyExpired(
            AlreadyExpiredException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), "ALREADY_EXPIRED");
    }

    // handles the case when a food item has already been reserved
    // returns a 409 Conflict status code with a message indicating that the
    // food is already reserved
    @ExceptionHandler(AlreadyReservedException.class)
    public ResponseEntity<ApiError> handleAlreadyReserved(
            AlreadyReservedException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), "ALREADY_RESERVED");
    }

    // handles the case when an entity has1 an invalid status for the requested
    // action
    // returns a 409 Conflict status code with a message indicating that the
    // status is invalid
    @ExceptionHandler(InvalidStatusException.class)
    public ResponseEntity<ApiError> handleInvalidStatus(
            InvalidStatusException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), "INVALID_STATUS");
    }

    // handles the case when an entity already exists in the database
    // returns a 409 Conflict status code with a message indicating that the
    // entity already exists
    @ExceptionHandler(DuplicateEntityException.class)
    public ResponseEntity<ApiError> handleDuplicateEntity(
            DuplicateEntityException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request.getRequestURI(), "DUPLICATE_ENTITY");
    }

    // handles the case when a user tries to log in with invalid credentials
    // returns a 401 Unauthorized status code with a message indicating that the
    // credentials are invalid
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getRequestURI(), "INVALID_CREDENTIALS");
    }

    // handles the case when an illegal or inappropriate argument is passed to a
    // method
    // returns a 400 Bad Request status code with a message indicating that the
    // argument is invalid
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI(), "INVALID_ARGUMENT");
    }

    /*
     * Spring exceptions: these are exceptions that are thrown by Spring when there
     * is a problem with the request or the application context
     */

    // handles validation exceptions for invalid request bodies
    // returns a 400 Bad Request status code with a message indicating that the
    // validation failed and a map of the validation errors for each field
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, List<String>> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI(),
                "VALIDATION_ERROR",
                validationErrors);
        return ResponseEntity.badRequest().body(apiError);
    }

    // handle response status exceptions for invalid HTTP status codes
    // returns HTTP status code and message based on the exception, or 500 Internal
    // Server Error if the status code is invalid
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(
            ResponseStatusException ex,
            HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        if (status == null) {
            logger.error("Invalid HTTP status code {} at {}", ex.getStatusCode().value(), request.getRequestURI());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = ex.getReason() != null ? ex.getReason() : "Unexpected error";
        return buildResponse(status, message, request.getRequestURI(), "RESPONSE_STATUS_ERROR");
    }

    /*
     * Security exceptions: these are exceptions that are thrown when there is a
     * problem
     * with authentication or authorization
     * e.g. when a user tries to access a protected resource without being
     * authenticated or without having the required role/permission
     */

    // handle unauthorized access attempts to protected resources
    // returns a 403 Forbidden status code with a message indicating that the user
    // does not have permission to access the resource
    // 401 Unauthorized is handled by the AuthEntryPointJwt class, which sends a 401
    // response when an unauthenticated user tries to access a protected resource
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {
        logger.warn("Forbidden access attempt to {}", request.getRequestURI(), ex);
        return buildResponse(HttpStatus.FORBIDDEN, "You do not have permission to access this resource",
                request.getRequestURI(), "ACCESS_DENIED");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest request) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password", request.getRequestURI(),
                "INVALID_CREDENTIALS");
    }

    // fallback exception handler for any unhandled exceptions that may occur in the
    // application
    // returns a 500 Internal Server Error status code with a generic message
    // indicating that an unexpected error occurred
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(
            Exception ex,
            HttpServletRequest request) {
        logger.error("An unhandled error occurred at {}", request.getRequestURI(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request.getRequestURI(),
                "INTERNAL_ERROR");
    }

    /*
     * Helper methods
     */

    // helper method to build the response body for the exception handlers
    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String message, String path, String code) {
        ApiError apiError = new ApiError(
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                code);
        return ResponseEntity.status(status).body(apiError);
    }
}
