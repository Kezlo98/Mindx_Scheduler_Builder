package com.sonthai.schedulermanagement.exception;

import lombok.Data;

@Data
public class DomainException extends RuntimeException{

    private DomainCode domainCode;

    private Object[] args;

    public DomainException(DomainCode domainCode) {
        super(domainCode.valueAsString(null));
        this.domainCode = domainCode;
    }

    public DomainException(DomainCode domainCode, Object[] args) {
        super(domainCode.valueAsString(args));
        this.domainCode = domainCode;
        this.args = args;
    }

    public DomainException(String message) {
        super(message);
    }
}
