package fiuba.tecnicas.msfiles.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import fiuba.tecnicas.msfiles.models.ServiceError;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ServiceError> handleException(RuntimeException re, WebRequest request) {
		re.printStackTrace();
		if (re.getMessage().matches("Token expired")) {
			return new ResponseEntity<ServiceError>(new ServiceError(HttpStatus.UNAUTHORIZED, re.getMessage()), HttpStatus.UNAUTHORIZED);
		}
        return new ResponseEntity<ServiceError>(new ServiceError(HttpStatus.FORBIDDEN, re.getMessage()), HttpStatus.FORBIDDEN);
	}
}
