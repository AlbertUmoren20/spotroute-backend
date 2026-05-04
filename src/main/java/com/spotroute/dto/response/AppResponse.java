package com.spotroute.dto.response;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;

@Setter
@Getter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class AppResponse<T> implements Serializable {
    private String status;
    private String message;
    private T data;
    private String execTime ;
    @Builder.Default
    private Object error = new ArrayList<>();
}

