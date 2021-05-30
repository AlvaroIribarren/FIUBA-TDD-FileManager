package fiuba.tecnicas.msfiles.models;

import org.springframework.http.HttpStatus;

public class ServiceError {
	private HttpStatus errorCode;
	private String message;
	private String status;
	
	public ServiceError(HttpStatus errorCode, String message) {
		this.errorCode = errorCode;
		this.message = message;
		this.status = "ERROR";
	}

	public HttpStatus getErrorCode() {
		return errorCode;
	}

	public void setStatus(HttpStatus errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getStatus() {
		return this.status;
	}
	
	public void setStatus(String status) {
		this.status = status; 
	}
}
