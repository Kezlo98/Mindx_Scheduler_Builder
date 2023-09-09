package com.sonthai.schedulermanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DomainCode {

    SUCCESS("0000", "Success"),
    INVALID_REQUEST("0001","Invalid Request: %s"),
    USERNAME_IS_EXISTED("0003", "Username is existed: %s"),
    USERNAME_IS_NOT_EXISTED("0004", "Username is not existed: %s, registrationId: %s"),
    TASK_IS_NOT_EXISTED("0005", "No task is existed: %s with assignee: %s"),
    TASK_IS_NOT_EXISTED_WITH_ASSIGNEE("0005", "Task is not existed, assignee: %s"),
    TASK_STATUS_IS_NOT_CORRECT("0006", "Task status is not correct, please double check again with task status: %s"),
    GENERAL_ERROR("9999", "Something's wrong. Please, try again."),
    ;

    private String code;

    private String value;

    public String valueAsString(Object[] args) {
        return String.format(String.format("%s", this.value), args);
    }
}
