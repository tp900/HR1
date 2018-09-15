package com.qr.hr.modles;

public class Leave {
    public int id ;//假单序号
    public String leaveType;//假单类别，调休或请假
    public String empName;//员工姓名
    public String empNo;//员工工号
    public String dept;//员工部门
    public String postion;//职位
    public String sDate;//开始时间
    public String eDate;//结束时间
    public String approveEmpNo;//审核人工号
    public String remark;//备注

    public int getId() {
        return id;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public String getEmpName() {
        return empName;
    }

    public String getEmpNo() {
        return empNo;
    }

    public String getDept() {
        return dept;
    }

    public String getPostion() {
        return postion;
    }

    public String getsDate() {
        return sDate;
    }

    public String geteDate() {
        return eDate;
    }

    public String getApproveEmpNo() {
        return approveEmpNo;
    }

    public String getRemark() {
        return remark;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setPostion(String postion) {
        this.postion = postion;
    }

    public void setsDate(String sDate) {
        this.sDate = sDate;
    }

    public void seteDate(String eDate) {
        this.eDate = eDate;
    }

    public void setApproveEmpNo(String approveEmpNo) {
        this.approveEmpNo = approveEmpNo;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
