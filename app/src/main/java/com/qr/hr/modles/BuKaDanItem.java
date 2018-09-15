package com.qr.hr.modles;

public class BuKaDanItem {
    public int id;
    public String status;
    public String am;
    public String pm;
    public String jb;
    public String reason;
    public String approve;
    public String bkdate;

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public String getAm() {
        return am;
    }

    public String getPm() {
        return pm;
    }

    public String getJb() {
        return jb;
    }

    public String getReason() {
        return reason;
    }

    public String getApprove() {
        return approve;
    }

    public String getBkdate() {
        return bkdate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAm(String am) {
        this.am = am;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public void setJb(String jb) {
        this.jb = jb;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }

    public void setBkdate(String bkdate) {
        this.bkdate = bkdate;
    }
}
