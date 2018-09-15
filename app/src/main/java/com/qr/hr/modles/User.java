package com.qr.hr.modles;

import java.io.Serializable;

public class User implements Serializable {
    public String empNo ;//工号
    public String pwd ;//用户密码
    public String empName;//姓名
    public String sex ;//性别
    public String dept ;//部门
    public String postion ;//岗位
    public String inDate ;//入职日期
    public String photo ;//照片

    public String getEmpNo() {
        return empNo;
    }

    public String getPwd() {
        return pwd;
    }

    public String getEmpName() {
        return empName;
    }

    public String getSex() {
        return sex;
    }

    public String getDept() {
        return dept;
    }

    public String getPostion() {
        return postion;
    }

    public String getInDate() {
        return inDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setPostion(String postion) {
        this.postion = postion;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
