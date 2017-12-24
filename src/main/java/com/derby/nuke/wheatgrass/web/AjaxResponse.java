package com.derby.nuke.wheatgrass.web;

public class AjaxResponse {
    private Boolean success;
    private String message;
    private Object data;

    public AjaxResponse() {
        this(Boolean.TRUE);
    }

    public AjaxResponse(Boolean success) {
        this(success, null);
    }

    public AjaxResponse(String message) {
        this(Boolean.TRUE, message);
    }

    public AjaxResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public AjaxResponse(Boolean success, String message, Object data) {
        this(success, message);
        this.data = data;
    }

    public static AjaxResponse fail() {
        return fail(null);
    }

    public static AjaxResponse fail(String message) {
        return new AjaxResponse(Boolean.FALSE, message);
    }

    public static AjaxResponse fail(String message, Object data) {
        return new AjaxResponse(Boolean.FALSE, message, data);
    }

    public static AjaxResponse success() {
        return success(null);
    }

    public static AjaxResponse success(String message) {
        return new AjaxResponse(Boolean.TRUE, message);
    }

    public static AjaxResponse success(String message, Object data) {
        return new AjaxResponse(Boolean.TRUE, message, data);
    }

    public static AjaxResponse successWithData(Object data) {
        return new AjaxResponse(Boolean.TRUE, null, data);
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
