package com.sonthai.schedulermanagement.util;


import com.sonthai.schedulermanagement.exception.DomainCode;
import com.sonthai.schedulermanagement.model.GeneralResponse;
import com.sonthai.schedulermanagement.model.ResponseStatus;

public class ResponseUtils {

    public static GeneralResponse<?> buildSuccessResponse(Object data){
        return new GeneralResponse<>()
                .setStatus(
                        new ResponseStatus(
                                DomainCode.SUCCESS.getCode(), DomainCode.SUCCESS.getValue()))
                .setData(data);
    }
}
