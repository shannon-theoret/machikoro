package com.shannontheoret.machikoro.utilities;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseUtil {
    public static ResponseEntity<Object> errorResponse(String message, HttpStatus status) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("message", message);
        return new ResponseEntity<>(errorBody, status);
    }

    public static ResponseEntity<Object> errorResponse(HttpStatus status) {
        return errorResponse("An internal error occurred.", status);
    }
}
