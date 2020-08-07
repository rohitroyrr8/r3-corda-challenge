package com.template.webserver.models;

public class CordappResponse<T> {
    private boolean status;
    private String message;
    private T data;

    public CordappResponse() {

    }

    public CordappResponse(String message, T data, boolean status) {
        this.message = message;
        this.data = data;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public static <T> CordappResponse<T> success(){
        return new CordappResponse<>("SUCCESS", null, true);
    }

    public static <T> CordappResponse<T> success(T data){
        return new CordappResponse<>("SUCCESS", data, true);
    }

    public static <T> CordappResponse<T> error(String message){
        return new CordappResponse<>(message, null, false);
    }

    @Override
    public String toString() {
        return "CordappResponse{" +
                "message='" + message + '\'' +
                ", data=" + data +
                ", status=" + status +
                '}';
    }
}
