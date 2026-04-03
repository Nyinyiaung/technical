package com.technical.commonutil;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.technical.config.MessageConfig;
import com.technical.dto.common.ErrorResponse;
import com.technical.dto.common.ExceptionConfig;
import com.technical.dto.common.SuccessResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class MasterCodeBase {
    @Autowired
    protected MessageConfig messageConfig;
    @Autowired
    protected ObjectMapper objectMapper;

    protected <T> ResponseEntity<SuccessResponse> successResponse(String messageKey, T data, HttpStatus status) {
        return new ResponseEntity<>(
                SuccessResponse.builder()
                        .message(messageConfig.getMessage(messageKey))
                        .data(data)
                        .build(),
                status
        );
    }

    protected ResponseEntity<List<ErrorResponse>> createErrorResponse(ExceptionConfig config, Exception e) {
        List<ErrorResponse> responses = new ArrayList<>();
        responses.add(new ErrorResponse(config.code(), messageConfig.getMessage(config.messageKey()), e.getMessage()));

        if (config.logException()) {
            log.error("Error Response: {}", responses, e);
        } else {
            log.info("Validation Failed Response: {}", responses);
        }
        return new ResponseEntity<>(responses, new HttpHeaders(), config.status());
    }

    protected Object createErrorResponse(String message, String path, String errorCode, HttpStatus status) throws IOException {
        Map<String, Object> errorResponse = Map.of(
                "code", errorCode,
                "message", message,
                "path", path
        );

        HttpServletResponse response = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getResponse();

        if (response != null) {
            response.setStatus(status.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
        return null;
    }
}
