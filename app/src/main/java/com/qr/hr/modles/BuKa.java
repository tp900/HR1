package com.qr.hr.modles;

import java.util.List;

public class BuKa {
    public int id;
    public String empNo;
    public String buKaDate;
    public String reason;//补卡原因
    public List<BuKaD> buKaDetail;//补卡明细
    public String status;//状态（待审核、已批准、未批准）
    public String approve;//审核人

    public int getId() {
        return id;
    }

    public String getEmpNo() {
        return empNo;
    }

    public String getBuKaDate() {
        return buKaDate;
    }

    public String getReason() {
        return reason;
    }

    public List<BuKaD> getBuKaDetail() {
        return buKaDetail;
    }

    public String getStatus() {
        return status;
    }

    public String getApprove() {
        return approve;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public void setBuKaDate(String buKaDate) {
        this.buKaDate = buKaDate;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setBuKaDetail(List<BuKaD> buKaDetail) {
        this.buKaDetail = buKaDetail;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setApprove(String approve) {
        this.approve = approve;
    }
}
