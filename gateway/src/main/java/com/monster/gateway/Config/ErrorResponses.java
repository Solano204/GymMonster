package com.monster.gateway.Config;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ErrorResponses {
    private int status;
    private String message;

    public ErrorResponses(int status, String message) {
        this.status = status;
        this.message = message;
    }

    // Getters and setters

    public static ErrorResponses of(int status, String message) {
        return new ErrorResponses(status, message);
    }
}