package com.shopsphere.order_service.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopsphere.order_service.dto.ErrorResponseDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ObjectMapper mapper;

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        ErrorResponseDTO responseDTO = ErrorResponseDTO.builder()
                .status(ex.getStatusCode().toString())
                .message(ex.getReason())
                .path(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(responseDTO, ex.getStatusCode());
    }

    @ExceptionHandler(value = {FeignException.class})
    public ResponseEntity<ErrorResponseDTO> handleFeignClientException(final FeignException ex) throws JsonProcessingException {

        final JsonNode jsonNode = mapper.readTree(ex.contentUTF8());

        final ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.builder()
                .status(jsonNode.path("status").asText())
                .message(jsonNode.path("message").asText())
                .path(jsonNode.path("path").asText())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ResourceAlreadyExistException.class})
    public ResponseEntity<ErrorResponseDTO> handleResourceAlreadyExistException(final ResourceAlreadyExistException ex,
                                                                                final WebRequest request) {
        final ErrorResponseDTO responseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.CONFLICT.name())
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final HashMap<String, String> errorMap = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error ->
                errorMap.put(((FieldError) error).getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler(value = {ResourceNotFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(final ResourceNotFoundException ex,
                                                                            final WebRequest request) {
        final ErrorResponseDTO responseDTO = ErrorResponseDTO.builder()
                .status(HttpStatus.NOT_FOUND.name())
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
    }
}
