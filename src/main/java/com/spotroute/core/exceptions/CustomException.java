package com.spotroute.core.exceptions;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@NoArgsConstructor
@Setter
@Getter
public class CustomException extends RuntimeException{
    protected String message;
    protected HttpStatus status;

    public CustomException(String message) {
        super(message);
        this.message = message;
    }

    public CustomException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}
