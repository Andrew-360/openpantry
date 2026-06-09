package com.communityPantry.communityPantry.exception;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class ApiError {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String code;
    private Map<String, List<String>> validationErrors; // for validation errors, can be null

    public ApiError(int status, String error, String message, String path, String code) {
        this.timestamp = Instant.now().toString();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.code = code;
    }

    public ApiError(int status, String error, String message, String path, String code,
            Map<String, List<String>> validationErrors) {
        this(status, error, message, path, code);
        this.validationErrors = validationErrors;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getCode() {
        return code;
    }

    public Map<String, List<String>> getValidationErrors() {
        return validationErrors;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setValidationErrors(Map<String, List<String>> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
