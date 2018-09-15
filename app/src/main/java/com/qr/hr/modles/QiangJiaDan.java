package com.qr.hr.modles;

public class QiangJiaDan {
    public int id;
    public String empNo;
    public String empName;
    public String empDept;
    public String empPostion;
    public String sDate;
    public String eDate;
    public double days;
    public double hours;
    public String remark;
    public String approveEmpNo;
    public String status;


    public int getId() {
        return id;
    }

    public String getEmpNo() {
        return empNo;
    }

    public String getEmpName() {
        return empName;
    }

    public String getEmpDept() {
        return empDept;
    }

    public String getEmpPostion() {
        return empPostion;
    }

    public String getsDate() {
        return sDate;
    }

    public String geteDate() {
        return eDate;
    }

    public double getDays() {
        return days;
    }

    public double getHours() {
        return hours;
    }

    public String getRemark() {
        return remark;
    }

    public String getApproveEmpNo() {
        return approveEmpNo;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public void setEmpDept(String empDept) {
        this.empDept = empDept;
    }

    public void setEmpPostion(String empPostion) {
        this.empPostion = empPostion;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }

    public void setDays(double days) {
        this.days = days;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setApproveEmpNo(String approveEmpNo) {
        this.approveEmpNo = approveEmpNo;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
