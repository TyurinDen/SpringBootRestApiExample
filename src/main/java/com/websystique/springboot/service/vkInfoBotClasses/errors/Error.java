package com.websystique.springboot.service.vkInfoBotClasses.errors;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Error {
    @SerializedName("error_code")
    private int errorCode;

    @SerializedName("error_msg")
    private String errorMsg;

    @SerializedName("request_params")
    private List<RequestParameters> requestParams = new ArrayList<>();

    private class RequestParameters {
        private String key;
        private String value;

        // TODO: 30.05.2019 нужны ли геттеры и сеттеры???
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "{key = " + '\'' + key + '\'' + ", value = '" + value + '\'' + '}';
        }
    }

    // TODO: 30.05.2019 нужны ли геттеры и сеттеры???
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<RequestParameters> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(List<RequestParameters> requestParams) {
        this.requestParams = requestParams;
    }

    @Override
    public String toString() {
        return "Error {" +
                "Error code = " + errorCode +
                ", Error msg : '" + errorMsg + '\'' +
                ", Request params= " + requestParams +
                '}';
    }
}
