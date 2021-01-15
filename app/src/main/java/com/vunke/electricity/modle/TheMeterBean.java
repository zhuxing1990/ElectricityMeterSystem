package com.vunke.electricity.modle;

import java.util.List;

/**
 * Created by zhuxi on 2019/9/16.
 */

public class TheMeterBean {


    /**
     * bizBody : [{"beginCheckNum":245,"checkDate":"2019-09-09T20:43:17.000+0000","collectorId":"06:1D:F2:86:E8:B7","comPort":"S3","endCheckNum":325,"meterId":1,"meterNo":"001600054792","roomLevel":"1","userId":1},{"beginCheckNum":485,"checkDate":"2019-09-17T20:18:41.000+0000","collectorId":"06:1D:F2:86:E8:B7","comPort":"S4","endCheckNum":759,"meterId":2,"meterNo":"001600054791","roomLevel":"1","userId":1}]
     * respCode : 2000
     * respMsg : success
     */

    private int respCode;
    private String respMsg;
    /**
     * beginCheckNum : 245
     * checkDate : 2019-09-09T20:43:17.000+0000
     * collectorId : 06:1D:F2:86:E8:B7
     * comPort : S3
     * endCheckNum : 325
     * meterId : 1
     * meterNo : 001600054792
     * roomLevel : 1
     * userId : 1
     */

    private List<BizBodyBean> bizBody;

    public int getRespCode() {
        return respCode;
    }

    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }

    public List<BizBodyBean> getBizBody() {
        return bizBody;
    }

    public void setBizBody(List<BizBodyBean> bizBody) {
        this.bizBody = bizBody;
    }

    public static class BizBodyBean {
        private double beginCheckNum;
        private String checkDate;
        private String collectorId;
        private String comPort;
        private double endCheckNum;
        private double endCheckNumTwo;
        private Long meterId;
        private String meterNo;
        private String roomLevel;
        private Long userId;
        private Long magnification;
        private String brand;

        public Double getBeginCheckNum() {
            return beginCheckNum;
        }
        public void setBeginCheckNum(double beginCheckNum) {
            this.beginCheckNum = beginCheckNum;
        }

        public double getEndCheckNumTwo() {
            return endCheckNumTwo;
        }

        public void setEndCheckNumTwo(double endCheckNumTwo) {
            this.endCheckNumTwo = endCheckNumTwo;
        }

        public String getCheckDate() {
            return checkDate;
        }

        public void setCheckDate(String checkDate) {
            this.checkDate = checkDate;
        }

        public String getCollectorId() {
            return collectorId;
        }

        public void setCollectorId(String collectorId) {
            this.collectorId = collectorId;
        }

        public String getComPort() {
            return comPort;
        }

        public void setComPort(String comPort) {
            this.comPort = comPort;
        }

        public double getEndCheckNum() {
            return endCheckNum;
        }

        public void setEndCheckNum(double endCheckNum) {
            this.endCheckNum = endCheckNum;
        }

        public Long getMeterId() {
            return meterId;
        }

        public void setMeterId(Long meterId) {
            this.meterId = meterId;
        }

        public String getMeterNo() {
            return meterNo;
        }

        public void setMeterNo(String meterNo) {
            this.meterNo = meterNo;
        }

        public String getRoomLevel() {
            return roomLevel;
        }

        public void setRoomLevel(String roomLevel) {
            this.roomLevel = roomLevel;
        }

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getMagnification() {
            return magnification;
        }

        public void setMagnification(Long magnification) {
            this.magnification = magnification;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }

        @Override
        public String toString() {
            return "BizBodyBean{" +
                    "beginCheckNum=" + beginCheckNum +
                    ", checkDate='" + checkDate + '\'' +
                    ", collectorId='" + collectorId + '\'' +
                    ", comPort='" + comPort + '\'' +
                    ", endCheckNum=" + endCheckNum +
                    ", endCheckNumTwo=" + endCheckNumTwo +
                    ", meterId=" + meterId +
                    ", meterNo='" + meterNo + '\'' +
                    ", roomLevel='" + roomLevel + '\'' +
                    ", userId=" + userId +
                    ", magnification=" + magnification +
                    ", brand='" + brand + '\'' +
                    '}';
        }
}

    @Override
    public String toString() {
        return "TheMeterBean{" +
                "respCode=" + respCode +
                ", respMsg='" + respMsg + '\'' +
                ", bizBody=" + bizBody +
                '}';
    }
}
