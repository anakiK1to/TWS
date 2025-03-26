package ru.itmo.standalone_server.exceptions;

import lombok.Getter;

import javax.xml.ws.WebFault;

@WebFault(name = "PersonServiceFault")
@Getter
public class WebExceptionService {
    private final ExceptionBean faultInfo;

    public WebExceptionService(String message, ExceptionBean faultInfo) {
        super(message);
        this.faultInfo = faultInfo;


    }

    public WebExceptionService(String message, ExceptionBean faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;


    }
}
