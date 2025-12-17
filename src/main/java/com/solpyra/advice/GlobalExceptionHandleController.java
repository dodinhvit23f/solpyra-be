package com.solpyra.advice;

import com.solpyra.common.constant.Constant;
import com.solpyra.common.dto.response.Response;
import com.solpyra.exception.NotFoundException;
import jakarta.validation.ValidationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandleController {

  private final MessageSource messageSource;

  @ExceptionHandler({BadRequestException.class, ValidationException.class, IllegalArgumentException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public Response<Object> handleBadRequestException(Exception e) {
    return getObjectResponse(e.getMessage());
  }

  @ExceptionHandler({AuthenticationCredentialsNotFoundException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public Response<Object> handleUsernameNotFoundException(AuthenticationCredentialsNotFoundException e) {
    return getObjectResponse(e.getMessage());
  }

  @ExceptionHandler({NotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public Response<Object> handleNotFoundException(NotFoundException e) {
    return getObjectResponse(e.getMessage());
  }


  @ExceptionHandler({MethodArgumentNotValidException.class})
  public Response<String> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    Map<String, String> errors = getBindingErrors(e);
    Map<String, String> extraMessage = errors.entrySet()
            .stream()
                .collect(Collectors.toMap(Entry::getKey, entry -> getMessage(entry.getValue())));
    log.error("Validation Exception: {}", errors);

    return Response.<String>builder()
        .errorCodes(new HashSet<>(errors.values()))
        .extraMessage(extraMessage)
        .traceId(MDC.get(Constant.TRACE_ID))
        .build();
  }

  private Response<Object> getObjectResponse(String e) {
    return Response.<Object>builder()
        .errorCodes(Set.of(e))
        .extraMessage(Map.of(e, getMessage(e)))
        .traceId(MDC.get(Constant.TRACE_ID))
        .build();
  }

  private Map<String, String> getBindingErrors(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return errors;
  }

  private String getMessage(String message) {
    return  messageSource.getMessage(message, null, Locale.getDefault());
  }
}
