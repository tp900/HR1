package com.qr.hr.modles;

import java.io.Serializable;

public class Messages implements Serializable {
    public int status ;//1成功0失败
    public String msg;//返回消息

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}