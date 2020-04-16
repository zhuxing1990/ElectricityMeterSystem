package com.vunke.electricity.modle;

/**
 * Created by zhuxi on 2019/11/6.
 */

public class MeterQueryBean {
    private String meterNo;
    private String collectorId;
    private double beginCheckNum;
    private double beginCheckNum2;
    private String beginCheckCode;
    private int    tableType;

    public String getMeterNo() {
        return meterNo;
    }

    public void setMeterNo(String meterNo) {
        this.meterNo = meterNo;
    }

    public String getCollectorId() {
        return collectorId;
    }

    public void setCollectorId(String collectorId) {
        this.collectorId = collectorId;
    }

    public double getBeginCheckNum() {
        return beginCheckNum;
    }

    public void setBeginCheckNum(double beginCheckNum) {
        this.beginCheckNum = beginCheckNum;
    }

    public double getBeginCheckNum2() {
        return beginCheckNum2;
    }

    public void setBeginCheckNum2(double beginCheckNum2) {
        this.beginCheckNum2 = beginCheckNum2;
    }

    public String getBeginCheckCode() {
        return beginCheckCode;
    }

    public void setBeginCheckCode(String beginCheckCode) {
        this.beginCheckCode = beginCheckCode;
    }

    public int getTableType() {
        return tableType;
    }

    public void setTableType(int tableType) {
        this.tableType = tableType;
    }

    @Override
    public String toString() {
        return "MeterQueryBean{" +
                "meterNo='" + meterNo + '\'' +
                ", collectorId='" + collectorId + '\'' +
                ", beginCheckNum='" + beginCheckNum + '\'' +
                ", beginCheckNum2='" + beginCheckNum2 + '\'' +
                ", beginCheckCode='" + beginCheckCode + '\'' +
                ", tableType=" + tableType +
                '}';
    }
}
