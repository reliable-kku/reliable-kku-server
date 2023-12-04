package com.deundeunhaku.reliablekkuserver.common.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(PaymentCancelException.class)
    public ResponseEntity<ErrorResponse> paymentCancelException(PaymentCancelException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> paymentException(PaymentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validException(MethodArgumentNotValidException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ErrorResponse> loginFailedException(LoginFailedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> illegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<ErrorResponse> fileSizeLimitExceededException(
            FileSizeLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("파일 크기는 최대 10MB 입니다."));
    }

    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ErrorResponse> notAuthorizedException(NotAuthorizedException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of(e.getMessage()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> expiredJwtException(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorResponse.of("토큰이 만료되었습니다."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        log.error("UnHandled Exception" + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.of("서버에서 에러가 발생했습니다."));
    }

}
