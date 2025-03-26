package ru.itmo.standalone_server.exceptions;

import lombok.Getter;

import javax.xml.ws.WebFault;

@WebFault(name = "PersonServiceFault")
@Getter
public class WebException extends Exception {
    private final ExceptionBean faultInfo;

    public WebException(String message, ExceptionBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public WebException(String message, ExceptionBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }
}
