package org.konata.udpsender.entity;

import org.json.JSONObject;

public class ApiReturn {
    private Integer retcode;
    private String message;
    private JSONObject data;

    public Integer getRetcode() {
        return retcode;
    }

    public void setRetcode(Integer retcode) {
        this.retcode = retcode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiReturn{" +
                "retcode=" + retcode +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
