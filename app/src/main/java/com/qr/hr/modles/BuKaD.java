package com.qr.hr.modles;

public class BuKaD {
    public int id;
    public int buKaId;
    public String buKaType;//补卡类型 （上午、下午、加班、全天）
    public int fromHour;//从小时
    public int fromMinute;//从分
    public int toHour;//至时
    public int toMinute;//至分

    public int getId() {
        return id;
    }

    public int getBuKaId() {
        return buKaId;
    }

    public String getBuKaType() {
        return buKaType;
    }

    public int getFromHour() {
        return fromHour;
    }

    public int getFromMinute() {
        return fromMinute;
    }

    public int getToHour() {
        return toHour;
    }

    public int getToMinute() {
        return toMinute;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBuKaId(int buKaId) {
        this.buKaId = buKaId;
    }

    public void setBuKaType(String buKaType) {
        this.buKaType = buKaType;
    }

    public void setFromHour(int fromHour) {
        this.fromHour = fromHour;
    }

    public void setFromMinute(int fromMinute) {
        this.fromMinute = fromMinute;
    }

    public void setToHour(int toHour) {
        this.toHour = toHour;
    }

    public void setToMinute(int toMinute) {
        this.toMinute = toMinute;
    }
}
